package seedu.address.logic.commands.deliverycommands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.tag.Tag;

/**
 * Unmarks a delivery as delivered by removing the "delivered" tag.
 */
public class UnmarkCommand extends Command {

    public static final String COMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unmarks the delivery identified by the index number.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UNMARK_DELIVERY_SUCCESS =
            "Unmarked delivery as not delivered: %1$s";

    private final Index targetIndex;

    public UnmarkCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Delivery> lastShownList = model.getFilteredDeliveryList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_DELIVERY_DISPLAYED_INDEX);
        }

        Delivery deliveryToUnmark = lastShownList.get(targetIndex.getZeroBased());

        Set<Tag> newTags = new HashSet<>(deliveryToUnmark.getTags());
        newTags.remove(new Tag("delivered"));

        Delivery unmarkedDelivery = new Delivery(
                deliveryToUnmark.getProduct(),
                deliveryToUnmark.getCompany(),
                deliveryToUnmark.getDeadline(),
                deliveryToUnmark.getAddress(),
                newTags
        );

        model.setDelivery(deliveryToUnmark, unmarkedDelivery);

        return new CommandResult(
                String.format(MESSAGE_UNMARK_DELIVERY_SUCCESS, unmarkedDelivery));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnmarkCommand)) {
            return false;
        }

        UnmarkCommand otherCommand = (UnmarkCommand) other;
        return targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
