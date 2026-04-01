package seedu.address.ui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.delivery.Delivery;

/**
 * Panel containing the list of deliveries.
 */
public class DeliveryListPanel extends UiPart<Region> {
    private static final String FXML = "DeliveryListPanel.fxml";

    private final ObservableSet<Delivery> selectedDeliveries = FXCollections.observableSet(new LinkedHashSet<>());
    private final Runnable onSelectionChanged;

    @FXML
    private ListView<Delivery> deliveryListView;

    /**
     * Creates a {@code DeliveryListPanel} with the given {@code ObservableList}.
     */
    public DeliveryListPanel(ObservableList<Delivery> deliveryList, Runnable onSelectionChanged) {
        super(FXML);
        this.onSelectionChanged = onSelectionChanged;
        deliveryListView.setItems(deliveryList);
        deliveryListView.setCellFactory(listView -> new DeliveryListViewCell());
        deliveryList.addListener((ListChangeListener<Delivery>) change -> pruneSelection(deliveryList));
    }

    /**
     * Returns the currently selected deliveries in display order.
     */
    public List<Delivery> getSelectedDeliveries() {
        List<Delivery> selected = new ArrayList<>();
        for (Delivery delivery : deliveryListView.getItems()) {
            if (selectedDeliveries.contains(delivery)) {
                selected.add(delivery);
            }
        }
        return selected;
    }

    private void setSelected(Delivery delivery, boolean isSelected) {
        if (isSelected) {
            selectedDeliveries.add(delivery);
        } else {
            selectedDeliveries.remove(delivery);
        }
        onSelectionChanged.run();
    }

    private void pruneSelection(ObservableList<Delivery> deliveryList) {
        if (selectedDeliveries.retainAll(new ArrayList<>(deliveryList))) {
            onSelectionChanged.run();
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Delivery} using a {@code DeliveryCard}.
     */
    class DeliveryListViewCell extends ListCell<Delivery> {
        @Override
        protected void updateItem(Delivery delivery, boolean empty) {
            super.updateItem(delivery, empty);

            if (empty || delivery == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new DeliveryCard(delivery, getIndex() + 1,
                        selectedDeliveries.contains(delivery),
                        isSelected -> setSelected(delivery, isSelected)).getRoot());
            }
        }
    }
}
