package seedu.address.model.company;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Address(null));
    }

    @Test
    public void constructor_invalidAddress_throwsIllegalArgumentException() {
        String invalidAddress = "";
        assertThrows(IllegalArgumentException.class, () -> new Address(invalidAddress));
    }

    @Test
    public void isValidAddress() {
        // null address
        assertThrows(NullPointerException.class, () -> Address.isValidAddress(null));

        // invalid addresses
        assertFalse(Address.isValidAddress("")); // empty string
        assertFalse(Address.isValidAddress(" ")); // spaces only
        assertFalse(Address.isValidAddress("  Warehouse 12")); // leading whitespace

        // valid addresses
        assertTrue(Address.isValidAddress("Warehouse 12, Jurong Port Road, #02-01"));
        assertTrue(Address.isValidAddress("Dock A")); // short dispatch location
        assertTrue(Address.isValidAddress(
                "Acme Logistics Pte Ltd, 21 Pioneer Road North, Singapore 628467")); // company delivery address
    }

    @Test
    public void equals() {
        Address address = new Address("North Distribution Hub");

        // same values -> returns true
        assertTrue(address.equals(new Address("North Distribution Hub")));

        // same object -> returns true
        assertTrue(address.equals(address));

        // null -> returns false
        assertFalse(address.equals(null));

        // different types -> returns false
        assertFalse(address.equals(5.0f));

        // different values -> returns false
        assertFalse(address.equals(new Address("South Delivery Hub")));
    }
}
