package seedu.address.logic.commands.deliverycommands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.delivery.Delivery;

/**
 * Selects deliveries at the given indices in the filtered list (sets selection to true; idempotent).
 */
public class SelectCommand extends Command {

    public static final String COMMAND_WORD = "select";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Selects deliveries at the given indices in the displayed list (sets checked to true).\n"
            + "Repeating the same index keeps it selected. Use checkboxes to unselect individual items.\n"
            + "Parameters: INDEX [INDEX]... | none\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "Example: " + COMMAND_WORD + " 1 3\n"
            + "Example: " + COMMAND_WORD + " none (clear all selection)";

    public static final String MESSAGE_CLEAR_SUCCESS = "Cleared delivery selection.";

    public static final String MESSAGE_SELECT_SUCCESS = "Selected %1$d index(es). "
            + "Currently %2$d delivery(s) selected.";

    private final boolean clearAll;
    private final List<Index> indicesToSelect;

    /**
     * @param clearAll if true, {@code indicesToSelect} is ignored and selection is cleared
     */
    public SelectCommand(boolean clearAll, List<Index> indicesToSelect) {
        this.clearAll = clearAll;
        this.indicesToSelect = indicesToSelect == null ? List.of() : List.copyOf(indicesToSelect);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (clearAll) {
            model.clearDeliverySelection();
            return new CommandResult(MESSAGE_CLEAR_SUCCESS);
        }

        List<Delivery> shown = model.getFilteredDeliveryList();
        for (Index index : indicesToSelect) {
            if (index.getZeroBased() >= shown.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_DELIVERY_DISPLAYED_INDEX);
            }
        }
        for (Index index : indicesToSelect) {
            Delivery delivery = shown.get(index.getZeroBased());
            model.setDeliverySelected(delivery, true);
        }

        int count = model.getSelectedDeliveriesInDisplayOrder().size();
        return new CommandResult(String.format(MESSAGE_SELECT_SUCCESS, indicesToSelect.size(), count));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SelectCommand)) {
            return false;
        }
        SelectCommand o = (SelectCommand) other;
        return clearAll == o.clearAll && indicesToSelect.equals(o.indicesToSelect);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(clearAll, indicesToSelect);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("clearAll", clearAll)
                .add("indicesToSelect", indicesToSelect)
                .toString();
    }
}
