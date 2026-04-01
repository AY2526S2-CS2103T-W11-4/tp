package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.company.Company;
import seedu.address.model.delivery.Deadline;
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
    private final String deadline;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedDelivery} with the given delivery details.
     */
    @JsonCreator
    public JsonAdaptedDelivery(@JsonProperty("product") String product,
                               @JsonProperty("company") String company,
                               @JsonProperty("deadline") String deadline,
                               @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.product = product;
        this.company = company;
        this.deadline = deadline;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Delivery} into this class for Jackson use.
     */
    public JsonAdaptedDelivery(Delivery source) {
        product = source.getProduct().productName;
        company = source.getCompany().getName().toString();
        deadline = source.getDeadline().toStorageString();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted company object into the model's {@code Delivery} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted delivery.
     */
    public Delivery toModelType(ObservableList<seedu.address.model.company.Company> existingCompanies)
            throws IllegalValueException {
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
        if (!company.matches("[^\\s].*")) {
            throw new IllegalValueException("Company should be a string that does not begin with a whitespace");
        }
        seedu.address.model.company.Company modelCompany = existingCompanies.stream()
                .filter(c -> c.getName().toString().equalsIgnoreCase(company))
                .findFirst()
                .orElseThrow(() -> new IllegalValueException(
                        "Unable to find Company for this Delivery"
                ));

        if (deadline == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Deadline.class.getSimpleName()));
        }
        if (!Deadline.isValidDeadline(deadline)) {
            throw new IllegalValueException(Deadline.MESSAGE_CONSTRAINTS);
        }
        final Deadline modelDeadline = new Deadline(deadline);

        final Set<Tag> modelTags = new HashSet<>(companyTags);
        return new Delivery(modelProduct, modelCompany, modelDeadline, modelTags);
    }

}
