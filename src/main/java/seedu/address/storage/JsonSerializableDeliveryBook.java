package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.DeliveryBook;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.delivery.Delivery;

/**
 * An Immutable DeliveryBook that is serializable to JSON format.
 */
@JsonRootName(value = "deliverybook")
class JsonSerializableDeliveryBook {

    public static final String MESSAGE_DUPLICATE_DELIVERY = "deliveries list contains duplicate delivery(s).";

    private final List<JsonAdaptedDelivery> deliveries = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableDeliveryBook} with the given deliveries.
     */
    @JsonCreator
    public JsonSerializableDeliveryBook(@JsonProperty("persons") List<JsonAdaptedDelivery> deliveries) {
        if (deliveries != null) {
            this.deliveries.addAll(deliveries);
        }
    }

    /**
     * Converts a given {@code ReadOnlyDeliveryBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableDeliveryBook}.
     */
    public JsonSerializableDeliveryBook(ReadOnlyDeliveryBook source) {
        deliveries.addAll(source.getDeliveryList().stream()
                .map(JsonAdaptedDelivery::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code DeliveryBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public DeliveryBook toModelType() throws IllegalValueException {
        DeliveryBook addressBook = new DeliveryBook();
        for (JsonAdaptedDelivery jsonAdaptedDelivery : deliveries) {
            Delivery delivery = jsonAdaptedDelivery.toModelType();
            if (addressBook.hasDelivery(delivery)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_DELIVERY);
            }
            addressBook.addDelivery(delivery);
        }
        return addressBook;
    }

}
