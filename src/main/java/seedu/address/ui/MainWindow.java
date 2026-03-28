package seedu.address.ui;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private final Logic logic;
    private final Model model;

    // Independent Ui parts residing in this Ui container
    private CompanyListPanel companyListPanel;
    private DeliveryListPanel deliveryListPanel;
    private RoutePanel routePanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML private StackPane commandBoxPlaceholder;
    @FXML private MenuItem helpMenuItem;
    @FXML private TabPane listTabPane;
    @FXML private StackPane companyListPanelPlaceholder;
    @FXML private StackPane deliveryListPanelPlaceholder;
    @FXML private StackPane routePanelPlaceholder;
    @FXML private StackPane resultDisplayPlaceholder;
    @FXML private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic, Model model) {
        super(FXML, primaryStage);
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.model = model;

        setWindowDefaultSize(logic.getGuiSettings());
        configureListTabs();
        setAccelerators();
        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        companyListPanel = new CompanyListPanel(model.getFilteredCompanyList());
        companyListPanelPlaceholder.getChildren().add(companyListPanel.getRoot());

        deliveryListPanel = new DeliveryListPanel(model.getFilteredDeliveryList());
        deliveryListPanelPlaceholder.getChildren().add(deliveryListPanel.getRoot());

        // RoutePanel owns all routing logic — MainWindow just hosts it
        routePanel = new RoutePanel(model.getFilteredDeliveryList(), model);
        routePanelPlaceholder.getChildren().add(routePanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        syncSelectedTabWithMode();
    }

    private void configureListTabs() {
        listTabPane.getSelectionModel().selectedIndexProperty().addListener((unused, oldValue, newValue) -> {
            if (newValue == null) return;
            model.setCompanyPackage(newValue.intValue() == 0);
        });
        syncSelectedTabWithMode();
    }

    private void syncSelectedTabWithMode() {
        listTabPane.getSelectionModel().select(model.getCompanyPackage() ? 0 : 1);
    }

    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public CompanyListPanel getCompanyListPanel() {
        return companyListPanel;
    }

    public DeliveryListPanel getDeliveryListPanel() {
        return deliveryListPanel;
    }

    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            if (commandResult.isShowHelp()) handleHelp();
            if (commandResult.isExit()) handleExit();
            syncSelectedTabWithMode();
            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
