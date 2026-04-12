package seedu.address.logic.commands.companycommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.company.Company;

/**
 * Filters companies by name, address, phone, email, and/or tag.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Filters companies by parameters,\n"
            + "Parameters (Varargs): " + PREFIX_NAME + "NAME " + PREFIX_ADDRESS + "ADDRESS "
            + PREFIX_PHONE + "PHONE " + PREFIX_EMAIL + "EMAIL " + PREFIX_TAG + "TAG\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "Dell " + PREFIX_TAG + "important";

    public static final String MESSAGE_FILTER_SUCCESS = "Filtered %1$d company(s): %2$s";
    public static final String MESSAGE_NO_COMPANIES = "No companies found: %1$s";

    private final List<String> names;
    private final List<String> addresses;
    private final List<String> phones;
    private final List<String> emails;
    private final List<String> tags;

    /**
     * Creates a FilterCommand to filter companies by the given parameters.
     */
    public FilterCommand(List<String> names, List<String> addresses,
                         List<String> phones, List<String> emails, List<String> tags) {
        requireNonNull(names);
        requireNonNull(addresses);
        requireNonNull(phones);
        requireNonNull(emails);
        requireNonNull(tags);
        this.names = names;
        this.addresses = addresses;
        this.phones = phones;
        this.emails = emails;
        this.tags = tags;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Predicate<Company> matchesName = company -> names.isEmpty();
        Predicate<Company> matchesAddress = company -> addresses.isEmpty();
        Predicate<Company> matchesPhone = company -> phones.isEmpty();
        Predicate<Company> matchesEmail = company -> emails.isEmpty();
        Predicate<Company> matchesTag = company -> tags.isEmpty();

        List<String> notFound = new ArrayList<>();

        for (String name : names) {
            Predicate<Company> match = company ->
                    company.getName().fullName.toLowerCase().contains(name.toLowerCase());
            boolean hasMatch = model.getAddressBook().getCompanyList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(name);
            }
            matchesName = matchesName.or(match);
        }

        for (String address : addresses) {
            Predicate<Company> match = company ->
                    company.getAddress().value.toLowerCase().contains(address.toLowerCase());
            boolean hasMatch = model.getAddressBook().getCompanyList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(address);
            }
            matchesAddress = matchesAddress.or(match);
        }

        for (String phone : phones) {
            Predicate<Company> match = company ->
                    company.getPhone().value.contains(phone);
            boolean hasMatch = model.getAddressBook().getCompanyList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(phone);
            }
            matchesPhone = matchesPhone.or(match);
        }

        for (String email : emails) {
            Predicate<Company> match = company ->
                    company.getEmail().value.toLowerCase().contains(email.toLowerCase());
            boolean hasMatch = model.getAddressBook().getCompanyList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(email);
            }
            matchesEmail = matchesEmail.or(match);
        }

        for (String tag : tags) {
            Predicate<Company> match = company ->
                    company.getTags().stream()
                            .anyMatch(t -> t.tagName.equalsIgnoreCase(tag));
            boolean hasMatch = model.getAddressBook().getCompanyList().stream().anyMatch(match);
            if (!hasMatch) {
                notFound.add(tag);
            }
            matchesTag = matchesTag.or(match);
        }

        Predicate<Company> combined =
                matchesName.and(matchesAddress).and(matchesPhone).and(matchesEmail).and(matchesTag);

        model.updateFilteredCompanyList(combined);

        if (!notFound.isEmpty()) {
            throw new CommandException(
                    String.format(MESSAGE_NO_COMPANIES, String.join(", ", notFound)));
        }

        return new CommandResult(
                String.format(MESSAGE_FILTER_SUCCESS,
                        model.getFilteredCompanyList().size(),
                        getParameterString()));
    }

    private String getParameterString() {
        List<String> parts = new ArrayList<>();
        if (!names.isEmpty()) {
            parts.add("name: " + String.join(", ", names));
        }
        if (!addresses.isEmpty()) {
            parts.add("address: " + String.join(", ", addresses));
        }
        if (!phones.isEmpty()) {
            parts.add("phone: " + String.join(", ", phones));
        }
        if (!emails.isEmpty()) {
            parts.add("email: " + String.join(", ", emails));
        }
        if (!tags.isEmpty()) {
            parts.add("tag: " + String.join(", ", tags));
        }
        return String.join(" | ", parts);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }
        return names.equals(otherFilterCommand.names)
                && addresses.equals(otherFilterCommand.addresses)
                && phones.equals(otherFilterCommand.phones)
                && emails.equals(otherFilterCommand.emails)
                && tags.equals(otherFilterCommand.tags);
    }

    @Override
    public String toString() {
        ToStringBuilder res = new ToStringBuilder(this);
        if (!names.isEmpty()) {
            res.add("name", names);
        }
        if (!addresses.isEmpty()) {
            res.add("address", addresses);
        }
        if (!phones.isEmpty()) {
            res.add("phone", phones);
        }
        if (!emails.isEmpty()) {
            res.add("email", emails);
        }
        if (!tags.isEmpty()) {
            res.add("tag", tags);
        }
        return res.toString();
    }
}
