package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.model.util.SampleDataUtil.getTagSet;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.deliverycommands.SelectCommand;
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

public class DeliverySelectCommandTest {

    private static final Company DELL = new Company(new Name("Dell"), new Phone("99272758"),
            new Email("dell@example.com"),
            new seedu.address.model.company.Address("Changi Business Park Central 1"),
            getTagSet("test"));

    private Model buildModelWithTwoDeliveries() {
        Delivery d1 = new Delivery(new Product("A"), DELL,
                new Deadline("2026-03-26 10:00"), Collections.emptySet());
        Delivery d2 = new Delivery(new Product("B"), DELL,
                new Deadline("2026-03-25 09:00"), Collections.emptySet());
        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        model.addDelivery(d1);
        model.addDelivery(d2);
        return model;
    }

    @Test
    public void execute_clearSelection_success() {
        Model model = buildModelWithTwoDeliveries();
        model.setDeliverySelected(model.getFilteredDeliveryList().get(0), true);
        assertFalse(model.getSelectedDeliveriesInDisplayOrder().isEmpty());

        SelectCommand command = new SelectCommand(true, List.of());
        Model expectedModel = new ModelManager(model.getAddressBook(), model.getDeliveryBook(), new UserPrefs());

        assertCommandSuccess(command, model, SelectCommand.MESSAGE_CLEAR_SUCCESS, expectedModel);
        assertTrue(model.getSelectedDeliveriesInDisplayOrder().isEmpty());
    }

    @Test
    public void execute_select_idempotent() {
        Model model = buildModelWithTwoDeliveries();
        List<Delivery> list = model.getFilteredDeliveryList();

        SelectCommand selectFirst = new SelectCommand(false, List.of(INDEX_FIRST_PERSON));
        Model expectedOneSelected = new ModelManager(model.getAddressBook(), model.getDeliveryBook(), new UserPrefs());
        expectedOneSelected.setDeliverySelected(expectedOneSelected.getFilteredDeliveryList().get(0), true);

        assertCommandSuccess(selectFirst, model,
                String.format(SelectCommand.MESSAGE_SELECT_SUCCESS, 1, 1), expectedOneSelected);
        assertEquals(List.of(list.get(0)), model.getSelectedDeliveriesInDisplayOrder());

        // selecting the same index again should not change count
        assertCommandSuccess(selectFirst, model,
                String.format(SelectCommand.MESSAGE_SELECT_SUCCESS, 1, 1), expectedOneSelected);
        assertEquals(List.of(list.get(0)), model.getSelectedDeliveriesInDisplayOrder());
    }

    @Test
    public void execute_selectMultiple_accumulates() throws Exception {
        Model model = buildModelWithTwoDeliveries();
        List<Delivery> list = model.getFilteredDeliveryList();

        new SelectCommand(false, List.of(INDEX_FIRST_PERSON)).execute(model);
        new SelectCommand(false, List.of(INDEX_SECOND_PERSON)).execute(model);

        assertEquals(List.of(list.get(0), list.get(1)), model.getSelectedDeliveriesInDisplayOrder());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = buildModelWithTwoDeliveries();
        Index outOfBound = Index.fromOneBased(99);
        assertCommandFailure(new SelectCommand(false, List.of(outOfBound)), model,
                Messages.MESSAGE_INVALID_DELIVERY_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        SelectCommand selectFirst = new SelectCommand(false, List.of(INDEX_FIRST_PERSON));
        SelectCommand selectFirstCopy = new SelectCommand(false, List.of(INDEX_FIRST_PERSON));
        SelectCommand clearAll = new SelectCommand(true, List.of());

        // same values -> true
        assertEquals(selectFirst, selectFirstCopy);

        // same object -> true
        assertTrue(selectFirst.equals(selectFirst));

        // null -> false
        assertFalse(selectFirst.equals(null));

        // different type -> false
        assertFalse(selectFirst.equals("string"));

        // clear vs select -> false
        assertFalse(selectFirst.equals(clearAll));
    }
}
