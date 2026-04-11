package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.util.SampleDataUtil.getTagSet;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.FilterCommand;
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
import seedu.address.model.delivery.ProductContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;

public class DeliveryFilterCommandTest {

    private static final Company DELL = new Company(new Name("Dell"), new Phone("99272758"),
            new Email("dell@example.com"),
            new seedu.address.model.company.Address("Changi Business Park Central 1"),
            getTagSet("test"));
    private static final Company ACER = new Company(new Name("Acer"), new Phone("91031282"),
            new Email("acer@example.com"),
            new seedu.address.model.company.Address("313 Orchard Rd"),
            getTagSet("test"));

    private Model model;
    private Delivery laptopDell;
    private Delivery printerDell;
    private Delivery monitorAcer;

    @BeforeEach
    public void setUp() {
        laptopDell = new Delivery(new Product("Laptop"), DELL,
                new Deadline("2026-03-26 10:00"), Set.of(new Tag("urgent")));
        printerDell = new Delivery(new Product("Printer"), DELL,
                new Deadline("2026-03-25 09:00"), Collections.emptySet());
        monitorAcer = new Delivery(new Product("Monitor"), ACER,
                new Deadline("2026-03-24 08:00"), Collections.emptySet());

        model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        model.addDelivery(laptopDell);
        model.addDelivery(printerDell);
        model.addDelivery(monitorAcer);
    }

    @Test
    public void execute_filterByCompany_showsOnlyMatchingDeliveries() throws Exception {
        FilterCommand command = new FilterCommand(
                List.of(),
                List.of(new CompanyNameContainsKeywordsPredicate(List.of("Dell"))),
                List.of(),
                List.of());
        CommandResult result = command.execute(model);

        assertEquals(2, model.getFilteredDeliveryList().size());
        assertTrue(model.getFilteredDeliveryList().contains(laptopDell));
        assertTrue(model.getFilteredDeliveryList().contains(printerDell));
        assertFalse(model.getFilteredDeliveryList().contains(monitorAcer));
        assertTrue(result.getFeedbackToUser().contains("2"));
    }

    @Test
    public void execute_filterByProduct_showsOnlyMatchingDeliveries() throws Exception {
        FilterCommand command = new FilterCommand(
                List.of(new ProductContainsKeywordsPredicate(List.of("Laptop"))),
                List.of(),
                List.of(),
                List.of());
        CommandResult result = command.execute(model);

        assertEquals(1, model.getFilteredDeliveryList().size());
        assertTrue(model.getFilteredDeliveryList().contains(laptopDell));
        assertTrue(result.getFeedbackToUser().contains("1"));
    }

    @Test
    public void execute_filterByTag_showsOnlyMatchingDeliveries() throws Exception {
        FilterCommand command = new FilterCommand(
                List.of(),
                List.of(),
                List.of("urgent"),
                List.of());
        CommandResult result = command.execute(model);

        assertEquals(1, model.getFilteredDeliveryList().size());
        assertTrue(model.getFilteredDeliveryList().contains(laptopDell));
    }

    @Test
    public void execute_filterByCompanyAndProduct_showsIntersection() throws Exception {
        FilterCommand command = new FilterCommand(
                List.of(new ProductContainsKeywordsPredicate(List.of("Laptop"))),
                List.of(new CompanyNameContainsKeywordsPredicate(List.of("Dell"))),
                List.of(),
                List.of());
        CommandResult result = command.execute(model);

        assertEquals(1, model.getFilteredDeliveryList().size());
        assertTrue(model.getFilteredDeliveryList().contains(laptopDell));
    }

    @Test
    public void execute_noMatchingCompany_throwsCommandException() {
        FilterCommand command = new FilterCommand(
                List.of(),
                List.of(new CompanyNameContainsKeywordsPredicate(List.of("Unknown"))),
                List.of(),
                List.of());
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_noMatchingProduct_throwsCommandException() {
        FilterCommand command = new FilterCommand(
                List.of(new ProductContainsKeywordsPredicate(List.of("Nonexistent"))),
                List.of(),
                List.of(),
                List.of());
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void equals() {
        List<CompanyNameContainsKeywordsPredicate> dellPredicate =
                List.of(new CompanyNameContainsKeywordsPredicate(List.of("Dell")));
        List<CompanyNameContainsKeywordsPredicate> acerPredicate =
                List.of(new CompanyNameContainsKeywordsPredicate(List.of("Acer")));

        FilterCommand filterDell = new FilterCommand(List.of(), dellPredicate, List.of(), List.of());
        FilterCommand filterDellCopy = new FilterCommand(List.of(), dellPredicate, List.of(), List.of());
        FilterCommand filterAcer = new FilterCommand(List.of(), acerPredicate, List.of(), List.of());

        // same values -> true
        assertEquals(filterDell, filterDellCopy);

        // same object -> true
        assertTrue(filterDell.equals(filterDell));

        // null -> false
        assertFalse(filterDell.equals(null));

        // different company -> false
        assertFalse(filterDell.equals(filterAcer));
    }
}
