package seedu.address.logic.parser.deliveryparser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.time.LocalDate;
import java.util.List;

import seedu.address.logic.commands.deliverycommands.FilterCommand;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.company.CompanyNameContainsKeywordsPredicate;
import seedu.address.model.delivery.ProductContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new delivery FilterCommand object.
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
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(userInput, PREFIX_PRODUCT, PREFIX_COMPANY, PREFIX_TAG, PREFIX_DEADLINE);

        if (!argMultimap.getPreamble().isEmpty()
                || (!arePrefixesPresent(argMultimap, PREFIX_PRODUCT)
                && !arePrefixesPresent(argMultimap, PREFIX_COMPANY)
                && !arePrefixesPresent(argMultimap, PREFIX_TAG)
                && !arePrefixesPresent(argMultimap, PREFIX_DEADLINE))) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        List<CompanyNameContainsKeywordsPredicate> companies =
                argMultimap.getAllValues(PREFIX_COMPANY).stream()
                        .map(x -> {
                            try {
                                return ParserUtil.parseCompany(x);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();

        List<ProductContainsKeywordsPredicate> products =
                argMultimap.getAllValues(PREFIX_PRODUCT).stream()
                        .map(x -> {
                            try {
                                return ParserUtil.parseProductName(x);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();

        List<String> tags = argMultimap.getAllValues(PREFIX_TAG).stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<LocalDate[]> timeRanges =
                argMultimap.getAllValues(PREFIX_DEADLINE).stream()
                        .map(x -> {
                            try {
                                return ParserUtil.parseTimeRange(
                                        x.split("\\s+")[0], x.split("\\s+")[1]);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();

        if (products.isEmpty() && companies.isEmpty() && tags.isEmpty() && timeRanges.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        return new FilterCommand(products, companies, tags, timeRanges);
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
