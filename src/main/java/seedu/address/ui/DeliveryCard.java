package seedu.address.ui;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.delivery.Delivery;

/**
 * An UI component that displays information of a {@code Delivery}.
 */
public class DeliveryCard extends UiPart<Region> {

    private static final String FXML = "DeliveryListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Delivery delivery;

    @FXML
    private HBox cardPane;
    @FXML
    private CheckBox selectDeliveryCheckBox;
    @FXML
    private Label product;
    @FXML
    private Label id;
    @FXML
    private Label company;
    @FXML
    private Label deadline;
    @FXML
    private Label address;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code DeliveryCard} with the given {@code Delivery} and index to display.
     */
    public DeliveryCard(Delivery delivery, int displayedIndex, boolean isSelected,
                        Consumer<Boolean> onSelectionChanged) {
        super(FXML);
        this.delivery = delivery;
        id.setText(displayedIndex + ". ");
        product.setText(delivery.getProduct().productName);
        company.setText(delivery.getCompany().getName().toString());
        deadline.setText("Deadline: " + delivery.getDeadline());
        address.setText(delivery.getCompany().getAddress().value);
        selectDeliveryCheckBox.setSelected(isSelected);
        selectDeliveryCheckBox.setOnAction(event ->
                onSelectionChanged.accept(selectDeliveryCheckBox.isSelected()));
        delivery.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));

        boolean isDelivered = delivery.getTags().stream()
                .anyMatch(tag -> tag.tagName.equalsIgnoreCase("delivered"));

        boolean isOverdue = delivery.getDeadline().getValue().isBefore(LocalDateTime.now());

        if (isDelivered) {
            // Subtle success tint — theme green at low opacity
            cardPane.setStyle("-fx-background-color: rgba(21,128,61,0.07);");
        } else if (isOverdue) {
            // Subtle error tint — theme red at low opacity
            cardPane.setStyle("-fx-background-color: rgba(185,28,28,0.07);");
        } else {
            // Does not change the UI
        }
    }
}
