package seedu.address.logic.commands.deliverycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_DELIVERIES;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.delivery.Address;
import seedu.address.model.delivery.Company;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing delivery in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the delivery identified "
            + "by the index number used in the displayed delivery list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_PRODUCT + "PRODUCT] "
            + "[" + PREFIX_COMPANY + "delivery] "
            + "[" + PREFIX_DEADLINE + "DEADLINE] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PRODUCT + "Laptop"
            + PREFIX_COMPANY + "Dell "
            + PREFIX_DEADLINE + "2026-03-25 14:30";

    public static final String MESSAGE_EDIT_DELIVERY_SUCCESS = "Edited Delivery: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_DELIVERY = "This Delivery already exists in the delivery book.";

    private final Index index;
    private final EditDeliveryDescriptor editDeliveryDescriptor;

    /**
     * @param index of the delivery in the filtered delivery list to edit
     * @param editDeliveryDescriptor details to edit the delivery with
     */
    public EditCommand(Index index, EditDeliveryDescriptor editDeliveryDescriptor) {
        requireNonNull(index);
        requireNonNull(editDeliveryDescriptor);

        this.index = index;
        this.editDeliveryDescriptor = new EditDeliveryDescriptor(editDeliveryDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Delivery> lastShownList = model.getFilteredDeliveryList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_DELIVERY_DISPLAYED_INDEX);
        }

        Delivery deliveryToEdit = lastShownList.get(index.getZeroBased());
        Delivery editedDelivery = createEditedDelivery(deliveryToEdit, editDeliveryDescriptor);

        if (!deliveryToEdit.isSameDelivery(editedDelivery) && model.hasDelivery(editedDelivery)) {
            throw new CommandException(MESSAGE_DUPLICATE_DELIVERY);
        }

        model.setDelivery(deliveryToEdit, editedDelivery);
        model.updateFilteredDeliveryList(PREDICATE_SHOW_ALL_DELIVERIES);
        return new CommandResult(String.format(MESSAGE_EDIT_DELIVERY_SUCCESS, Messages.format(editedDelivery)));
    }

    /**
     * Creates and returns a {@code delivery} with the details of {@code deliveryToEdit}
     * edited with {@code editDeliveryDescriptor}.
     */
    private static Delivery createEditedDelivery(
            Delivery deliveryToEdit, EditDeliveryDescriptor editDeliveryDescriptor) {
        assert deliveryToEdit != null;

        Product updatedProduct = editDeliveryDescriptor.getProduct().orElse(deliveryToEdit.getProduct());
        Company updatedCompany = editDeliveryDescriptor.getCompany().orElse(deliveryToEdit.getCompany());
        Deadline updatedDeadline = editDeliveryDescriptor.getDeadline().orElse(deliveryToEdit.getDeadline());
        Address updatedAddress = editDeliveryDescriptor.getAddress().orElse(deliveryToEdit.getAddress());
        Set<Tag> updatedTags = editDeliveryDescriptor.getTags().orElse(deliveryToEdit.getTags());

        return new Delivery(updatedProduct, updatedCompany, updatedDeadline, updatedAddress, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editDeliveryDescriptor.equals(otherEditCommand.editDeliveryDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editDeliveryDescriptor", editDeliveryDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the delivery with. Each non-empty field value will replace the
     * corresponding field value of the delivery.
     */
    public static class EditDeliveryDescriptor {
        private Product product;
        private Company company;
        private Deadline deadline;
        private Address address;
        private Set<Tag> tags;

        public EditDeliveryDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditDeliveryDescriptor(EditDeliveryDescriptor toCopy) {
            setProduct(toCopy.product);
            setCompany(toCopy.company);
            setDeadline(toCopy.deadline);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(product, company, deadline, address, tags);
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Optional<Product> getProduct() {
            return Optional.ofNullable(product);
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        public Optional<Company> getCompany() {
            return Optional.ofNullable(company);
        }

        public void setDeadline(Deadline deadline) {
            this.deadline = deadline;
        }

        public Optional<Deadline> getDeadline() {
            return Optional.ofNullable(deadline);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditDeliveryDescriptor)) {
                return false;
            }

            EditDeliveryDescriptor otherEditDeliveryDescriptor = (EditDeliveryDescriptor) other;
            return Objects.equals(product, otherEditDeliveryDescriptor.product)
                    && Objects.equals(company, otherEditDeliveryDescriptor.company)
                    && Objects.equals(deadline, otherEditDeliveryDescriptor.deadline)
                    && Objects.equals(address, otherEditDeliveryDescriptor.address)
                    && Objects.equals(tags, otherEditDeliveryDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("product", product)
                    .add("company", company)
                    .add("deadline", deadline)
                    .add("address", address)
                    .add("tags", tags)
                    .toString();
        }
    }
}
