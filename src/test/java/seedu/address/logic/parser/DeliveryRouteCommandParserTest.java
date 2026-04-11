package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.RouteCommand;
import seedu.address.logic.parser.deliveryparser.RouteCommandParser;

public class DeliveryRouteCommandParserTest {

    private final RouteCommandParser parser = new RouteCommandParser();

    @Test
    public void parse_extraArgs_failure() {
        assertParseFailure(parser, " 1 ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RouteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyArgs_success() {
        assertParseSuccess(parser, "", new RouteCommand());
        assertParseSuccess(parser, "   ", new RouteCommand());
    }
}
