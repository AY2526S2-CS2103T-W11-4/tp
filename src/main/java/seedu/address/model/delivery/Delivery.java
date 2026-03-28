package seedu.address.model.delivery;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.company.Company;
import seedu.address.model.tag.Tag;

/**
 * Represents a Delivery in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Delivery {

    // Identity fields
    private final Product product;
    private final Company company;
    private final Deadline deadline;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Delivery(Product product, Company company, Deadline deadline, Address address, Set<Tag> tags) {
        requireAllNonNull(product, company, deadline, address, tags);
        this.product = product;
        this.company = company;
        this.deadline = deadline;
        this.address = address;
        this.tags.addAll(tags);
    }

    public Product getProduct() {
        return product;
    }

    public Company getCompany() {
        return company;
    }

    public Deadline getDeadline() {
        return deadline;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same product.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSameDelivery(Delivery otherDelivery) {
        if (otherDelivery == this) {
            return true;
        }

        return otherDelivery != null
                && otherDelivery.getProduct().equals(getProduct())
                && otherDelivery.getCompany().equals(getCompany())
                && otherDelivery.getDeadline().equals(getDeadline());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Delivery)) {
            return false;
        }

        Delivery otherDelivery = (Delivery) other;
        return product.equals(otherDelivery.product)
                && company.equals(otherDelivery.company)
                && deadline.equals(otherDelivery.deadline)
                && address.equals(otherDelivery.address)
                && tags.equals(otherDelivery.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(product, company, deadline, address, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("product", product)
                .add("company", company)
                .add("deadline", deadline)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }

}
