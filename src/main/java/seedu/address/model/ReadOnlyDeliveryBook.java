package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.delivery.Delivery;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyDeliveryBook {

    /**
     * Returns an unmodifiable view of the companies list.
     * This list will not contain any duplicate companies.
     */
    ObservableList<Delivery> getDeliveryList();
}
