package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.user.Vehicle;
import seedu.address.model.user.VehicleProfile;

/**
 * Jackson-friendly version of {@link Vehicle}.
 */
class JsonAdaptedVehicle {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Vehicle's %s field is missing!";

    private final String profile;

    @JsonCreator
    public JsonAdaptedVehicle(@JsonProperty("profile") String profile) {
        this.profile = profile;
    }

    public JsonAdaptedVehicle(Vehicle source) {
        this.profile = source.getProfile().value;
    }

    public Vehicle toModelType() throws IllegalValueException {
        if (profile == null) {
            throw new IllegalValueException(String.format(
                    MISSING_FIELD_MESSAGE_FORMAT, VehicleProfile.class.getSimpleName()));
        }
        if (!VehicleProfile.isValidProfile(profile)) {
            throw new IllegalValueException(VehicleProfile.MESSAGE_CONSTRAINTS);
        }
        return new Vehicle(new VehicleProfile(profile));
    }
}
