package seedu.address.model.delivery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void constructor_invalidDeadline_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("2026/03/25 14:30"));
    }

    @Test
    public void isValidDeadline() {
        assertThrows(NullPointerException.class, () -> Deadline.isValidDeadline(null));

        assertFalse(Deadline.isValidDeadline("2026/03/25 14:30"));
        assertFalse(Deadline.isValidDeadline("2026-03-25"));
        assertFalse(Deadline.isValidDeadline("14:30 2026-03-25"));
        assertFalse(Deadline.isValidDeadline("2026-02-29 10:00"));
        assertFalse(Deadline.isValidDeadline("2026-04-31 10:00"));

        assertTrue(Deadline.isValidDeadline("2026-03-25 14:30"));
    }

    @Test
    public void toStorageString_returnsFormattedDeadline() {
        Deadline deadline = new Deadline("2026-03-25 14:30");
        assertEquals("2026-03-25 14:30", deadline.toStorageString());
    }
}
