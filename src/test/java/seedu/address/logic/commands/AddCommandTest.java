package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalCompanies.ALICE;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.companycommands.AddCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.DeliveryBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.company.Company;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.user.User;
import seedu.address.testutil.CompanyBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Company validPerson = new CompanyBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Company validPerson = new CompanyBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_COMPANY, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Company alice = new CompanyBuilder().withName("Alice").build();
        Company bob = new CompanyBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean getCompanyPackage() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCompanyPackage(boolean isCompanyPackage) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setDeliveryBookFilePath(Path deliveryBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addCompany(Company person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setDeliveryBook(ReadOnlyDeliveryBook deliveryBook) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyDeliveryBook getDeliveryBook() {
            return new DeliveryBook();
        }

        @Override
        public boolean hasCompany(Company person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteCompany(Company target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCompany(Company target, Company editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Company> getFilteredCompanyList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredCompanyList(Predicate<Company> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setDelivery(Delivery target, Delivery editedDelivery) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void sortDeliveriesByDeadline(Predicate<Delivery> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasDelivery(Delivery delivery) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addDelivery(Delivery delivery) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteDelivery(Delivery delivery) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Delivery> getFilteredDeliveryList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredDeliveryList(Predicate<Delivery> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableSet<Delivery> getDeliverySelection() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearDeliverySelection() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void toggleDeliverySelection(Index index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setDeliverySelected(Delivery delivery, boolean selected) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public List<Delivery> getSelectedDeliveriesInDisplayOrder() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public User getUser() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public StringProperty getUserAddress() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setUser(User user) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Company person;

        ModelStubWithPerson(Company person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public boolean hasCompany(Company person) {
            requireNonNull(person);
            return this.person.isSameCompany(person);
        }
    }

    /**
     * A Model stub that always accept the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Company> personsAdded = new ArrayList<>();

        @Override
        public boolean hasCompany(Company person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSameCompany);
        }

        @Override
        public void addCompany(Company person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
