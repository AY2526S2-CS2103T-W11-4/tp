package seedu.address.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;

/**
 * The UI component that is responsible for receiving user command inputs.
 * Features a hint line showing command format and a popup autocomplete dropdown.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    // Company mode: command -> parameter template
    private static final Map<String, String> COMPANY_COMMANDS = new LinkedHashMap<>();
    // Delivery mode: command -> parameter template
    private static final Map<String, String> DELIVERY_COMMANDS = new LinkedHashMap<>();
    // Descriptions shown in autocomplete dropdown
    private static final Map<String, String> DESCRIPTIONS = new LinkedHashMap<>();

    static {
        // Shared commands
        COMPANY_COMMANDS.put("add", "n/NAME p/PHONE e/EMAIL a/ADDRESS [t/TAG]...");
        COMPANY_COMMANDS.put("edit", "INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]...");
        COMPANY_COMMANDS.put("delete", "INDEX");
        COMPANY_COMMANDS.put("find", "KEYWORD [MORE_KEYWORDS]...");
        COMPANY_COMMANDS.put("list", "");
        COMPANY_COMMANDS.put("clear", "");
        COMPANY_COMMANDS.put("switch", "");
        COMPANY_COMMANDS.put("set", "a/ADDRESS");
        COMPANY_COMMANDS.put("help", "");
        COMPANY_COMMANDS.put("exit", "");

        DELIVERY_COMMANDS.put("add", "pr/PRODUCT c/COMPANY dl/DEADLINE a/ADDRESS [t/TAG]...");
        DELIVERY_COMMANDS.put("edit", "INDEX [pr/PRODUCT] [c/COMPANY] [dl/DEADLINE] [a/ADDRESS] [t/TAG]...");
        DELIVERY_COMMANDS.put("delete", "INDEX");
        DELIVERY_COMMANDS.put("mark", "INDEX");
        DELIVERY_COMMANDS.put("unmark", "INDEX");
        DELIVERY_COMMANDS.put("select", "INDEX [INDEX]... | none");
        DELIVERY_COMMANDS.put("sort", "c/COMPANY");
        DELIVERY_COMMANDS.put("route", "");
        DELIVERY_COMMANDS.put("find", "KEYWORD [MORE_KEYWORDS]...");
        DELIVERY_COMMANDS.put("list", "");
        DELIVERY_COMMANDS.put("clear", "");
        DELIVERY_COMMANDS.put("switch", "");
        DELIVERY_COMMANDS.put("set", "a/ADDRESS");
        DELIVERY_COMMANDS.put("help", "");
        DELIVERY_COMMANDS.put("exit", "");

        DESCRIPTIONS.put("add", "Add a new entry");
        DESCRIPTIONS.put("edit", "Edit an existing entry");
        DESCRIPTIONS.put("delete", "Remove an entry by index");
        DESCRIPTIONS.put("find", "Search entries by keyword");
        DESCRIPTIONS.put("list", "Show all entries");
        DESCRIPTIONS.put("clear", "Delete all entries");
        DESCRIPTIONS.put("switch", "Switch between company / delivery view");
        DESCRIPTIONS.put("set", "Set your delivery origin address");
        DESCRIPTIONS.put("help", "Open the help window");
        DESCRIPTIONS.put("exit", "Save and exit");
        DESCRIPTIONS.put("mark", "Mark delivery as completed");
        DESCRIPTIONS.put("unmark", "Remove completed status");
        DESCRIPTIONS.put("select", "Select deliveries for route planning");
        DESCRIPTIONS.put("sort", "Sort deliveries by company deadline");
        DESCRIPTIONS.put("route", "Plan route for selected deliveries");
    }

    private final CommandExecutor commandExecutor;
    private final Model model;

    @FXML private TextField commandTextField;
    @FXML private Label promptLabel;
    @FXML private Label hintLabel;

    // Popup autocomplete
    private final Popup autocompletePopup = new Popup();
    private final ListView<String> suggestionListView = new ListView<>();

    /**
     * Creates a {@code CommandBox}.
     */
    public CommandBox(CommandExecutor commandExecutor, Model model) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.model = model;

        setupAutocompletePopup();

        commandTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            setStyleToDefault();
            refreshHint(newVal);
            refreshSuggestions(newVal);
        });

        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);

        // Close popup when field loses focus
        commandTextField.focusedProperty().addListener((obs, was, now) -> {
            if (!now) {
                autocompletePopup.hide();
            }
        });

        refreshHint("");
    }

    /** Clears the input field. Called on tab switch. */
    public void clear() {
        commandTextField.setText("");
        autocompletePopup.hide();
    }

    // ── Internal setup ────────────────────────────────────────────────────────

    private void setupAutocompletePopup() {
        suggestionListView.getStyleClass().add("autocomplete-list");
        suggestionListView.setCellFactory(lv -> new SuggestionCell());
        suggestionListView.setPrefWidth(340);
        suggestionListView.setFixedCellSize(36);
        suggestionListView.setOnMouseClicked(e -> applySuggestion());

        // Wrap in a styled container
        javafx.scene.layout.VBox popupBox = new javafx.scene.layout.VBox(suggestionListView);
        popupBox.getStyleClass().add("autocomplete-popup");
        popupBox.getStylesheets().add(
                getClass().getResource("/view/DarkTheme.css").toExternalForm());
        popupBox.getStylesheets().add(
                getClass().getResource("/view/Extensions.css").toExternalForm());

        autocompletePopup.getContent().add(popupBox);
        autocompletePopup.setAutoHide(true);
        autocompletePopup.setConsumeAutoHidingEvents(false);
    }

    private Map<String, String> currentCommands() {
        return model.getCompanyPackage() ? COMPANY_COMMANDS : DELIVERY_COMMANDS;
    }

    // ── Hint line ─────────────────────────────────────────────────────────────

    private void refreshHint(String input) {
        if (input == null || input.isEmpty()) {
            hintLabel.setText("");
            return;
        }

        Map<String, String> cmds = currentCommands();
        String[] parts = input.trim().split("\\s+", 2);
        String word = parts[0];

        if (cmds.containsKey(word)) {
            String params = cmds.get(word);
            hintLabel.setText(params.isEmpty() ? "" : word + " " + params);
        } else {
            // Partial match — show best guess
            List<String> matches = cmds.keySet().stream()
                    .filter(c -> c.startsWith(word))
                    .collect(Collectors.toList());
            if (matches.size() == 1) {
                String m = matches.get(0);
                String params = cmds.get(m);
                hintLabel.setText(params.isEmpty() ? m : m + " " + params);
            } else {
                hintLabel.setText("");
            }
        }
    }

    // ── Autocomplete popup ────────────────────────────────────────────────────

    private void refreshSuggestions(String input) {
        if (input == null || input.isEmpty() || input.contains(" ")) {
            autocompletePopup.hide();
            return;
        }

        String word = input.trim();
        Map<String, String> cmds = currentCommands();
        List<String> matches = cmds.keySet().stream()
                .filter(c -> c.startsWith(word) && !c.equals(word))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            autocompletePopup.hide();
            return;
        }

        suggestionListView.setItems(FXCollections.observableArrayList(matches));
        int rows = Math.min(matches.size(), 6);
        suggestionListView.setPrefHeight(rows * 36 + 8);

        if (!autocompletePopup.isShowing() && commandTextField.getScene() != null) {
            Point2D anchor = commandTextField.localToScreen(0, commandTextField.getHeight());
            if (anchor != null) {
                autocompletePopup.show(commandTextField, anchor.getX(), anchor.getY() + 2);
            }
        }
    }

    private void applySuggestion() {
        String sel = suggestionListView.getSelectionModel().getSelectedItem();
        if (sel == null && !suggestionListView.getItems().isEmpty()) {
            sel = suggestionListView.getItems().get(0);
        }
        if (sel != null) {
            commandTextField.setText(sel + " ");
            commandTextField.positionCaret(commandTextField.getText().length());
            autocompletePopup.hide();
            commandTextField.requestFocus();
        }
    }

    // ── Key handling ──────────────────────────────────────────────────────────

    private void onKeyPressed(KeyEvent e) {
        switch (e.getCode()) {
        case TAB:
            if (autocompletePopup.isShowing()) {
                applySuggestion();
            } else {
                tabComplete();
            }
            e.consume();
            break;
        case DOWN:
            if (autocompletePopup.isShowing()) {
                int idx = suggestionListView.getSelectionModel().getSelectedIndex();
                int max = suggestionListView.getItems().size() - 1;
                suggestionListView.getSelectionModel().select(idx < max ? idx + 1 : 0);
                e.consume();
            }
            break;
        case UP:
            if (autocompletePopup.isShowing()) {
                int idx = suggestionListView.getSelectionModel().getSelectedIndex();
                int max = suggestionListView.getItems().size() - 1;
                suggestionListView.getSelectionModel().select(idx > 0 ? idx - 1 : max);
                e.consume();
            }
            break;
        case ESCAPE:
            autocompletePopup.hide();
            e.consume();
            break;
        default:
            break;
        }
    }

    private void tabComplete() {
        String input = commandTextField.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        List<String> matches = currentCommands().keySet().stream()
                .filter(c -> c.startsWith(input))
                .collect(Collectors.toList());
        if (matches.size() == 1) {
            commandTextField.setText(matches.get(0) + " ");
            commandTextField.positionCaret(commandTextField.getText().length());
        }
    }

    // ── Command execution ─────────────────────────────────────────────────────

    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isBlank()) {
            return;
        }
        try {
            autocompletePopup.hide();
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();
        if (!styleClass.contains(ERROR_STYLE_CLASS)) {
            styleClass.add(ERROR_STYLE_CLASS);
        }
    }

    // ── Cell renderer ─────────────────────────────────────────────────────────

    private static final class SuggestionCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            HBox row = new HBox(12);
            row.setStyle("-fx-padding: 4 10 4 10; -fx-alignment: CENTER_LEFT;");

            Label cmd = new Label(item);
            cmd.getStyleClass().add("autocomplete-cmd");
            cmd.setMinWidth(80);

            Label desc = new Label(DESCRIPTIONS.getOrDefault(item, ""));
            desc.getStyleClass().add("autocomplete-desc");

            row.getChildren().addAll(cmd, desc);
            setGraphic(row);
        }
    }

    /** Represents a function that can execute commands. */
    @FunctionalInterface
    public interface CommandExecutor {
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }
}
