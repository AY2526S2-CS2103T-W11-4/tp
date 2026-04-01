package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.model.util.SampleDataUtil.getTagSet;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.ListCommand;
import seedu.address.model.DeliveryBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.delivery.Address;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;

public class DeliveryListCommandTest {
    private static final Company DELL = new Company(new Name("Dell"), new Phone("99272758"),
            new Email("dell@example.com"),
            new seedu.address.model.company.Address("Changi Business Park Central 1"),
            getTagSet("test"));
    private static final Company ACER = new Company(new Name("Acer"), new Phone("91031282"),
            new Email("acer@example.com"),
            new seedu.address.model.company.Address("313 Orchard Rd"),
            getTagSet("test"));

    @Test
    public void execute_listShowsAllDeliveriesSortedByDeadline_success() throws Exception {
        Delivery latest = new Delivery(new Product("Laptop"), DELL,
                new Deadline("2026-03-26 10:00"), new Address("10 Anson Road"), getTagSet());
        Delivery earliest = new Delivery(new Product("Monitor"), ACER,
                new Deadline("2026-03-24 08:00"), new Address("12 Anson Road"), getTagSet());
        Delivery middle = new Delivery(new Product("Printer"), DELL,
                new Deadline("2026-03-25 09:00"), new Address("11 Anson Road"), getTagSet());

        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        model.addDelivery(latest);
        model.addDelivery(earliest);
        model.addDelivery(middle);
        model.updateFilteredDeliveryList(delivery -> delivery.getCompany().equals(DELL));

        ListCommand command = new ListCommand();
        CommandResult result = command.execute(model);

        assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(3, model.getFilteredDeliveryList().size());
        assertEquals(List.of(earliest, middle, latest), model.getFilteredDeliveryList());
        assertEquals(List.of(earliest, middle, latest), model.getDeliveryBook().getDeliveryList());
    }
}
