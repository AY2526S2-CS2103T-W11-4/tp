package seedu.address.model.delivery;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents a Delivery deadline.
 * Guarantees: immutable; is valid as declared in {@link #isValidDeadline(String)}
 */
public class Deadline {

    public static final String MESSAGE_CONSTRAINTS =
            "Deadline should follow the format yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    private final LocalDateTime value;

    /**
     * Constructs a {@code Deadline}.
     *
     * @param deadline A valid deadline string.
     */
    public Deadline(String deadline) {
        requireNonNull(deadline);
        checkArgument(isValidDeadline(deadline), MESSAGE_CONSTRAINTS);
        value = LocalDateTime.parse(deadline, FORMATTER);
    }

    /**
     * Constructs a {@code Deadline}.
     *
     * @param deadline A valid {@code LocalDateTime}.
     */
    public Deadline(LocalDateTime deadline) {
        requireNonNull(deadline);
        value = deadline;
    }

    /**
     * Returns true if a given string is a valid deadline.
     */
    public static boolean isValidDeadline(String test) {
        requireNonNull(test);
        try {
            LocalDateTime.parse(test, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public LocalDateTime getValue() {
        return value;
    }

    public String toStorageString() {
        return value.format(FORMATTER);
    }

    @Override
    public String toString() {
        return toStorageString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Deadline)) {
            return false;
        }

        Deadline otherDeadline = (Deadline) other;
        return value.equals(otherDeadline.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
