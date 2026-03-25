package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import seedu.address.model.delivery.Address;
import seedu.address.model.delivery.Company;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;

public class SortCommandTest {

    @Test
    public void execute_companyDeliveriesSortedByDeadline_success() throws Exception {
        Delivery laterDell = new Delivery(new Product("Laptop"), new Company("Dell"),
                new Deadline("2026-03-26 10:00"), new Address("10 Anson Road"), Collections.emptySet());
        Delivery earlierDell = new Delivery(new Product("Printer"), new Company("Dell"),
                new Deadline("2026-03-25 09:00"), new Address("11 Anson Road"), Collections.emptySet());
        Delivery acerDelivery = new Delivery(new Product("Monitor"), new Company("Acer"),
                new Deadline("2026-03-24 08:00"), new Address("12 Anson Road"), Collections.emptySet());

        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        model.addDelivery(laterDell);
        model.addDelivery(earlierDell);
        model.addDelivery(acerDelivery);

        SortCommand command = new SortCommand(new Company("Dell"));
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SORT_SUCCESS, 2, "Dell"), result.getFeedbackToUser());
        assertEquals(2, model.getFilteredDeliveryList().size());
        assertEquals(earlierDell, model.getFilteredDeliveryList().get(0));
        assertEquals(laterDell, model.getFilteredDeliveryList().get(1));
        assertEquals(List.of(earlierDell, laterDell, acerDelivery), model.getDeliveryBook().getDeliveryList());
    }

    @Test
    public void execute_missingCompanyDelivery_throwsCommandException() {
        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        SortCommand command = new SortCommand(new Company("Dell"));
        String expectedMessage = String.format(SortCommand.MESSAGE_NO_DELIVERIES_FOR_COMPANY, "Dell");

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
    }
}
