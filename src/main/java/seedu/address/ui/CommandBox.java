package seedu.address.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
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
    private static final String ERROR_HINT_STYLE_CLASS = "error-hint";
    private static final String FXML = "CommandBox.fxml";

    // Company mode: command -> parameter template
    private static final Map<String, String> COMPANY_COMMANDS = new LinkedHashMap<>();
    // Delivery mode: command -> parameter template
    private static final Map<String, String> DELIVERY_COMMANDS = new LinkedHashMap<>();
    // Full MESSAGE_USAGE strings shown in error hint (company-side)
    private static final Map<String, String> COMPANY_USAGE = new LinkedHashMap<>();
    // Full MESSAGE_USAGE strings shown in error hint (delivery-side)
    private static final Map<String, String> DELIVERY_USAGE = new LinkedHashMap<>();

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

        // Company-side full usage strings
        COMPANY_USAGE.put("add",
                seedu.address.logic.commands.companycommands.AddCommand.MESSAGE_USAGE);
        COMPANY_USAGE.put("edit",
                seedu.address.logic.commands.companycommands.EditCommand.MESSAGE_USAGE);
        COMPANY_USAGE.put("delete",
                seedu.address.logic.commands.companycommands.DeleteCommand.MESSAGE_USAGE);
        COMPANY_USAGE.put("filter",
                seedu.address.logic.commands.companycommands.FilterCommand.MESSAGE_USAGE);
        COMPANY_USAGE.put("set",
                seedu.address.logic.commands.SetCommand.MESSAGE_USAGE);

        // Delivery-side full usage strings
        DELIVERY_USAGE.put("add",
                seedu.address.logic.commands.deliverycommands.AddCommand.MESSAGE_USAGE);
        DELIVERY_USAGE.put("edit",
                seedu.address.logic.commands.deliverycommands.EditCommand.MESSAGE_USAGE);
        DELIVERY_USAGE.put("delete",
                seedu.address.logic.commands.deliverycommands.DeleteCommand.MESSAGE_USAGE);
        DELIVERY_USAGE.put("filter",
                seedu.address.logic.commands.deliverycommands.FilterCommand.MESSAGE_USAGE);
        DELIVERY_USAGE.put("select",
                seedu.address.logic.commands.deliverycommands.SelectCommand.MESSAGE_USAGE);
        DELIVERY_USAGE.put("sort",
                seedu.address.logic.commands.deliverycommands.SortCommand.MESSAGE_USAGE);
        DELIVERY_USAGE.put("mark",
                seedu.address.logic.commands.deliverycommands.MarkCommand.MESSAGE_USAGE);
        DELIVERY_USAGE.put("unmark",
                seedu.address.logic.commands.deliverycommands.UnmarkCommand.MESSAGE_USAGE);
        DELIVERY_USAGE.put("set",
                seedu.address.logic.commands.SetCommand.MESSAGE_USAGE);
    }

    private final CommandExecutor commandExecutor;
    private final Model model;

    @FXML private TextField commandTextField;
    @FXML private Label promptLabel;
    @FXML private Label hintLabel;

    /**
     * Creates a {@code CommandBox}.
     */
    public CommandBox(CommandExecutor commandExecutor, Model model) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.model = model;

        commandTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            setStyleToDefault();
            refreshHint(newVal);
        });

        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);

        refreshHint("");
    }

    /** Clears the input field. Called on tab switch. */
    public void clear() {
        commandTextField.setText("");
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

    // ── Key handling ──────────────────────────────────────────────────────────

    private void onKeyPressed(KeyEvent e) {
        if (e.getCode() == javafx.scene.input.KeyCode.TAB) {
            tabComplete();
            e.consume();
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
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
            String word = commandText.trim().split("\\s+", 2)[0].toLowerCase();
            Map<String, String> usageMap = model.getCompanyPackage() ? COMPANY_USAGE : DELIVERY_USAGE;
            if (e instanceof ParseException && usageMap.containsKey(word)) {
                setHintToErrorUsage(usageMap.get(word));
            } else {
                hintLabel.setText("");
                hintLabel.getStyleClass().remove(ERROR_HINT_STYLE_CLASS);
            }
        }
    }

    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
        hintLabel.getStyleClass().remove(ERROR_HINT_STYLE_CLASS);
        refreshHint(commandTextField.getText());
    }

    private void setHintToErrorUsage(String usage) {
        hintLabel.setText(usage);
        ObservableList<String> styleClass = hintLabel.getStyleClass();
        if (!styleClass.contains(ERROR_HINT_STYLE_CLASS)) {
            styleClass.add(ERROR_HINT_STYLE_CLASS);
        }
    }

    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();
        if (!styleClass.contains(ERROR_STYLE_CLASS)) {
            styleClass.add(ERROR_STYLE_CLASS);
        }
    }

    /** Represents a function that can execute commands. */
    @FunctionalInterface
    public interface CommandExecutor {
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }
}
