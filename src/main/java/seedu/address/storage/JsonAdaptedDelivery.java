package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.delivery.Address;
import seedu.address.model.delivery.Company;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Delivery}.
 */
class JsonAdaptedDelivery {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Delivery's %s field is missing!";

    private final String product;
    private final String company;
    private final String address;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedDelivery} with the given delivery details.
     */
    @JsonCreator
    public JsonAdaptedDelivery(@JsonProperty("product") String product,
                               @JsonProperty("company") String company,
                               @JsonProperty("address") String address,
                               @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.product = product;
        this.company = company;
        this.address = address;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Delivery} into this class for Jackson use.
     */
    public JsonAdaptedDelivery(Delivery source) {
        product = source.getProduct().productName;
        company = source.getCompany().value;
        address = source.getAddress().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted company object into the model's {@code Delivery} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted delivery.
     */
    public Delivery toModelType() throws IllegalValueException {
        final List<Tag> companyTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            companyTags.add(tag.toModelType());
        }

        if (product == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Product.class.getSimpleName()));
        }
        if (!Product.isValidProduct(product)) {
            throw new IllegalValueException(Product.MESSAGE_CONSTRAINTS);
        }
        final Product modelProduct = new Product(product);

        if (company == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Company.class.getSimpleName()));
        }
        if (!Company.isValidCompany(company)) {
            throw new IllegalValueException(Company.MESSAGE_CONSTRAINTS);
        }
        final Company modelCompany = new Company(company);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final Set<Tag> modelTags = new HashSet<>(companyTags);
        return new Delivery(modelProduct, modelCompany, modelAddress, modelTags);
    }

}
