package seedu.address.model.delivery;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Delivery}'s {@code Product} matches any of the keywords given.
 */
public class ProductContainsKeywordsPredicate implements Predicate<Delivery> {
    private final List<String> keywords;

    public ProductContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Delivery delivery) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(delivery.getProduct().productName, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ProductContainsKeywordsPredicate)) {
            return false;
        }

        ProductContainsKeywordsPredicate otherContainsKeywordsPredicate = (ProductContainsKeywordsPredicate) other;
        return keywords.equals(otherContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
