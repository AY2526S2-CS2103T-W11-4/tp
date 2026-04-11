package seedu.address.logic.commands.companycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_COMPANIES;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.deliverycommands.ListCommand;
import seedu.address.model.Model;

/**
 * Unfilters companies.
 */

public class UnfilterCommand extends Command {
    public static final String COMMAND_WORD = "unfilter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": unfilters";

    public static final String MESSAGE_SUCCESS = "Unfiltered all deliveries";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredCompanyList(PREDICATE_SHOW_ALL_COMPANIES);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ListCommand;
    }
}
