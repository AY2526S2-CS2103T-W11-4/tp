package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;

/**
 * API of the Storage component
 */
public interface Storage extends AddressBookStorage, DeliveryBookStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Path getAddressBookFilePath();

    @Override
    Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException;

    @Override
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

    @Override
    Optional<ReadOnlyDeliveryBook> readDeliveryBook(ObservableList<Company> existingCompanies)
            throws DataLoadingException;

    @Override
    void saveDeliveryBook(ReadOnlyDeliveryBook deliveryBook) throws IOException;

}
