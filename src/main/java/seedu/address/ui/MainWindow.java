package seedu.address.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
 * a top bar with pill toggle navigation and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final int COMPANIES_TAB_INDEX = 0;
    private static final int DELIVERIES_TAB_INDEX = 1;
    private static final int ROUTES_TAB_INDEX = 2;

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
    private CommandBox commandBox;

    @FXML private StackPane commandBoxPlaceholder;
    @FXML private MenuItem helpMenuItem;
    @FXML private TabPane listTabPane;
    @FXML private StackPane companyListPanelPlaceholder;
    @FXML private StackPane deliveryListPanelPlaceholder;
    @FXML private Button mapSelectedDeliveriesButton;
    @FXML private StackPane routePanelPlaceholder;
    @FXML private StackPane resultDisplayPlaceholder;
    @FXML private StackPane statusbarPlaceholder;
    @FXML private Label userAddressLabel;

    // Pill toggle buttons
    @FXML private Button pillCompanies;
    @FXML private Button pillDeliveries;
    @FXML private Button pillRoutes;

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
        // Hide the TabPane's own header area — navigation is handled by the pill toggle
        listTabPane.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                javafx.scene.Node header = listTabPane.lookup(".tab-header-area");
                if (header != null) {
                    header.setManaged(false);
                    header.setVisible(false);
                }
            }
        });

        companyListPanel = new CompanyListPanel(model.getFilteredCompanyList());
        companyListPanelPlaceholder.getChildren().add(companyListPanel.getRoot());

        deliveryListPanel = new DeliveryListPanel(model.getFilteredDeliveryList(), model, this::updateMapButtonState);
        deliveryListPanelPlaceholder.getChildren().add(deliveryListPanel.getRoot());

        routePanel = new RoutePanel(model.getFilteredDeliveryList(), model);
        routePanelPlaceholder.getChildren().add(routePanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand, model);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        userAddressLabel.textProperty().bind(model.getUserAddress());

        syncSelectedTabWithMode();
        updatePillToggleState();
        updateMapButtonState();
    }

    private void configureListTabs() {
        listTabPane.getSelectionModel().selectedIndexProperty().addListener((unused, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            int idx = newValue.intValue();
            model.setCompanyPackage(idx == COMPANIES_TAB_INDEX);
            updatePillToggleState();

            // Clear command input on tab switch (commands are tab-dependent)
            if (commandBox != null) {
                commandBox.clear();
            }
        });
        syncSelectedTabWithMode();
    }

    private void syncSelectedTabWithMode() {
        listTabPane.getSelectionModel().select(model.getCompanyPackage() ? COMPANIES_TAB_INDEX : DELIVERIES_TAB_INDEX);
    }

    /**
     * Updates the pill toggle button styles to reflect the currently active tab.
     */
    private void updatePillToggleState() {
        int selectedIndex = listTabPane.getSelectionModel().getSelectedIndex();
        Button[] pills = { pillCompanies, pillDeliveries, pillRoutes };
        for (int i = 0; i < pills.length; i++) {
            if (pills[i] == null) {
                continue;
            }
            pills[i].getStyleClass().removeAll("pill-button", "pill-button-active");
            pills[i].getStyleClass().add(i == selectedIndex ? "pill-button-active" : "pill-button");
        }
    }

    @FXML
    private void handlePillCompanies() {
        listTabPane.getSelectionModel().select(COMPANIES_TAB_INDEX);
        model.setCompanyPackage(true);
    }

    @FXML
    private void handlePillDeliveries() {
        listTabPane.getSelectionModel().select(DELIVERIES_TAB_INDEX);
        model.setCompanyPackage(false);
    }

    @FXML
    private void handlePillRoutes() {
        listTabPane.getSelectionModel().select(ROUTES_TAB_INDEX);
    }

    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Creates help window.
     */
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

    @FXML
    private void handleShowSelectedRoutes() {
        List<seedu.address.model.delivery.Delivery> selectedDeliveries = model.getSelectedDeliveriesInDisplayOrder();
        if (selectedDeliveries.isEmpty()) {
            updateMapButtonState();
            return;
        }

        listTabPane.getSelectionModel().select(ROUTES_TAB_INDEX);
        Platform.runLater(() -> routePanel.planRoutesFor(selectedDeliveries));
    }

    private void updateMapButtonState() {
        if (mapSelectedDeliveriesButton != null) {
            mapSelectedDeliveriesButton.setDisable(model.getSelectedDeliveriesInDisplayOrder().isEmpty());
        }
    }

    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            resultDisplayPlaceholder.setVisible(true);
            resultDisplayPlaceholder.setManaged(true);
            if (commandResult.isShowHelp()) {
                handleHelp();
            }
            if (commandResult.isExit()) {
                handleExit();
            }
            if (!commandResult.getDeliveriesToRoute().isEmpty()) {
                listTabPane.getSelectionModel().select(ROUTES_TAB_INDEX);
                List<seedu.address.model.delivery.Delivery> toRoute =
                        new ArrayList<>(commandResult.getDeliveriesToRoute());
                Platform.runLater(() -> routePanel.planRoutesFor(toRoute));
            } else {
                syncSelectedTabWithMode();
            }
            updatePillToggleState();
            updateMapButtonState();
            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser("");
            resultDisplayPlaceholder.setVisible(false);
            resultDisplayPlaceholder.setManaged(false);
            throw e;
        }
    }
}
