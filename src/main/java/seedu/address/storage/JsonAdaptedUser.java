package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.company.Company;
import seedu.address.model.user.User;
import seedu.address.model.user.Vehicle;

/**
 * Jackson-friendly version of {@link User}.
 */
class JsonAdaptedUser {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "User's %s field is missing!";

    private final JsonAdaptedCompany company;
    private final JsonAdaptedVehicle vehicle;

    @JsonCreator
    public JsonAdaptedUser(
            @JsonProperty("company") JsonAdaptedCompany company,
            @JsonProperty("vehicle") JsonAdaptedVehicle vehicle) {
        this.company = company;
        this.vehicle = vehicle;
    }

    public JsonAdaptedUser(User source) {
        this.company = new JsonAdaptedCompany(source.getCompany());
        this.vehicle = new JsonAdaptedVehicle(source.getVehicle());
    }

    public User toModelType() throws IllegalValueException {
        if (company == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Company.class.getSimpleName()));
        }
        if (vehicle == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Vehicle.class.getSimpleName()));
        }
        return new User(company.toModelType(), vehicle.toModelType());
    }
}
