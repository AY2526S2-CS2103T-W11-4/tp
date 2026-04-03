package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.util.SampleDataUtil.getTagSet;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.SortCommand;
import seedu.address.logic.parser.deliveryparser.SortCommandParser;
import seedu.address.model.company.Company;
import seedu.address.model.company.CompanyNameContainsKeywordsPredicate;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;

public class SortCommandParserTest {
    private static final Company DELL = new Company(new Name("Dell"), new Phone("99272758"),
            new Email("dell@example.com"),
            new seedu.address.model.company.Address("Changi Business Park Central 1"),
            getTagSet("test"));
    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_validArgs_returnsSortCommand() {
        assertParseSuccess(parser, " c/Dell",
                new SortCommand(List.of(new CompanyNameContainsKeywordsPredicate(List.of("Dell")))));
    }

    @Test
    public void parse_missingCompany_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

}
