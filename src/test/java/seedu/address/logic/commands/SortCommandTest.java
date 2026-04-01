package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.model.util.SampleDataUtil.getTagSet;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.SortCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.DeliveryBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;
import seedu.address.model.company.CompanyNameContainsKeywordsPredicate;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;

public class SortCommandTest {
    private static final Company DELL = new Company(new Name("Dell"), new Phone("99272758"),
            new Email("dell@example.com"),
            new seedu.address.model.company.Address("Changi Business Park Central 1"),
            getTagSet("test"));
    private static final Company ACER = new Company(new Name("Acer"), new Phone("91031282"),
            new Email("acer@example.com"),
            new seedu.address.model.company.Address("313 Orchard Rd"),
            getTagSet("test"));

    @Test
    public void execute_companyDeliveriesSortedByDeadline_success() throws Exception {
        Delivery laterDell = new Delivery(new Product("Laptop"), DELL,
                new Deadline("2026-03-26 10:00"), Collections.emptySet());
        Delivery earlierDell = new Delivery(new Product("Printer"), DELL,
                new Deadline("2026-03-25 09:00"), Collections.emptySet());
        Delivery acerDelivery = new Delivery(new Product("Monitor"), ACER,
                new Deadline("2026-03-24 08:00"), Collections.emptySet());

        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        model.addDelivery(laterDell);
        model.addDelivery(earlierDell);
        model.addDelivery(acerDelivery);

        SortCommand command = new SortCommand(new CompanyNameContainsKeywordsPredicate(List.of("Dell")));
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SORT_SUCCESS, 2, "Dell"),
                result.getFeedbackToUser());
        assertEquals(2, model.getFilteredDeliveryList().size());
        assertEquals(earlierDell, model.getFilteredDeliveryList().get(0));
        assertEquals(laterDell, model.getFilteredDeliveryList().get(1));
        assertEquals(List.of(earlierDell, laterDell, acerDelivery), model.getDeliveryBook().getDeliveryList());
    }

    @Test
    public void execute_missingCompanyDelivery_throwsCommandException() {
        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        SortCommand command = new SortCommand(new CompanyNameContainsKeywordsPredicate(List.of("Dell")));
        String expectedMessage = String.format(SortCommand.MESSAGE_NO_DELIVERIES_FOR_COMPANY, "Dell");

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
    }
}
