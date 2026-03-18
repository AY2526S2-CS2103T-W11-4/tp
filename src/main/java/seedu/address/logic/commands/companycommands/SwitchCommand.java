package seedu.address.logic.commands.companycommands;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;

/**
 * Terminates the program.
 */
public class SwitchCommand extends Command {

    public static final String COMMAND_WORD = "switch";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Switch between Address and Delivery.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS_ACKNOWLEDGEMENT_DELIVERY = "Switching to Delivery Book as requested ...";

    @Override
    public CommandResult execute(Model model) {
        model.setCompanyPackage(false);
        return new CommandResult(MESSAGE_SUCCESS_ACKNOWLEDGEMENT_DELIVERY);
    }

}
