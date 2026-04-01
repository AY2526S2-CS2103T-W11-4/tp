package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.EditCommand;
import seedu.address.logic.commands.deliverycommands.EditCommand.EditDeliveryDescriptor;
import seedu.address.logic.commands.deliverycommands.ListCommand;
import seedu.address.logic.parser.deliveryparser.DeliveryBookParser;
import seedu.address.model.company.CompanyNameContainsKeywordsPredicate;
import seedu.address.model.delivery.Deadline;

public class DeliveryBookParserTest {

    private final DeliveryBookParser parser = new DeliveryBookParser();

    @Test
    public void parseCommand_list() throws Exception {
        assertEquals(new ListCommand(), parser.parseCommand("list"));
    }

    @Test
    public void parseCommand_editDeadline() throws Exception {
        EditDeliveryDescriptor descriptor = new EditDeliveryDescriptor();
        descriptor.setDeadline(new Deadline("2026-03-26 09:00"));

        EditCommand command = (EditCommand) parser.parseCommand("edit "
                + INDEX_FIRST_PERSON.getOneBased() + " dl/2026-03-26 09:00");

        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_editCompany() throws Exception {
        EditDeliveryDescriptor descriptor = new EditDeliveryDescriptor();
        descriptor.setCompany(new CompanyNameContainsKeywordsPredicate(List.of("Dell")));

        EditCommand command = (EditCommand) parser.parseCommand("edit "
                + INDEX_FIRST_PERSON.getOneBased() + " c/Dell");

        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }
}
