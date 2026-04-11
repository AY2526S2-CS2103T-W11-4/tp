package seedu.address.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.delivery.Delivery;
import seedu.address.model.user.User;
import seedu.address.routing.model.RouteResult;
import seedu.address.routing.service.DeliveryRouterService;

/**
 * Panel containing the route map and plan button.
 * Follows the same UiPart pattern as CompanyListPanel and DeliveryListPanel.
 */
public class RoutePanel extends UiPart<Region> {

    private static final String FXML = "RoutePanel.fxml";

    private final Logger logger = LogsCenter.getLogger(RoutePanel.class);

    private final ObservableList<Delivery> deliveryList;
    private final Model model;
    private DeliveryRouterService routerService;

    private WebEngine webEngine;

    @FXML private Button planRoutesButton;
    @FXML private Label routeStatusLabel;
    @FXML private StackPane mapPlaceholder;

    /**
     * Creates map display of route
     * @param deliveryList
     * @param model
     */
    public RoutePanel(ObservableList<Delivery> deliveryList, Model model) {
        super(FXML);
        this.deliveryList = deliveryList;
        this.model = model;
        this.routerService = new DeliveryRouterService();
        initMap();
    }

    private void initMap() {
        WebView mapView = new WebView();
        webEngine = mapView.getEngine();

        URL mapUrl = getClass().getResource("/view/route-map.html");
        if (mapUrl != null) {
            webEngine.load(mapUrl.toExternalForm());
        } else {
            logger.warning("route-map.html not found in resources");
        }

        mapPlaceholder.getChildren().add(mapView);
    }

    @FXML
    private void handlePlanRoutes() {
        planRoutesFor(deliveryList.stream().collect(Collectors.toList()));
    }

    /**
     * Plans routes for the given deliveries and updates the route map.
     */
    public void planRoutesFor(List<Delivery> deliveries) {
        if (deliveries.isEmpty()) {
            routeStatusLabel.setText("No deliveries to route. Add some deliveries first.");
            return;
        }

        User user = model.getUser();
        List<Delivery> deliveriesToRoute = new ArrayList<>(deliveries);

        planRoutesButton.setDisable(true);
        routeStatusLabel.setText("Planning routes for "
                + user.getCompany().getName().fullName + "... please wait.");

        Task<RouteResult> task = new Task<>() {
            @Override
            protected RouteResult call() throws Exception {
                return routerService.planRoutes(deliveriesToRoute, user);
            }
        };

        task.setOnSucceeded(e -> {
            planRoutesButton.setDisable(false);
            RouteResult result = task.getValue();
            drawRoutesOnMap(result);

            int totalStops = result.routes.stream().mapToInt(r -> r.stops.size()).sum();
            String status = String.format("Done! %d stops assigned.", totalStops);
            if (!result.unassigned.isEmpty()) {
                status += String.format(" (%d deliveries could not be assigned)",
                        result.unassigned.size());
            }
            routeStatusLabel.setText(status);
        });

        task.setOnFailed(e -> {
            planRoutesButton.setDisable(false);
            String err = task.getException().getMessage();
            routeStatusLabel.setText("Failed: " + err);
            logger.warning("Route planning failed: " + err);
            webEngine.executeScript("showError('" + escapeJs(err) + "')");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void drawRoutesOnMap(RouteResult result) {
        JSONArray routesJson = new JSONArray();

        for (RouteResult.VehicleRoute vehicle : result.routes) {
            JSONObject vObj = new JSONObject();
            vObj.put("vehicleId", vehicle.vehicleId);

            JSONArray stopsArr = new JSONArray();
            for (RouteResult.Stop stop : vehicle.stops) {
                JSONObject sObj = new JSONObject();
                sObj.put("address", stop.address);
                sObj.put("lat", stop.lat);
                sObj.put("lon", stop.lon);
                sObj.put("arrivalTime", stop.arrivalTimeFormatted);
                stopsArr.put(sObj);
            }
            vObj.put("stops", stopsArr);
            vObj.put("depotLat", vehicle.depotLat);
            vObj.put("depotLon", vehicle.depotLon);

            // Serialise road-following geometry as [[lon,lat],...]
            if (vehicle.geometry != null && !vehicle.geometry.isEmpty()) {
                JSONArray geomArr = new JSONArray();
                for (double[] pt : vehicle.geometry) {
                    geomArr.put(new JSONArray().put(pt[0]).put(pt[1]));
                }
                vObj.put("geometry", geomArr);
            }

            routesJson.put(vObj);
        }

        String json = escapeJs(routesJson.toString());
        webEngine.executeScript("drawRoutes('" + json + "')");
    }

    private String escapeJs(String s) {
        return s.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
