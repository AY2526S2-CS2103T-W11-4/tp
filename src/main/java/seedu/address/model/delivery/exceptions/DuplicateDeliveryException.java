package seedu.address.model.delivery.exceptions;

/**
 * Signals that the operation will result in duplicate Persons (Persons are considered duplicates if they have the same
 * identity).
 */
public class DuplicateDeliveryException extends RuntimeException {
    public DuplicateDeliveryException() {
        super("Operation would result in duplicate persons");
    }
}
