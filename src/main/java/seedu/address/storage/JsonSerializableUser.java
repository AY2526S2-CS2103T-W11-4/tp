package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.user.User;

/**
 * An Immutable User that is serializable to JSON format.
 */
@JsonRootName(value = "user")
class JsonSerializableUser {

    private final JsonAdaptedUser user;

    @JsonCreator
    public JsonSerializableUser(@JsonProperty("user") JsonAdaptedUser user) {
        this.user = user;
    }

    public JsonSerializableUser(User source) {
        this.user = new JsonAdaptedUser(source);
    }

    public User toModelType() throws IllegalValueException {
        if (user == null) {
            throw new IllegalValueException("User data is missing!");
        }
        return user.toModelType();
    }
}
