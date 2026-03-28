package seedu.address.model.user;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents the User's vehicle in the application.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Vehicle {

    private final VehicleProfile profile;

    public Vehicle(VehicleProfile profile) {
        requireAllNonNull(profile);
        this.profile = profile;
    }

    public VehicleProfile getProfile() {
        return profile;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Vehicle)) {
            return false;
        }
        return profile.equals(((Vehicle) other).profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("profile", profile).toString();
    }
}
