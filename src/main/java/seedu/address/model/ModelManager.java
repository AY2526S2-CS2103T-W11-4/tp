package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.company.Company;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.user.User;
import seedu.address.model.util.SampleDataUtil;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {

    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);
    private static final Comparator<Delivery> DELIVERY_DEADLINE_COMPARATOR = Comparator
            .comparing((Delivery delivery) -> delivery.getDeadline().getValue())
            .thenComparing(delivery -> delivery.getCompany().getName().toString().toLowerCase())
            .thenComparing(delivery -> delivery.getProduct().productName.toLowerCase());

    private final AddressBook addressBook;
    private final DeliveryBook deliveryBook;
    private final UserPrefs userPrefs;
    private User user;
    private final FilteredList<Company> filteredCompanies;
    private final FilteredList<Delivery> filteredDeliveries;
    private boolean isCompanyPackage;

    /**
     * Initializes a ModelManager with the given addressBook, deliveryBook, userPrefs and user.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyDeliveryBook deliveryBook,
                        ReadOnlyUserPrefs userPrefs, User user) {
        requireAllNonNull(addressBook, deliveryBook, userPrefs, user);
        logger.fine("Initializing with address book: " + addressBook
                + " and user prefs " + userPrefs
                + " and user " + user);

        this.addressBook = new AddressBook(addressBook);
        this.deliveryBook = new DeliveryBook(deliveryBook);
        this.userPrefs = new UserPrefs(userPrefs);
        this.user = user;
        this.filteredCompanies = new FilteredList<>(this.addressBook.getCompanyList());
        this.filteredDeliveries = new FilteredList<>(this.deliveryBook.getDeliveryList());
    }

    /**
     * Convenience constructor — uses sample user. Preserves backward compatibility.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyDeliveryBook deliveryBook,
                        ReadOnlyUserPrefs userPrefs) {
        this(addressBook, deliveryBook, userPrefs, SampleDataUtil.getSampleUser());
    }

    public ModelManager() {
        this(new AddressBook(), new DeliveryBook(), new UserPrefs());
    }

    // =========== UserPrefs ===========================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public boolean getCompanyPackage() {
        return isCompanyPackage;
    }

    @Override
    public void setCompanyPackage(boolean isCompanyPackage) {
        this.isCompanyPackage = isCompanyPackage;
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    // =========== AddressBook =========================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasCompany(Company company) {
        requireNonNull(company);
        return addressBook.hasCompany(company);
    }

    @Override
    public void deleteCompany(Company target) {
        requireNonNull(target);
        addressBook.removeCompany(target);
    }

    @Override
    public void addCompany(Company company) {
        requireNonNull(company);
        addressBook.addCompany(company);
        updateFilteredCompanyList(PREDICATE_SHOW_ALL_COMPANIES);
    }

    @Override
    public void setCompany(Company target, Company editedCompany) {
        requireAllNonNull(target, editedCompany);
        addressBook.setCompany(target, editedCompany);
    }

    @Override
    public ObservableList<Company> getFilteredCompanyList() {
        return filteredCompanies;
    }

    @Override
    public void updateFilteredCompanyList(Predicate<Company> predicate) {
        requireNonNull(predicate);
        filteredCompanies.setPredicate(predicate);
    }

    // =========== DeliveryBook ========================================================

    @Override
    public void setDeliveryBookFilePath(Path deliveryBookFilePath) {
        requireNonNull(deliveryBookFilePath);
        userPrefs.setDeliveryBookFilePath(deliveryBookFilePath);
    }

    @Override
    public void setDeliveryBook(ReadOnlyDeliveryBook deliveryBook) {
        this.deliveryBook.resetData(deliveryBook);
    }

    @Override
    public ReadOnlyDeliveryBook getDeliveryBook() {
        return deliveryBook;
    }

    @Override
    public boolean hasDelivery(Delivery delivery) {
        requireNonNull(delivery);
        return deliveryBook.hasDelivery(delivery);
    }

    @Override
    public void addDelivery(Delivery delivery) {
        requireNonNull(delivery);
        deliveryBook.addDelivery(delivery);
        updateFilteredDeliveryList(deliveryItem -> true);
    }

    @Override
    public void setDelivery(Delivery target, Delivery editedDelivery) {
        requireAllNonNull(target, editedDelivery);
        deliveryBook.setDelivery(target, editedDelivery);
        updateFilteredDeliveryList(deliveryItem -> true);
    }

    @Override
    public void sortDeliveriesByDeadline(Predicate<Delivery> predicate) {
        requireNonNull(predicate);
        deliveryBook.sortDeliveries(predicate, DELIVERY_DEADLINE_COMPARATOR);
    }

    @Override
    public void deleteDelivery(Delivery delivery) {
        requireNonNull(delivery);
        deliveryBook.removeDelivery(delivery);
        updateFilteredDeliveryList(deliveryItem -> true);
    }

    @Override
    public ObservableList<Delivery> getFilteredDeliveryList() {
        return filteredDeliveries;
    }

    @Override
    public void updateFilteredDeliveryList(Predicate<Delivery> predicate) {
        requireNonNull(predicate);
        filteredDeliveries.setPredicate(predicate);
    }

    // =========== User ================================================================

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        requireNonNull(user);
        this.user = user;
    }

    // =========== Equality ============================================================

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ModelManager)) {
            return false;
        }
        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && user.equals(otherModelManager.user)
                && filteredCompanies.equals(otherModelManager.filteredCompanies)
                && filteredDeliveries.equals(otherModelManager.filteredDeliveries);
    }
}
