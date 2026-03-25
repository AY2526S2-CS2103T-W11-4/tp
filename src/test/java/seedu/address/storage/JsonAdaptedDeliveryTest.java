package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedDelivery.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.delivery.Address;
import seedu.address.model.delivery.Company;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;
import seedu.address.model.tag.Tag;

public class JsonAdaptedDeliveryTest {

    private static final Delivery VALID_DELIVERY = new Delivery(new Product("Laptop"), new Company("Dell"),
            new Deadline("2026-03-25 14:30"), new Address("10 Anson Road"), Collections.singleton(new Tag("fragile")));

    private static final String VALID_PRODUCT = VALID_DELIVERY.getProduct().toString();
    private static final String VALID_COMPANY = VALID_DELIVERY.getCompany().toString();
    private static final String VALID_ADDRESS = VALID_DELIVERY.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = VALID_DELIVERY.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validDelivery_returnsDelivery() throws Exception {
        JsonAdaptedDelivery delivery = new JsonAdaptedDelivery(VALID_DELIVERY);
        assertEquals(VALID_DELIVERY, delivery.toModelType());
    }

    @Test
    public void toModelType_nullDeadline_throwsIllegalValueException() {
        JsonAdaptedDelivery delivery =
                new JsonAdaptedDelivery(VALID_PRODUCT, VALID_COMPANY, null, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Deadline.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, delivery::toModelType);
    }

    @Test
    public void toModelType_invalidDeadline_throwsIllegalValueException() {
        JsonAdaptedDelivery delivery =
                new JsonAdaptedDelivery(VALID_PRODUCT, VALID_COMPANY, "2026/03/25 14:30", VALID_ADDRESS, VALID_TAGS);
        assertThrows(IllegalValueException.class, Deadline.MESSAGE_CONSTRAINTS, delivery::toModelType);
    }
}
