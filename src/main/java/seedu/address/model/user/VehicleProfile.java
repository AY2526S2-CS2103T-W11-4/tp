package seedu.address.model.user;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the vehicle profile used for routing.
 * Guarantees: immutable; is valid as declared in {@link #isValidProfile(String)}
 */
public class VehicleProfile {

    public static final String MESSAGE_CONSTRAINTS =
            "Vehicle profile must be one of: driving-car, driving-hgv, cycling-regular, "
            + "cycling-road, cycling-mountain, foot-walking, foot-hiking";

    public static final String[] VALID_PROFILES = {
        "driving-car", "driving-hgv", "cycling-regular",
        "cycling-road", "cycling-mountain", "foot-walking", "foot-hiking"
    };

    public final String value;

    public VehicleProfile(String profile) {
        requireNonNull(profile);
        checkArgument(isValidProfile(profile), MESSAGE_CONSTRAINTS);
        this.value = profile;
    }

    public static boolean isValidProfile(String test) {
        for (String p : VALID_PROFILES) {
            if (p.equals(test)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof VehicleProfile)) {
            return false;
        }
        return value.equals(((VehicleProfile) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
