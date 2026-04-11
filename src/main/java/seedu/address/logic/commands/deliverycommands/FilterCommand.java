package seedu.address.logic.commands.deliverycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.company.CompanyNameContainsKeywordsPredicate;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.ProductContainsKeywordsPredicate;

/**
 * Filters deliveries by product, company, tag, and/or deadline range.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Filters deliveries by parameters,\n"
            + "Parameters (Varargs): " + PREFIX_PRODUCT + "PRODUCT " + PREFIX_COMPANY + "COMPANY "
            + PREFIX_DEADLINE + "DEADLINE" + PREFIX_TAG + "TAG\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_COMPANY + "Dell " + PREFIX_PRODUCT + "Laptop "
            + PREFIX_TAG + "fragile";

    public static final String MESSAGE_FILTER_SUCCESS = "Filtered %1$d delivery(s): %2$s";
    public static final String MESSAGE_NO_DELIVERIES = "No deliveries found: %1$s";

    private final List<ProductContainsKeywordsPredicate> productName;
    private final List<CompanyNameContainsKeywordsPredicate> companyName;
    private final List<String> tags;
    private final List<LocalDate[]> timeRange;

    /**
     * Creates a FilterCommand to filter deliveries for the specified parameters.
     */
    public FilterCommand(List<ProductContainsKeywordsPredicate> productName,
                         List<CompanyNameContainsKeywordsPredicate> companyName,
                         List<String> tags,
                         List<LocalDate[]> timeRange) {
        requireNonNull(productName);
        requireNonNull(companyName);
        requireNonNull(tags);
        requireNonNull(timeRange);
        this.productName = productName;
        this.companyName = companyName;
        this.tags = tags;
        this.timeRange = timeRange;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<String> productNames = getProductName();
        List<String> companyNames = getCompanyName();

        Predicate<Delivery> matchesProduct = delivery -> productNames.isEmpty();
        Predicate<Delivery> matchesCompany = delivery -> companyNames.isEmpty();
        Predicate<Delivery> matchesTag = delivery -> tags.isEmpty();
        Predicate<Delivery> matchesDeadline = delivery -> timeRange.isEmpty();

        List<String> notFound = new ArrayList<>();

        for (String name : productNames) {
            Predicate<Delivery> match = delivery ->
                    delivery.getProduct().getName().equalsIgnoreCase(name);
            boolean hasMatch = model.getDeliveryBook().getDeliveryList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(name);
            }
            matchesProduct = matchesProduct.or(match);
        }

        for (String name : companyNames) {
            Predicate<Delivery> match = delivery ->
                    delivery.getCompany().getName().toString().equalsIgnoreCase(name);
            boolean hasMatch = model.getDeliveryBook().getDeliveryList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(name);
            }
            matchesCompany = matchesCompany.or(match);
        }

        for (String tag : tags) {
            Predicate<Delivery> match = delivery ->
                    delivery.getTags().stream()
                            .anyMatch(t -> t.tagName.equalsIgnoreCase(tag));
            boolean hasMatch = model.getDeliveryBook().getDeliveryList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(tag);
            }
            matchesTag = matchesTag.or(match);
        }

        for (LocalDate[] range : timeRange) {
            Predicate<Delivery> match = delivery -> delivery.getDeadline().isInRange(range);
            boolean hasMatch = model.getDeliveryBook().getDeliveryList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(Arrays.toString(range));
            }
            matchesDeadline = matchesDeadline.or(match);
        }

        Predicate<Delivery> combined =
                matchesProduct.and(matchesCompany).and(matchesTag).and(matchesDeadline);

        model.sortDeliveriesByDeadline(combined);
        model.updateFilteredDeliveryList(combined);

        if (!notFound.isEmpty()) {
            throw new CommandException(
                    String.format(MESSAGE_NO_DELIVERIES, String.join(", ", notFound)));
        }

        return new CommandResult(
                String.format(MESSAGE_FILTER_SUCCESS,
                        model.getFilteredDeliveryList().size(),
                        getParameterString()));
    }

    private String getParameterString() {
        List<String> parts = new ArrayList<>();
        if (!productName.isEmpty()) {
            parts.add("product: " + String.join(", ", getProductName()));
        }
        if (!companyName.isEmpty()) {
            parts.add("company: " + String.join(", ", getCompanyName()));
        }
        if (!tags.isEmpty()) {
            parts.add("tag: " + String.join(", ", tags));
        }
        if (!timeRange.isEmpty()) {
            parts.add("timeRange: "
                    + String.join(", ", timeRange.stream().map(Arrays::toString).toList()));
        }
        return String.join(" | ", parts);
    }

    private List<String> getProductName() {
        return productName.stream().map(x -> x.getKeywords().get(0)).toList();
    }

    private List<String> getCompanyName() {
        return companyName.stream().map(x -> x.getKeywords().get(0)).toList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }
        return productName.equals(otherFilterCommand.productName)
                && companyName.equals(otherFilterCommand.companyName)
                && tags.equals(otherFilterCommand.tags)
                && timeRange.equals(otherFilterCommand.timeRange);
    }

    @Override
    public String toString() {
        ToStringBuilder res = new ToStringBuilder(this);
        if (!productName.isEmpty()) {
            res.add("product", getProductName());
        }
        if (!companyName.isEmpty()) {
            res.add("company", getCompanyName());
        }
        if (!tags.isEmpty()) {
            res.add("tag", tags);
        }
        if (!timeRange.isEmpty()) {
            res.add("timeRange",
                    String.join(" ", timeRange.stream().map(Arrays::toString).toList()));
        }
        return res.toString();
    }
}
