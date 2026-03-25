package seedu.address.logic.commands.deliverycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.delivery.Company;
import seedu.address.model.delivery.Delivery;

/**
 * Sorts a company's deliveries by deadline, with the earliest deadline shown first.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts deliveries for a company by deadline, "
            + "with the earliest deadline first.\n"
            + "Parameters: " + PREFIX_COMPANY + "COMPANY\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_COMPANY + "Dell";

    public static final String MESSAGE_SORT_SUCCESS = "Sorted %1$d delivery(s) for company: %2$s";
    public static final String MESSAGE_NO_DELIVERIES_FOR_COMPANY = "No deliveries found for company: %1$s";

    private final Company company;

    /**
     * Creates a SortCommand to sort deliveries for the specified company.
     */
    public SortCommand(Company company) {
        requireNonNull(company);
        this.company = company;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Predicate<Delivery> matchesCompany = delivery -> delivery.getCompany().value.equalsIgnoreCase(company.value);

        boolean hasMatchingDelivery = model.getDeliveryBook().getDeliveryList().stream()
                .anyMatch(matchesCompany);
        if (!hasMatchingDelivery) {
            throw new CommandException(String.format(MESSAGE_NO_DELIVERIES_FOR_COMPANY, company));
        }

        model.sortDeliveriesByDeadline(matchesCompany);
        model.updateFilteredDeliveryList(matchesCompany);

        return new CommandResult(
                String.format(MESSAGE_SORT_SUCCESS, model.getFilteredDeliveryList().size(), company));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SortCommand)) {
            return false;
        }

        SortCommand otherSortCommand = (SortCommand) other;
        return company.equals(otherSortCommand.company);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("company", company)
                .toString();
    }
}
