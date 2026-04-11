package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.FilterCommand;
import seedu.address.logic.parser.deliveryparser.FilterCommandParser;
import seedu.address.model.company.CompanyNameContainsKeywordsPredicate;
import seedu.address.model.delivery.ProductContainsKeywordsPredicate;

public class DeliveryFilterCommandParserTest {

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_companyOnly_success() {
        FilterCommand expected = new FilterCommand(
                List.of(),
                List.of(new CompanyNameContainsKeywordsPredicate(List.of("Dell"))),
                List.of(),
                List.of());
        assertParseSuccess(parser, " c/Dell", expected);
    }

    @Test
    public void parse_productOnly_success() {
        FilterCommand expected = new FilterCommand(
                List.of(new ProductContainsKeywordsPredicate(List.of("Laptop"))),
                List.of(),
                List.of(),
                List.of());
        assertParseSuccess(parser, " p/Laptop", expected);
    }

    @Test
    public void parse_tagOnly_success() {
        FilterCommand expected = new FilterCommand(
                List.of(),
                List.of(),
                List.of("urgent"),
                List.of());
        assertParseSuccess(parser, " t/urgent", expected);
    }

    @Test
    public void parse_companyAndProduct_success() {
        FilterCommand expected = new FilterCommand(
                List.of(new ProductContainsKeywordsPredicate(List.of("Laptop"))),
                List.of(new CompanyNameContainsKeywordsPredicate(List.of("Dell"))),
                List.of(),
                List.of());
        assertParseSuccess(parser, " c/Dell p/Laptop", expected);
    }

    @Test
    public void parse_emptyInput_throwsParseException() {
        assertParseFailure(parser, "  ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noValidPrefix_throwsParseException() {
        assertParseFailure(parser, " n/SomeValue",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }
}
