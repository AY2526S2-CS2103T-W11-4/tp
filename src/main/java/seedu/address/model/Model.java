package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.company.Company;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.user.User;

/**
 * The API of the Model component.
 */
public interface Model {

    Predicate<Company> PREDICATE_SHOW_ALL_COMPANIES = unused -> true;
    Predicate<Delivery> PREDICATE_SHOW_ALL_DELIVERIES = unused -> true;

    void setUserPrefs(ReadOnlyUserPrefs userPrefs);
    ReadOnlyUserPrefs getUserPrefs();
    GuiSettings getGuiSettings();
    void setGuiSettings(GuiSettings guiSettings);
    boolean getCompanyPackage();
    void setCompanyPackage(boolean isCompanyPackage);
    Path getAddressBookFilePath();
    void setAddressBookFilePath(Path addressBookFilePath);
    void setAddressBook(ReadOnlyAddressBook addressBook);
    ReadOnlyAddressBook getAddressBook();
    boolean hasCompany(Company company);
    void deleteCompany(Company target);
    void addCompany(Company company);
    void setCompany(Company target, Company editedCompany);
    ObservableList<Company> getFilteredCompanyList();
    void updateFilteredCompanyList(Predicate<Company> predicate);

    void setDeliveryBookFilePath(Path deliveryBookFilePath);
    void setDeliveryBook(ReadOnlyDeliveryBook deliveryBook);
    ReadOnlyDeliveryBook getDeliveryBook();
    boolean hasDelivery(Delivery delivery);
    void deleteDelivery(Delivery target);
    void addDelivery(Delivery delivery);
    void setDelivery(Delivery delivery, Delivery editedDelivery);
    void sortDeliveriesByDeadline(Predicate<Delivery> predicate);
    ObservableList<Delivery> getFilteredDeliveryList();
    void updateFilteredDeliveryList(Predicate<Delivery> predicate);

    // =========== User ================================================================

    /**
     * Returns the current user (driver profile + depot).
     */
    User getUser();

    /**
     * Replaces the current user with {@code user}.
     */
    void setUser(User user);
}
