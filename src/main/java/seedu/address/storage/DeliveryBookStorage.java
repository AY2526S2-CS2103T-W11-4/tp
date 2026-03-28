package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.company.Company;

/**
 * Represents a storage for {@link seedu.address.model.DeliveryBook}.
 */
public interface DeliveryBookStorage {

    /**
     * Returns the file path of the data file.
     */
    Path getDeliveryBookFilePath();

    /**
     * Returns DeliveryBook data as a {@link ReadOnlyDeliveryBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Optional<ReadOnlyDeliveryBook> readDeliveryBook(ObservableList<Company> existingCompanies)
            throws DataLoadingException;

    /**
     * @see #getDeliveryBookFilePath()
     */
    Optional<ReadOnlyDeliveryBook> readDeliveryBook(Path filePath, ObservableList<Company> existingCompanies)
            throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyDeliveryBook} to the storage.
     * @param deliveryBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveDeliveryBook(ReadOnlyDeliveryBook deliveryBook) throws IOException;

    /**
     * @see #saveDeliveryBook(ReadOnlyDeliveryBook)
     */
    void saveDeliveryBook(ReadOnlyDeliveryBook deliveryBook, Path filePath) throws IOException;

}
