package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.model.util.SampleDataUtil.getTagSet;
import static seedu.address.storage.JsonAdaptedDelivery.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;
import seedu.address.model.tag.Tag;

public class JsonAdaptedDeliveryTest {

    private static final Company VALID_COMPANY = new Company(new Name("Dell"), new Phone("99272758"),
            new Email("dell@example.com"),
            new seedu.address.model.company.Address("Changi Business Park Central 1"),
            getTagSet("test"));
    private static final Delivery VALID_DELIVERY = new Delivery(new Product("Laptop"),
            VALID_COMPANY,
            new Deadline("2026-03-25 14:30"),
            Collections.singleton(new Tag("fragile")));
    private static final String VALID_PRODUCT = VALID_DELIVERY.getProduct().toString();
    private static final String VALID_COMPANY_STRING = VALID_COMPANY.getName().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = VALID_DELIVERY.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final ObservableList<Company> EXISTING_COMPANIES =
            FXCollections.observableArrayList(VALID_COMPANY);

    @Test
    public void toModelType_validDelivery_returnsDelivery() throws Exception {
        JsonAdaptedDelivery delivery = new JsonAdaptedDelivery(VALID_DELIVERY);
        assertEquals(VALID_DELIVERY, delivery.toModelType(EXISTING_COMPANIES));
    }

    @Test
    public void toModelType_nullDeadline_throwsIllegalValueException() {
        JsonAdaptedDelivery delivery =
                new JsonAdaptedDelivery(VALID_PRODUCT, VALID_COMPANY_STRING, null,
                        VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Deadline.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, () -> delivery.toModelType(EXISTING_COMPANIES));
    }

    @Test
    public void toModelType_invalidDeadline_throwsIllegalValueException() {
        JsonAdaptedDelivery delivery =
                new JsonAdaptedDelivery(VALID_PRODUCT, VALID_COMPANY_STRING, "2026/03/25 14:30",
                        VALID_TAGS);
        assertThrows(IllegalValueException.class,
                Deadline.MESSAGE_CONSTRAINTS, () -> delivery.toModelType(EXISTING_COMPANIES));
    }
}
