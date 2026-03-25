package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.deliverycommands.SortCommand;
import seedu.address.logic.parser.deliveryparser.SortCommandParser;
import seedu.address.model.delivery.Company;

public class SortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_validArgs_returnsSortCommand() {
        assertParseSuccess(parser, " c/Dell", new SortCommand(new Company("Dell")));
    }

    @Test
    public void parse_missingCompany_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateCompany_throwsParseException() {
        assertParseFailure(parser, " c/Dell c/Acer",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_COMPANY));
    }
}
