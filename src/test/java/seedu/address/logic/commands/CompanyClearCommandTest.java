package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalCompanies.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.companycommands.ClearCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.DeliveryBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class CompanyClearCommandTest {

    @Test
    public void execute_emptyCompanyBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyCompanyBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new DeliveryBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new DeliveryBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
