package seedu.address.model.util;

import java.util.HashSet;

import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.user.User;
import seedu.address.model.user.Vehicle;
import seedu.address.model.user.VehicleProfile;

/**
 * Contains utility methods for creating a sample {@code User}.
 */
public class SampleUserDataUtil {

    /**
     * Returns a sample User with a default company (depot) and vehicle.
     * Used when no user data has been saved yet.
     */
    public static User getSampleUser() {
        Company defaultCompany = new Company(
                new Name("My Company"),
                new Phone("61234567"),
                new Email("company@example.com"),
                new Address("3 Temasek Boulevard, Singapore 038983"),
                new HashSet<>()
        );

        Vehicle defaultVehicle = new Vehicle(new VehicleProfile("driving-car"));

        return new User(defaultCompany, defaultVehicle);
    }
}
