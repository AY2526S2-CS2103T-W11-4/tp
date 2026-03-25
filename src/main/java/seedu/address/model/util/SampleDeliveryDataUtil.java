package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.DeliveryBook;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.delivery.Address;
import seedu.address.model.delivery.Company;
import seedu.address.model.delivery.Deadline;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.delivery.Product;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDeliveryDataUtil {
    public static Delivery[] getSampleCompanies() {
        return new Delivery[] {
            new Delivery(new Product("laptop"), new Company("Dell"), new Deadline("2026-03-25 14:30"),
                    new Address("Blk 30 Geylang Street 29, #06-40"), getTagSet("fragile"))
        };
    }

    public static ReadOnlyDeliveryBook getSampleDeliveryBook() {
        DeliveryBook sampleAb = new DeliveryBook();
        for (Delivery sampleDelivery : getSampleCompanies()) {
            sampleAb.addDelivery(sampleDelivery);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
