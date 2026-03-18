package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private DeliveryBookStorage deliveryBookStorage;
    private UserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given {@code AddressBookStorage} and {@code UserPrefStorage}.
     */
    public StorageManager(AddressBookStorage addressBookStorage, DeliveryBookStorage deliveryBookStorage,
            UserPrefsStorage userPrefsStorage) {
        this.addressBookStorage = addressBookStorage;
        this.deliveryBookStorage = deliveryBookStorage;
        this.userPrefsStorage = userPrefsStorage;
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
    public Optional<ReadOnlyDeliveryBook> readDeliveryBook() throws DataLoadingException {
        return readDeliveryBook(deliveryBookStorage.getDeliveryBookFilePath());
    }

    @Override
    public Optional<ReadOnlyDeliveryBook> readDeliveryBook(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + filePath);
        return deliveryBookStorage.readDeliveryBook(filePath);
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
}
