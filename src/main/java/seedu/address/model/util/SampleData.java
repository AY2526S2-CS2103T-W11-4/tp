package seedu.address.model.util;

import seedu.address.model.AddressBook;
import seedu.address.model.DeliveryBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDeliveryBook;
import seedu.address.model.company.Company;
import seedu.address.model.delivery.Delivery;

/**
 * Contains utility methods for populating {@code AddressBook} and {@code DeliveryBook} with sample data.
 */
public class SampleData {
    private final Company[] companies;
    private final Delivery[] deliveries;

    /**
     * Creates SampleData instance with the relevant entries
     */
    public SampleData(Company[] companies, Delivery[] deliveries) {
        this.companies = companies;
        this.deliveries = deliveries;
    }

    public Company[] getCompanies() {
        return this.companies;
    }

    public Delivery[] getDeliveries() {
        return deliveries;
    }

    public ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Company sampleCompany : getCompanies()) {
            sampleAb.addCompany(sampleCompany);
        }
        return sampleAb;
    }

    public ReadOnlyDeliveryBook getSampleDeliveryBook() {
        DeliveryBook sampleAb = new DeliveryBook();
        for (Delivery sampleDelivery : getDeliveries()) {
            sampleAb.addDelivery(sampleDelivery);
        }
        return sampleAb;
    }
}
