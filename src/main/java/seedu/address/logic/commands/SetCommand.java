package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;

import java.util.HashSet;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.user.User;
import seedu.address.model.user.Vehicle;
import seedu.address.model.user.VehicleProfile;

/**
 * Parses input arguments and creates a new SetCommand object
 */
public class SetCommand extends Command {
    public static final String COMMAND_WORD = "set";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets address of the user. "
            + "Parameters: "
            + PREFIX_ADDRESS + "ADDRESS "
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 ";

    public static final String MESSAGE_SUCCESS = "New address set: ";

    private final Address toSet;

    /**
     * Creates an SetCommand to add the specified {@code address}
     */
    public SetCommand(Address address) {
        requireNonNull(address);
        toSet = address;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Company defaultCompany = new Company(
                new Name("User"),
                new Phone("00000000"),
                new Email("dummy@example.com"),
                this.toSet,
                new HashSet<>()
        );
        Vehicle defaultVehicle = new Vehicle(new VehicleProfile("driving-car"));

        model.setUser(new User(defaultCompany, defaultVehicle));

        return new CommandResult(String.format(MESSAGE_SUCCESS + this.toSet));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SetCommand)) {
            return false;
        }

        SetCommand otherSetCommand = (SetCommand) other;
        return toSet.equals(otherSetCommand.toSet);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toSet", toSet)
                .toString();
    }
}
