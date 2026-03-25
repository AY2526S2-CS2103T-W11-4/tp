package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.company.Company;

/**
 * A class to access deliveryBook data stored as a json file on the hard disk.
 */
public class JsonDeliveryBookStorage implements DeliveryBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonDeliveryBookStorage.class);

    private Path filePath;

    public JsonDeliveryBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getDeliveryBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyDeliveryBook> readDeliveryBook(ObservableList<Company> existingCompanies)
            throws DataLoadingException {
        return readDeliveryBook(filePath, existingCompanies);
    }

    /**
     * Similar to {@link #readDeliveryBook(ObservableList)}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyDeliveryBook> readDeliveryBook(Path filePath, ObservableList<Company> existingCompanies)
            throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableDeliveryBook> jsonDeliveryBook = JsonUtil.readJsonFile(
                filePath, JsonSerializableDeliveryBook.class);
        if (!jsonDeliveryBook.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonDeliveryBook.get().toModelType(existingCompanies));
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveDeliveryBook(ReadOnlyDeliveryBook deliveryBook) throws IOException {
        saveDeliveryBook(deliveryBook, filePath);
    }

    /**
     * Similar to {@link #saveDeliveryBook(ReadOnlyDeliveryBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveDeliveryBook(ReadOnlyDeliveryBook deliveryBook, Path filePath) throws IOException {
        requireNonNull(deliveryBook);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableDeliveryBook(deliveryBook), filePath);
    }

}
