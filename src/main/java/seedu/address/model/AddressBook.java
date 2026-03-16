package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.company.Company;
import seedu.address.model.company.UniqueCompanyList;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.UniqueDeliveryList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameCompany comparison for companies
 * and .isSameDelivery comparison for deliveries).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueCompanyList companies;
    private final UniqueDeliveryList deliveries;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     * among constructors.
     */
    {
        companies = new UniqueCompanyList();
        deliveries = new UniqueDeliveryList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the companies and deliveries in the {@code toBeCopied}.
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the company list with {@code companies}.
     * {@code companies} must not contain duplicate companies.
     */
    public void setCompanies(List<Company> companies) {
        this.companies.setCompanies(companies);
    }

    /**
     * Replaces the contents of the delivery list with {@code deliveries}.
     * {@code deliveries} must not contain duplicate deliveries.
     */
    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries.setDeliveries(deliveries);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setCompanies(newData.getCompanyList());
        setDeliveries(newData.getDeliveryList());
    }

    //// company-level operations

    /**
     * Returns true if a company with the same identity as {@code company} exists in the address book.
     */
    public boolean hasCompany(Company company) {
        requireNonNull(company);
        return companies.contains(company);
    }

    /**
     * Adds a company to the address book.
     * The company must not already exist in the address book.
     */
    public void addCompany(Company company) {
        companies.add(company);
    }

    /**
     * Replaces the given company {@code target} in the list with {@code editedCompany}.
     * {@code target} must exist in the address book.
     * The company identity of {@code editedCompany} must not be the same as another existing company
     * in the address book.
     */
    public void setCompany(Company target, Company editedCompany) {
        requireNonNull(editedCompany);
        companies.setCompany(target, editedCompany);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeCompany(Company key) {
        companies.remove(key);
    }

    //// delivery-level operations

    /**
     * Returns true if a delivery with the same identity as {@code delivery} exists in the address book.
     */
    public boolean hasDelivery(Delivery delivery) {
        requireNonNull(delivery);
        return deliveries.contains(delivery);
    }

    /**
     * Adds a delivery to the address book.
     * The delivery must not already exist in the address book.
     */
    public void addDelivery(Delivery delivery) {
        requireNonNull(delivery);
        deliveries.add(delivery);
    }

    /**
     * Replaces the given delivery {@code target} in the list with {@code editedDelivery}.
     * {@code target} must exist in the address book.
     * The delivery identity of {@code editedDelivery} must not be the same as another existing delivery
     * in the address book.
     */
    public void setDelivery(Delivery target, Delivery editedDelivery) {
        requireNonNull(editedDelivery);
        deliveries.setDelivery(target, editedDelivery);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeDelivery(Delivery key) {
        requireNonNull(key);
        deliveries.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("companies", companies)
                .add("deliveries", deliveries)
                .toString();
    }

    @Override
    public ObservableList<Company> getCompanyList() {
        return companies.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Delivery> getDeliveryList() {
        return deliveries.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        return companies.equals(otherAddressBook.companies)
                && deliveries.equals(otherAddressBook.deliveries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companies, deliveries);
    }
}
