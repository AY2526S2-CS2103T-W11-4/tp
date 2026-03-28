package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;
import seedu.address.model.user.User;

/**
 * Manages storage of AddressBook, DeliveryBook and User data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);

    private final AddressBookStorage addressBookStorage;
    private final DeliveryBookStorage deliveryBookStorage;
    private final UserPrefsStorage userPrefsStorage;
    private final UserStorage userStorage;

    /**
     * Creates a {@code StorageManager} with the given storage components.
     */
    public StorageManager(AddressBookStorage addressBookStorage,
                          DeliveryBookStorage deliveryBookStorage,
                          UserPrefsStorage userPrefsStorage,
                          UserStorage userStorage) {
        this.addressBookStorage = addressBookStorage;
        this.deliveryBookStorage = deliveryBookStorage;
        this.userPrefsStorage = userPrefsStorage;
        this.userStorage = userStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }

    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + filePath);
        return addressBookStorage.readAddressBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    // ================ DeliveryBook methods ==============================

    @Override
    public Path getDeliveryBookFilePath() {
        return deliveryBookStorage.getDeliveryBookFilePath();
    }

    @Override
    public Optional<ReadOnlyDeliveryBook> readDeliveryBook(ObservableList<Company> existingCompanies)
            throws DataLoadingException {
        return readDeliveryBook(deliveryBookStorage.getDeliveryBookFilePath(), existingCompanies);
    }

    @Override
    public Optional<ReadOnlyDeliveryBook> readDeliveryBook(Path filePath,
            ObservableList<Company> existingCompanies) throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + filePath);
        return deliveryBookStorage.readDeliveryBook(filePath, existingCompanies);
    }

    @Override
    public void saveDeliveryBook(ReadOnlyDeliveryBook deliveryBook) throws IOException {
        saveDeliveryBook(deliveryBook, deliveryBookStorage.getDeliveryBookFilePath());
    }

    @Override
    public void saveDeliveryBook(ReadOnlyDeliveryBook deliveryBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        deliveryBookStorage.saveDeliveryBook(deliveryBook, filePath);
    }

    // ================ User methods ==============================

    @Override
    public Path getUserFilePath() {
        return userStorage.getUserFilePath();
    }

    @Override
    public Optional<User> readUser() throws DataLoadingException {
        return readUser(userStorage.getUserFilePath());
    }

    @Override
    public Optional<User> readUser(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read user data from file: " + filePath);
        return userStorage.readUser(filePath);
    }

    @Override
    public void saveUser(User user) throws IOException {
        saveUser(user, userStorage.getUserFilePath());
    }

    @Override
    public void saveUser(User user, Path filePath) throws IOException {
        logger.fine("Attempting to write user data to file: " + filePath);
        userStorage.saveUser(user, filePath);
    }
}
