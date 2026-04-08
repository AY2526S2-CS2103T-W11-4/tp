package seedu.address.logic.parser.deliveryparser;

import seedu.address.logic.commands.deliverycommands.SortCommand;
import seedu.address.logic.parser.*;
import seedu.address.logic.parser.exceptions.ParseException;

import java.util.Arrays;
import java.util.List;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.*;

public class SortCommandParser implements Parser<SortCommand> {
    public SortCommand parse(String args) throws ParseException {
        String[] prefixString = args.trim().split("\\s+");
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
