package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.company.Company;
import seedu.address.model.delivery.Delivery;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX = "The company index provided is invalid";
    public static final String MESSAGE_COMPANIES_LISTED_OVERVIEW = "%1$d companies listed!";
    public static final String MESSAGE_DELIVERIES_LISTED_OVERVIEW = "%1$d deliveries listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_INVALID_DELIVERY_DISPLAYED_INDEX =
            "The delivery index provided is invalid";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code company} for display to the user.
     */
    public static String format(Company company) {
        final StringBuilder builder = new StringBuilder();
        builder.append(company.getName())
                .append("; Phone: ")
                .append(company.getPhone())
                .append("; Email: ")
                .append(company.getEmail())
                .append("; Address: ")
                .append(company.getAddress())
                .append("; Tags: ");
        company.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code company} for display to the user.
     */
    public static String format(Delivery delivery) {
        final StringBuilder builder = new StringBuilder();
        builder.append(delivery.getProduct())
                .append("; Product: ")
                .append(delivery.getCompany())
                .append("; Company: ")
                .append(delivery.getAddress())
                .append("; Address: ");
        delivery.getTags().forEach(builder::append);
        return builder.toString();
    }

}
