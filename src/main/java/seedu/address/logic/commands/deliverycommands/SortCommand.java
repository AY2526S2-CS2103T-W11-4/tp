package seedu.address.logic.commands.deliverycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_DELIVERIES;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.Model;

/**
 * Sorts a company's deliveries by deadline, with the earliest deadline shown first.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts deliveries by parameters,\n"
            + "Parameters (Varargs): " + PREFIX_PRODUCT + " " + PREFIX_COMPANY + " " + PREFIX_DEADLINE + "\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_PRODUCT + PREFIX_COMPANY + " ";

    public static final String MESSAGE_SORT_SUCCESS = "Sort %1$d delivery(s) by: %2$s";
    public static final String MESSAGE_NO_DELIVERIES = "No deliveries found";

    private final List<Prefix> prefixes;

    /**
     * Creates a FilterCommand to filter deliveries for the specified company.
     */
    public SortCommand(List<Prefix> prefixes) {
        requireNonNull(prefixes);
        this.prefixes = prefixes;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.getFilteredDeliveryList().isEmpty()) {
            throw new CommandException(MESSAGE_NO_DELIVERIES);
        }

        StringBuilder sortBy = new StringBuilder();
        for (Prefix prefix : prefixes) {
            if (prefix.equals(PREFIX_PRODUCT)) {
                model.sortDeliveriesByProduct(PREDICATE_SHOW_ALL_DELIVERIES);
                sortBy.append(" product");
            } else if (prefix.equals(PREFIX_COMPANY)) {
                model.sortDeliveriesByCompany(PREDICATE_SHOW_ALL_DELIVERIES);
                sortBy.append(" company");
            } else if (prefix.equals(PREFIX_DEADLINE)) {
                model.sortDeliveriesByDeadline(PREDICATE_SHOW_ALL_DELIVERIES);
                sortBy.append(" deadline");
            }
        }

        return new CommandResult(
                String.format(MESSAGE_SORT_SUCCESS, model.getFilteredDeliveryList().size(), sortBy));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SortCommand otherSortCommand)) {
            return false;
        }

        return prefixes.equals(otherSortCommand.prefixes);
    }

    @Override
    public String toString() {
        ToStringBuilder res = new ToStringBuilder(this);
        for (Prefix prefix : prefixes) {
            if (prefix.equals(PREFIX_PRODUCT)) {
                res.add("product", PREFIX_PRODUCT);
            } else if (prefix.equals(PREFIX_COMPANY)) {
                res.add("company", PREFIX_COMPANY);
            } else if (prefix.equals(PREFIX_DEADLINE)) {
                res.add("deadline", PREFIX_DEADLINE);
            }
        }
        return res.toString();
    }
}

