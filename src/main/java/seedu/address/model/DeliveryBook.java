package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.UniqueDeliveryList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameCompany comparison for companies
 * and .isSameDelivery comparison for deliveries).
 */
public class DeliveryBook implements ReadOnlyDeliveryBook {

    private final UniqueDeliveryList deliveries;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     * among constructors.
     */
    {
        deliveries = new UniqueDeliveryList();
    }

    public DeliveryBook() {}

    /**
     * Creates an AddressBook using the companies and deliveries in the {@code toBeCopied}.
     */
    public DeliveryBook(ReadOnlyDeliveryBook toBeCopied) {
        this();
        resetData(toBeCopied);
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
    public void resetData(ReadOnlyDeliveryBook newData) {
        requireNonNull(newData);
        setDeliveries(newData.getDeliveryList());
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
     * Sorts deliveries by the given comparator.
     */
    public void sortDeliveries(Comparator<Delivery> comparator) {
        requireNonNull(comparator);
        deliveries.sort(comparator);
    }

    /**
     * Sorts only deliveries matching the predicate by the given comparator.
     */
    public void sortDeliveries(Predicate<Delivery> predicate, Comparator<Delivery> comparator) {
        requireNonNull(predicate);
        requireNonNull(comparator);
        deliveries.sortMatching(predicate, comparator);
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
                .add("deliveries", deliveries)
                .toString();
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

        if (!(other instanceof DeliveryBook)) {
            return false;
        }

        DeliveryBook otherAddressBook = (DeliveryBook) other;
        return deliveries.equals(otherAddressBook.deliveries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deliveries);
    }
}
