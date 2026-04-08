package seedu.address.logic.parser.companyparser;

import seedu.address.logic.commands.deliverycommands.FilterCommand;
import seedu.address.logic.parser.*;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.company.CompanyNameContainsKeywordsPredicate;
import seedu.address.model.delivery.ProductContainsKeywordsPredicate;

import java.time.LocalDate;
import java.util.List;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.*;

/**
 * Parses input arguments and creates a new FilterCommand object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PRODUCT, PREFIX_COMPANY, PREFIX_DEADLINE);

        if (!argMultimap.getPreamble().isEmpty()
                || (!arePrefixesPresent(argMultimap, PREFIX_PRODUCT)
                && !arePrefixesPresent(argMultimap, PREFIX_COMPANY)
                && !arePrefixesPresent(argMultimap, PREFIX_DEADLINE))) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        List<CompanyNameContainsKeywordsPredicate> companies = argMultimap.getAllValues(PREFIX_COMPANY).stream()
                .map(x -> {
                    try {
                        return ParserUtil.parseCompany(x);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        List<ProductContainsKeywordsPredicate> products = argMultimap.getAllValues(PREFIX_PRODUCT).stream()
                .map(x -> {
                    try {
                        return ParserUtil.parseProductName(x);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        List<LocalDate[]> timeRanges = argMultimap.getAllValues(PREFIX_DEADLINE).stream()
                .map(x -> {
                    try {
                        return ParserUtil.parseTimeRange(x.split("\\s+")[0], x.split("\\s+")[1]);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        if (products.isEmpty() && companies.isEmpty() && timeRanges.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }
        return new FilterCommand(products, companies, timeRanges);
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
