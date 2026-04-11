package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.SelectCommand;
import seedu.address.logic.parser.deliveryparser.SelectCommandParser;

public class DeliverySelectCommandParserTest {

    private final SelectCommandParser parser = new SelectCommandParser();

    @Test
    public void parse_emptyArgs_failure() {
        assertParseFailure(parser, " ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_none_success() {
        assertParseSuccess(parser, "none", new SelectCommand(true, List.of()));
        assertParseSuccess(parser, "NONE", new SelectCommand(true, List.of()));
    }

    @Test
    public void parse_singleIndex_success() {
        assertParseSuccess(parser, "1", new SelectCommand(false, List.of(INDEX_FIRST_PERSON)));
    }

    @Test
    public void parse_multipleIndices_success() {
        assertParseSuccess(parser, "1 2",
                new SelectCommand(false, List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
    }
}
