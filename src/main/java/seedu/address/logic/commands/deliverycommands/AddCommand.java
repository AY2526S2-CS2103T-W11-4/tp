package seedu.address.logic.commands.deliverycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.company.Company;
import seedu.address.model.company.CompanyNameContainsKeywordsPredicate;
import seedu.address.model.delivery.Address;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;
import seedu.address.model.tag.Tag;

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

    private final Product product;
    private final CompanyNameContainsKeywordsPredicate name;
    private final Deadline deadline;
    private Address address;
    private final Set<Tag> tagList;

    /**
     * Creates an AddCommand to add the specified {@code Delivery}.
     */
    public AddCommand(Product product, CompanyNameContainsKeywordsPredicate name,
                      Deadline deadline, Address address, Set<Tag> tagList) {
        this.product = product;
        this.name = name;
        this.deadline = deadline;
        this.address = address;
        this.tagList = tagList;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Company company = findMatchingCompany(model, name);
        if (company == null) {
            throw new CommandException("Company not found");
        }

        if (this.address == null) {
            this.address = new Address(company.getAddress().toString());
        }

        Delivery toAdd = new Delivery(this.product, company, this.deadline, this.address, this.tagList);

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
        return product.equals(otherAddCommand.product)
                && name.equals(otherAddCommand.name)
                && deadline.equals(otherAddCommand.deadline)
                && Objects.equals(address, otherAddCommand.address)
                && tagList.equals(otherAddCommand.tagList);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("product", product)
                .add("company", name)
                .add("deadline", deadline)
                .add("address", address)
                .add("tag", tagList)
                .toString();
    }

    private static Company findMatchingCompany(Model model, CompanyNameContainsKeywordsPredicate predicate) {
        String companyName = predicate.getKeywords().get(0);
        return model.getAddressBook().getCompanyList().stream()
                .filter(company -> company.getName().toString().equalsIgnoreCase(companyName))
                .findFirst()
                .orElse(null);
    }
}
