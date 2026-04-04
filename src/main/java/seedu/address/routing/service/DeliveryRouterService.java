package seedu.address.routing.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import seedu.address.model.delivery.Delivery;
import seedu.address.model.user.User;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.routing.client.OrsHttpClient;
import seedu.address.routing.model.Coordinate;
import seedu.address.routing.model.RouteResult;

/**
 * Orchestrates the full routing pipeline:
 *   1. Geocode depot address from User's company
 *   2. Geocode all delivery addresses
 *   3. Call ORS optimization using User's vehicle profile
 */
public class DeliveryRouterService {

    // Default service time per stop: 5 minutes
    private static final int DEFAULT_SERVICE_SECS = 300;

    private final GeocodingService geocodingService;
    private final OptimizationService optimizationService;

    /**
     * Creates instance that contains the necessary routing features
     */
    public DeliveryRouterService() {
        OrsHttpClient client = new OrsHttpClient();
        this.geocodingService = new GeocodingService(client);
        this.optimizationService = new OptimizationService(client);
    }

    /**
     * Plans optimized routes for today's deliveries using the default sample user.
     * Convenience overload for when no user has been set up yet.
     */
    public RouteResult planRoutes(List<Delivery> deliveries) throws IOException {
        return planRoutes(deliveries, SampleDataUtil.getSampleUser());
    }

    /**
     * Plans an optimized route for today's deliveries.
     *
     * @param deliveries the full delivery list from the model
     * @param user       the logged-in user (provides depot address and vehicle profile)
     */
    public RouteResult planRoutes(List<Delivery> deliveries, User user) throws IOException {
        if (deliveries.isEmpty()) {
            throw new IOException("No deliveries to route.");
        }

        // Step 1: geocode depot from user's company address
        Coordinate depot = geocodingService.geocode(user.getDepotAddress());
        List<Coordinate> vehicleCoords = new ArrayList<>();
        vehicleCoords.add(depot);

        // Step 2: geocode all delivery addresses
        List<String> addresses = new ArrayList<>();
        for (Delivery d : deliveries) {
            addresses.add(d.getCompany().getAddress().value);
        }
        List<Coordinate> deliveryCoords = geocodingService.geocodeAll(addresses);

        // Step 3: build time windows + service durations
        int earliest;
        int latest;
        boolean overdue = false;
        List<Delivery> overdueDeliveries = new ArrayList<>();
        List<int[]> timeWindows = new ArrayList<>();
        List<Integer> serviceDurations = new ArrayList<>();
        for (int i = 0; i < deliveries.size(); i++) {
            earliest = (int) (LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .toEpochSecond());
            latest = (int) (deliveries.get(i).getDeadline().getValue().atZone(ZoneId.systemDefault())
                    .toEpochSecond());
            if (latest <= earliest) {
                overdue = true;
                overdueDeliveries.add(deliveries.get(i));
            }
            timeWindows.add(new int[]{earliest, latest});
            serviceDurations.add(DEFAULT_SERVICE_SECS);
        }
        if (overdue) {
            throw new IOException("Overdue Deliveries, please update the deadline of:\n"
                    + overdueDeliveries.stream().map(x -> x.toString() + "\n").toList());
        }

        // Step 4: optimize using user's vehicle profile
        return optimizationService.optimize(
                vehicleCoords, deliveryCoords,
                timeWindows, serviceDurations,
                user.getVehicleProfile());
    }
}
