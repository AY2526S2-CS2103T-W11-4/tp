package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRODUCT;
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
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;

public class DeliverySortCommandTest {

    private static final Company ACER = new Company(new Name("Acer"), new Phone("91031282"),
            new Email("acer@example.com"),
            new seedu.address.model.company.Address("313 Orchard Rd"),
            getTagSet());
    private static final Company DELL = new Company(new Name("Dell"), new Phone("99272758"),
            new Email("dell@example.com"),
            new seedu.address.model.company.Address("Changi Business Park Central 1"),
            getTagSet());
    private static final Company LENOVO = new Company(new Name("Lenovo"), new Phone("88889999"),
            new Email("lenovo@example.com"),
            new seedu.address.model.company.Address("1 Fusionopolis Way"),
            getTagSet());

    /** Builds a model with three deliveries: laptop/Dell/late, monitor/Acer/early, zebra/Lenovo/mid. */
    private Model buildModelWithThreeDeliveries() {
        Delivery laptopDell = new Delivery(new Product("Laptop"), DELL,
                new Deadline("2026-03-26 10:00"), Collections.emptySet());
        Delivery monitorAcer = new Delivery(new Product("Monitor"), ACER,
                new Deadline("2026-03-24 08:00"), Collections.emptySet());
        Delivery zebraLenovo = new Delivery(new Product("Zebra Printer"), LENOVO,
                new Deadline("2026-03-25 09:00"), Collections.emptySet());

        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        model.addDelivery(laptopDell);
        model.addDelivery(monitorAcer);
        model.addDelivery(zebraLenovo);
        return model;
    }

    @Test
    public void execute_sortByDeadline_sortsAscending() throws Exception {
        Model model = buildModelWithThreeDeliveries();
        SortCommand command = new SortCommand(List.of(PREFIX_DEADLINE));
        CommandResult result = command.execute(model);

        List<Delivery> sorted = model.getFilteredDeliveryList();
        assertEquals(3, sorted.size());
        // earliest deadline first
        assertEquals(new Deadline("2026-03-24 08:00"), sorted.get(0).getDeadline());
        assertEquals(new Deadline("2026-03-25 09:00"), sorted.get(1).getDeadline());
        assertEquals(new Deadline("2026-03-26 10:00"), sorted.get(2).getDeadline());
        assertTrue(result.getFeedbackToUser().contains("deadline"));
    }

    @Test
    public void execute_sortByProduct_sortsAlphabetically() throws Exception {
        Model model = buildModelWithThreeDeliveries();
        SortCommand command = new SortCommand(List.of(PREFIX_PRODUCT));
        CommandResult result = command.execute(model);

        List<Delivery> sorted = model.getFilteredDeliveryList();
        assertEquals(3, sorted.size());
        // Laptop < Monitor < Zebra Printer alphabetically
        assertEquals("Laptop", sorted.get(0).getProduct().getName());
        assertEquals("Monitor", sorted.get(1).getProduct().getName());
        assertEquals("Zebra Printer", sorted.get(2).getProduct().getName());
        assertTrue(result.getFeedbackToUser().contains("product"));
    }

    @Test
    public void execute_sortByCompany_sortsAlphabetically() throws Exception {
        Model model = buildModelWithThreeDeliveries();
        SortCommand command = new SortCommand(List.of(PREFIX_COMPANY));
        CommandResult result = command.execute(model);

        List<Delivery> sorted = model.getFilteredDeliveryList();
        assertEquals(3, sorted.size());
        // Acer < Dell < Lenovo alphabetically
        assertEquals("Acer", sorted.get(0).getCompany().getName().toString());
        assertEquals("Dell", sorted.get(1).getCompany().getName().toString());
        assertEquals("Lenovo", sorted.get(2).getCompany().getName().toString());
        assertTrue(result.getFeedbackToUser().contains("company"));
    }

    @Test
    public void execute_emptyDeliveryList_throwsCommandException() {
        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        SortCommand command = new SortCommand(List.of(PREFIX_DEADLINE));
        assertThrows(CommandException.class, SortCommand.MESSAGE_NO_DELIVERIES, () -> command.execute(model));
    }

    @Test
    public void equals() {
        SortCommand sortByDeadline = new SortCommand(List.of(PREFIX_DEADLINE));
        SortCommand sortByDeadlineCopy = new SortCommand(List.of(PREFIX_DEADLINE));
        SortCommand sortByProduct = new SortCommand(List.of(PREFIX_PRODUCT));
        SortCommand sortByMultiple = new SortCommand(List.of(PREFIX_PRODUCT, PREFIX_COMPANY));

        // same values -> true
        assertEquals(sortByDeadline, sortByDeadlineCopy);

        // same object -> true
        assertTrue(sortByDeadline.equals(sortByDeadline));

        // null -> false
        assertFalse(sortByDeadline.equals(null));

        // different type -> false
        assertFalse(sortByDeadline.equals("string"));

        // different prefix -> false
        assertFalse(sortByDeadline.equals(sortByProduct));

        // different number of prefixes -> false
        assertFalse(sortByProduct.equals(sortByMultiple));
    }
}
