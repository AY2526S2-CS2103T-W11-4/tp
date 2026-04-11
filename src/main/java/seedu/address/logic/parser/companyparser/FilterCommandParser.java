package seedu.address.logic.parser.companyparser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.logic.commands.companycommands.FilterCommand;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new company FilterCommand object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterCommand parse(String userInput) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                userInput, PREFIX_COMPANY, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_TAG);

        if (!argMultimap.getPreamble().isEmpty()
                || (!arePrefixesPresent(argMultimap, PREFIX_COMPANY)
                && !arePrefixesPresent(argMultimap, PREFIX_ADDRESS)
                && !arePrefixesPresent(argMultimap, PREFIX_PHONE)
                && !arePrefixesPresent(argMultimap, PREFIX_EMAIL)
                && !arePrefixesPresent(argMultimap, PREFIX_TAG))) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        List<String> names = argMultimap.getAllValues(PREFIX_COMPANY).stream()
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        List<String> addresses = argMultimap.getAllValues(PREFIX_ADDRESS).stream()
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        List<String> phones = argMultimap.getAllValues(PREFIX_PHONE).stream()
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        List<String> emails = argMultimap.getAllValues(PREFIX_EMAIL).stream()
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        List<String> tags = argMultimap.getAllValues(PREFIX_TAG).stream()
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        if (names.isEmpty() && addresses.isEmpty() && phones.isEmpty()
                && emails.isEmpty() && tags.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        return new FilterCommand(names, addresses, phones, emails, tags);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        for (Prefix prefix : prefixes) {
            if (!argumentMultimap.getValue(prefix).isPresent()) {
                return false;
            }
        }
        return true;
    }
}
