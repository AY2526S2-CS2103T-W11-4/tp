package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.SortCommand;
import seedu.address.logic.parser.deliveryparser.SortCommandParser;

public class DeliverySortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_deadlinePrefix_success() {
        assertParseSuccess(parser, " d/", new SortCommand(List.of(PREFIX_DEADLINE)));
    }

    @Test
    public void parse_companyPrefix_success() {
        assertParseSuccess(parser, " c/", new SortCommand(List.of(PREFIX_COMPANY)));
    }

    @Test
    public void parse_productPrefix_success() {
        assertParseSuccess(parser, " p/", new SortCommand(List.of(PREFIX_PRODUCT)));
    }

    @Test
    public void parse_multiplePrefixes_success() {
        assertParseSuccess(parser, " p/ c/", new SortCommand(List.of(PREFIX_PRODUCT, PREFIX_COMPANY)));
        assertParseSuccess(parser, " d/ p/", new SortCommand(List.of(PREFIX_DEADLINE, PREFIX_PRODUCT)));
    }

    @Test
    public void parse_emptyInput_throwsParseException() {
        assertParseFailure(parser, " ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPrefix_throwsParseException() {
        // n/ is not a valid sort prefix
        assertParseFailure(parser, " n/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
