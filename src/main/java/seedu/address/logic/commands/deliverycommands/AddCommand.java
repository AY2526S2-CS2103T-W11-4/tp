package seedu.address.logic.commands.deliverycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.delivery.Delivery;

/**
 * Adds a delivery to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a delivery to the delivery book. "
            + "Parameters: "
            + PREFIX_PRODUCT + "PRODUCT "
            + PREFIX_COMPANY + "COMPANY "
            + PREFIX_DEADLINE + "DEADLINE "
            + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_PRODUCT + "Laptop "
            + PREFIX_COMPANY + "Dell "
            + PREFIX_DEADLINE + "2026-03-25 14:30 "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "urgent";

    public static final String MESSAGE_SUCCESS = "New delivery added: %1$s";
    public static final String MESSAGE_DUPLICATE_DELIVERY = "This delivery already exists in the address book";

    private final Delivery toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Delivery}.
     */
    public AddCommand(Delivery delivery) {
        requireNonNull(delivery);
        toAdd = delivery;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasDelivery(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_DELIVERY);
        }

        model.addDelivery(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
