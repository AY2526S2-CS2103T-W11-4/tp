package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.user.User;

/**
 * Represents a storage for {@link User}.
 */
public interface UserStorage {

    Path getUserFilePath();

    Optional<User> readUser() throws DataLoadingException;

    Optional<User> readUser(Path filePath) throws DataLoadingException;

    void saveUser(User user) throws IOException;

    void saveUser(User user, Path filePath) throws IOException;
}
