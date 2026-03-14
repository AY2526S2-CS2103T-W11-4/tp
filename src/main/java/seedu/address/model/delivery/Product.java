package seedu.address.model.delivery;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the Product in the delivery list..
 * Guarantees: immutable; is valid as declared in {@link #isValidProduct(String)}
 */
public class Product {

    public static final String MESSAGE_CONSTRAINTS =
            "Product name should only contain alphanumeric characters and spaces, and it should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String productName;

    /**
     * Constructs a {@code Product}.
     *
     * @param product A valid product.
     */
    public Product(String product) {
        requireNonNull(product);
        checkArgument(isValidProduct(product), MESSAGE_CONSTRAINTS);
        productName = product;
    }

    /**
     * Returns true if a given string is a valid product.
     */
    public static boolean isValidProduct(String test) {
        return test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return productName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Product)) {
            return false;
        }

        Product otherProduct = (Product) other;
        return productName.equals(otherProduct.productName);
    }

    @Override
    public int hashCode() {
        return productName.hashCode();
    }

}
