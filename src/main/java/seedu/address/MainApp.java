package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.DeliveryBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;
import seedu.address.model.user.User;
import seedu.address.model.util.SampleData;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.AddressBookStorage;
import seedu.address.storage.DeliveryBookStorage;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonDeliveryBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.JsonUserStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.storage.UserStorage;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 3, 0, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing application ]===========================");
        super.init();


        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);

        AddressBookStorage addressBookStorage = new JsonAddressBookStorage(userPrefs.getAddressBookFilePath());
        DeliveryBookStorage deliveryBookStorage = new JsonDeliveryBookStorage(userPrefs.getDeliveryBookFilePath());
        UserStorage userStorage = new JsonUserStorage(userPrefs.getUserFilePath());

        storage = new StorageManager(addressBookStorage, deliveryBookStorage, userPrefsStorage, userStorage);

        model = initModelManager(storage, userPrefs);
        model.setCompanyPackage(true);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, model);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s books and {@code userPrefs}.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        Optional<ReadOnlyDeliveryBook> deliveryBookOptional;
        ReadOnlyAddressBook initialAddressData;
        ReadOnlyDeliveryBook initialDeliveryData;

        SampleData sampleData = SampleDataUtil.getSampleDataUtil();

        assert sampleData != null : "sampleData should not be null";

        // Address book
        try {
            addressBookOptional = storage.readAddressBook();
            if (!addressBookOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with a sample AddressBook.");
            }
            initialAddressData = addressBookOptional.orElseGet(sampleData::getSampleAddressBook);
            assert initialAddressData != null : "AddressBook should not be null";
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty AddressBook.");
            initialAddressData = new AddressBook();
        }

        ObservableList<Company> existingCompanies = initialAddressData.getCompanyList();

        // Delivery book
        try {
            deliveryBookOptional = storage.readDeliveryBook(existingCompanies);
            if (!deliveryBookOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getDeliveryBookFilePath()
                        + " populated with a sample DeliveryBook.");
            }
            initialDeliveryData = deliveryBookOptional.orElseGet(sampleData::getSampleDeliveryBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getDeliveryBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty DeliveryBook.");
            initialDeliveryData = new DeliveryBook();
        }

        // User
        User initialUser;
        try {
            Optional<User> userOptional = storage.readUser();
            if (!userOptional.isPresent()) {
                logger.info("Creating a new user data file " + storage.getUserFilePath()
                        + " populated with sample user data.");
            }
            initialUser = userOptional.orElseGet(SampleDataUtil::getSampleUser);
        } catch (DataLoadingException e) {
            logger.warning("User data file at " + storage.getUserFilePath() + " could not be loaded."
                    + " Will be starting with sample user data.");
            initialUser = SampleDataUtil.getSampleUser();
        }

        return new ModelManager(initialAddressData, initialDeliveryData, userPrefs, initialUser);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting application " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping application ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
