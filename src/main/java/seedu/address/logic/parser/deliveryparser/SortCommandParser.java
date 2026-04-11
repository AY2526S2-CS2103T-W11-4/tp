package seedu.address.logic.parser.deliveryparser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;

import java.util.Arrays;
import java.util.List;

import seedu.address.logic.commands.deliverycommands.SortCommand;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new company SortCommand object.
 */
public class SortCommandParser implements Parser<SortCommand> {

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public SortCommand parse(String userInput) throws ParseException {
        String[] prefixString = userInput.trim().split("\\s+");
        if (!Arrays.stream(prefixString).allMatch(
                x -> PREFIX_PRODUCT.equals(new Prefix(x))
                || PREFIX_COMPANY.equals(new Prefix(x))
                || PREFIX_DEADLINE.equals(new Prefix(x)))) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        List<Prefix> prefixes = Arrays.stream(prefixString)
                .map(Prefix::new)
                .toList();
        return new SortCommand(prefixes);
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
