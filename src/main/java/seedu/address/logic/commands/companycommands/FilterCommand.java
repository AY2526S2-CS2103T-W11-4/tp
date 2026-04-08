package seedu.address.logic.commands.companycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.*;

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
 * Sorts a company's deliveries by deadline, with the earliest deadline shown first.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Filters deliveries by parameters,\n"
            + "Parameters (Varargs): " + PREFIX_PRODUCT + "PRODUCT " + PREFIX_COMPANY + "COMPANY "
            + PREFIX_DEADLINE + "DEADLINE\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_COMPANY + "Dell" + PREFIX_PRODUCT + "Laptop";

    public static final String MESSAGE_SORT_SUCCESS = "Filtered %1$d delivery(s): %2$s";
    public static final String MESSAGE_NO_DELIVERIES = "No deliveries found: %1$s";

    private final List<ProductContainsKeywordsPredicate> productName;
    private final List<CompanyNameContainsKeywordsPredicate> companyName;
    private final List<LocalDate[]> timeRange;

    /**
     * Creates a FilterCommand to filter deliveries for the specified company.
     */
    public FilterCommand(List<ProductContainsKeywordsPredicate> productName,
                         List<CompanyNameContainsKeywordsPredicate> companyName,
                         List<LocalDate[]> timeRange) {
        requireNonNull(productName);
        this.productName = productName;
        this.companyName = companyName;
        this.timeRange = timeRange;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<String> productName = getProductName();
        List<String> companyName = getCompanyName();

        Predicate<Delivery> matchesProduct = delivery -> productName.isEmpty();
        Predicate<Delivery> matchesCompany = delivery -> companyName.isEmpty();
        Predicate<Delivery> matchesDeadline = delivery -> timeRange.isEmpty();
        Predicate<Delivery> matches;

        List<String> empty = new ArrayList<>();

        for (String name : productName) {
            Predicate<Delivery> match = delivery -> delivery.getProduct().getName().equalsIgnoreCase(name);
            boolean hasMatchingDelivery = model.getDeliveryBook().getDeliveryList().stream()
                    .anyMatch(match);
            if (!hasMatchingDelivery) {
                empty.add(name);
            }
            matchesProduct = matchesProduct.or(match);
        }

        for (String name : companyName) {
            Predicate<Delivery> match = delivery -> delivery.getCompany().getName().toString().equalsIgnoreCase(name);
            boolean hasMatchingDelivery = model.getDeliveryBook().getDeliveryList().stream()
                    .anyMatch(match);
            if (!hasMatchingDelivery) {
                empty.add(name);
            }
            matchesCompany = matchesCompany.or(match);
        }

        for (LocalDate[] range : timeRange) {
            Predicate<Delivery> match = delivery -> delivery.getDeadline().isInRange(range);
            boolean hasMatchingDelivery = model.getDeliveryBook().getDeliveryList().stream()
                    .anyMatch(match);
            if (!hasMatchingDelivery) {
                empty.add(Arrays.toString(range));
            }
            matchesDeadline = matchesDeadline.or(match);
        }

        matches = matchesProduct.and(matchesCompany.and(matchesDeadline));
        model.sortDeliveriesByDeadline(matches);
        model.updateFilteredDeliveryList(matches);
        if (!empty.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_DELIVERIES, String.join(" ", companyName)));
        }

        return new CommandResult(
                String.format(MESSAGE_SORT_SUCCESS, model.getFilteredDeliveryList().size(),
                        this.getParameterString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FilterCommand)) {
            return false;
        }

        FilterCommand otherFilterCommand = (FilterCommand) other;
        return productName.equals(otherFilterCommand.productName);
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
        if (!timeRange.isEmpty()) {
            res.add("timeRange", String.join(" ", timeRange.stream().map(Arrays::toString).toList()));
        }
        return res.toString();
    }

    private String getParameterString() {
        String res = "";
        if (!productName.isEmpty()) {
            res = res + "product: " + String.join(" ", getProductName()) + "\n";
        }
        if (!companyName.isEmpty()) {
            res = res + "company: " + String.join(" ", getCompanyName()) + "\n";
        }
        if (!timeRange.isEmpty()) {
            res = res + "timeRange: " + String.join(" ", timeRange.stream().map(Arrays::toString).toList()) + "\n";
        }
        return res;
    }

    private List<String> getProductName() {
        return productName.stream().map(x -> x.getKeywords().get(0)).toList();
    }

    private List<String> getCompanyName() {
        return companyName.stream().map(x -> x.getKeywords().get(0)).toList();
    }
}
