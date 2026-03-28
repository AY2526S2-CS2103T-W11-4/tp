package seedu.address.model.user;

import seedu.address.model.company.Company;

/**
 * Unmodifiable view of a User.
 */
public interface ReadOnlyUser {
    Company getCompany();
    Vehicle getVehicle();
}
