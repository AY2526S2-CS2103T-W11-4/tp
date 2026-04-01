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
 * Marks a delivery as delivered by adding the tag "delivered".
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the delivery identified by the index number as delivered.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_MARK_DELIVERY_SUCCESS = "Marked delivery as delivered: %1$s";

    private final Index targetIndex;

    public MarkCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Delivery> lastShownList = model.getFilteredDeliveryList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_DELIVERY_DISPLAYED_INDEX);
        }

        Delivery deliveryToMark = lastShownList.get(targetIndex.getZeroBased());

        Set<Tag> newTags = new HashSet<>(deliveryToMark.getTags());
        newTags.add(new Tag("delivered"));

        Delivery markedDelivery = new Delivery(
                deliveryToMark.getProduct(),
                deliveryToMark.getCompany(),
                deliveryToMark.getDeadline(),
                newTags
        );

        model.setDelivery(deliveryToMark, markedDelivery);

        return new CommandResult(String.format(MESSAGE_MARK_DELIVERY_SUCCESS, markedDelivery));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MarkCommand)) {
            return false;
        }

        MarkCommand otherMarkCommand = (MarkCommand) other;
        return targetIndex.equals(otherMarkCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
