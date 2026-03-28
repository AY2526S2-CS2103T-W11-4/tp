package seedu.address.model.user;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.company.Company;

/**
 * Represents the logged-in driver/user of the application.
 * Holds their associated Company (depot start address) and Vehicle (routing profile).
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class User implements ReadOnlyUser {

    private final Company company;
    private final Vehicle vehicle;

    public User(Company company, Vehicle vehicle) {
        requireAllNonNull(company, vehicle);
        this.company = company;
        this.vehicle = vehicle;
    }

    @Override
    public Company getCompany() {
        return company;
    }

    @Override
    public Vehicle getVehicle() {
        return vehicle;
    }

    /** Convenience method — returns the depot address string for use in routing. */
    public String getDepotAddress() {
        return company.getAddress().value;
    }

    /** Convenience method — returns the ORS profile string for use in routing. */
    public String getVehicleProfile() {
        return vehicle.getProfile().value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof User)) {
            return false;
        }
        User otherUser = (User) other;
        return company.equals(otherUser.company) && vehicle.equals(otherUser.vehicle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, vehicle);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("company", company)
                .add("vehicle", vehicle)
                .toString();
    }
}
