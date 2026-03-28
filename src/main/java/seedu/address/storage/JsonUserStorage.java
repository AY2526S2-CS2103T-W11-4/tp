package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.user.User;

/**
 * A class to access User data stored as a JSON file on the hard disk.
 */
public class JsonUserStorage implements UserStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonUserStorage.class);
    private final Path filePath;

    public JsonUserStorage(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Path getUserFilePath() {
        return filePath;
    }

    @Override
    public Optional<User> readUser() throws DataLoadingException {
        return readUser(filePath);
    }

    @Override
    public Optional<User> readUser(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);
        Optional<JsonSerializableUser> jsonUser = JsonUtil.readJsonFile(filePath, JsonSerializableUser.class);
        if (!jsonUser.isPresent()) {
            return Optional.empty();
        }
        try {
            return Optional.of(jsonUser.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveUser(User user) throws IOException {
        saveUser(user, filePath);
    }

    @Override
    public void saveUser(User user, Path filePath) throws IOException {
        requireNonNull(user);
        requireNonNull(filePath);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableUser(user), filePath);
    }
}
