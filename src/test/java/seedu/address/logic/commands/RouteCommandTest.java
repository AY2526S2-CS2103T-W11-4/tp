package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.model.util.SampleDataUtil.getTagSet;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.deliverycommands.RouteCommand;
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

public class RouteCommandTest {

    private static final Company DELL = new Company(new Name("Dell"), new Phone("99272758"),
            new Email("dell@example.com"),
            new seedu.address.model.company.Address("Changi Business Park Central 1"),
            getTagSet("test"));

    private Model buildModelWithDelivery() {
        Delivery d1 = new Delivery(new Product("A"), DELL,
                new Deadline("2026-03-26 10:00"), Collections.emptySet());
        Model model = new ModelManager(new seedu.address.model.AddressBook(), new DeliveryBook(), new UserPrefs());
        model.addDelivery(d1);
        return model;
    }

    @Test
    public void execute_noSelection_throwsCommandException() {
        Model model = buildModelWithDelivery();
        assertCommandFailure(new RouteCommand(), model, RouteCommand.MESSAGE_NO_SELECTION);
    }

    @Test
    public void execute_withSelection_success() throws Exception {
        Model model = buildModelWithDelivery();
        new SelectCommand(false, List.of(INDEX_FIRST_PERSON)).execute(model);
        Delivery selected = model.getFilteredDeliveryList().get(0);

        CommandResult expected = new CommandResult(
                String.format(RouteCommand.MESSAGE_SUCCESS, 1), false, false, List.of(selected));
        Model expectedModel = new ModelManager(model.getAddressBook(), model.getDeliveryBook(), new UserPrefs());
        expectedModel.setDeliverySelected(expectedModel.getFilteredDeliveryList().get(0), true);

        assertCommandSuccess(new RouteCommand(), model, expected, expectedModel);
    }
}
