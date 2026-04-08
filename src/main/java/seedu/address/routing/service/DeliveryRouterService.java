package seedu.address.routing.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
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
 *
 * If ORS is unavailable, the service falls back to a local route plan
 * sorted by deadline so the app can still display a usable route order.
 */
public class DeliveryRouterService {

    private static final Logger logger = LogsCenter.getLogger(DeliveryRouterService.class);

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

        // Keep these validations as hard failures.
        validateNoOverdueDeliveries(deliveries);

        // Prepare addresses once so both primary path and fallback can reuse them.
        List<String> addresses = new ArrayList<>();
        for (Delivery d : deliveries) {
            addresses.add(d.getCompany().getAddress().value);
        }

        Coordinate depot = null;
        List<Coordinate> deliveryCoords = null;

        try {
            // Step 1: geocode depot from user's company address
            depot = geocodingService.geocode(user.getDepotAddress());
            List<Coordinate> vehicleCoords = new ArrayList<>();
            vehicleCoords.add(depot);

            // Step 2: geocode all delivery addresses
            deliveryCoords = geocodingService.geocodeAll(addresses);

            // Step 3: build time windows + service durations
            List<int[]> timeWindows = buildTimeWindows(deliveries);
            List<Integer> serviceDurations = buildServiceDurations(deliveries.size());

            // Step 4: optimize using user's vehicle profile
            return optimizationService.optimize(
                    vehicleCoords,
                    deliveryCoords,
                    timeWindows,
                    serviceDurations,
                    user.getVehicleProfile()
            );
        } catch (IOException e) {
            logger.warning("ORS routing failed, falling back to local deadline-based routing. Reason: "
                    + e.getMessage());
            return buildFallbackRouteResult(deliveries, user, depot, deliveryCoords);
        }
    }

    /**
     * Validates that no delivery is already overdue.
     */
    private void validateNoOverdueDeliveries(List<Delivery> deliveries) throws IOException {
        int earliest;
        int latest;
        boolean overdue = false;
        List<Delivery> overdueDeliveries = new ArrayList<>();

        for (Delivery delivery : deliveries) {
            earliest = (int) LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
            latest = (int) delivery.getDeadline().getValue()
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();

            if (latest <= earliest) {
                overdue = true;
                overdueDeliveries.add(delivery);
            }
        }

        if (overdue) {
            throw new IOException("Overdue Deliveries, please update the deadline of:\n"
                    + overdueDeliveries.stream().map(x -> x.toString() + "\n").toList());
        }
    }

    /**
     * Builds ORS-compatible time windows for each delivery.
     */
    private List<int[]> buildTimeWindows(List<Delivery> deliveries) {
        List<int[]> timeWindows = new ArrayList<>();
        int earliest;

        for (Delivery delivery : deliveries) {
            earliest = (int) LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
            int latest = (int) delivery.getDeadline().getValue()
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
            timeWindows.add(new int[]{earliest, latest});
        }

        return timeWindows;
    }

    /**
     * Builds service durations with a fixed service time per stop.
     */
    private List<Integer> buildServiceDurations(int numberOfStops) {
        List<Integer> serviceDurations = new ArrayList<>();
        for (int i = 0; i < numberOfStops; i++) {
            serviceDurations.add(DEFAULT_SERVICE_SECS);
        }
        return serviceDurations;
    }

    /**
     * Builds a local fallback route result when ORS is unavailable.
     *
     * Fallback strategy:
     * - sort deliveries by deadline
     * - reuse already-geocoded coordinates when available
     * - if coordinates are unavailable, use 0.0 as placeholder
     * - produce one vehicle route with no road geometry
     */
    private RouteResult buildFallbackRouteResult(List<Delivery> deliveries,
                                                 User user,
                                                 Coordinate depot,
                                                 List<Coordinate> deliveryCoords) {
        List<Integer> sortedIndexes = new ArrayList<>();
        for (int i = 0; i < deliveries.size(); i++) {
            sortedIndexes.add(i);
        }

        sortedIndexes.sort(
                Comparator.comparing((Integer i) -> deliveries.get(i).getDeadline().getValue())
                        .thenComparing(i -> deliveries.get(i).getCompany().getName().fullName)
                        .thenComparing(i -> deliveries.get(i).getProduct().toString())
        );

        double depotLat = 0.0;
        double depotLon = 0.0;

        if (depot != null) {
            depotLat = depot.lat;
            depotLon = depot.lon;
        } else {
            try {
                Coordinate fallbackDepot = geocodingService.geocode(user.getDepotAddress());
                depotLat = fallbackDepot.lat;
                depotLon = fallbackDepot.lon;
            } catch (IOException ignored) {
                logger.warning("Fallback route could not geocode depot; using placeholder coordinates.");
            }
        }

        List<RouteResult.Stop> stops = new ArrayList<>();
        long estimatedArrival = Instant.now().getEpochSecond();

        for (Integer originalIndex : sortedIndexes) {
            Delivery delivery = deliveries.get(originalIndex);

            double lat = depotLat;
            double lon = depotLon;

            if (deliveryCoords != null && originalIndex < deliveryCoords.size()
                    && deliveryCoords.get(originalIndex) != null) {
                lat = deliveryCoords.get(originalIndex).lat;
                lon = deliveryCoords.get(originalIndex).lon;
            } else {
                try {
                    Coordinate coord = geocodingService.geocode(delivery.getCompany().getAddress().value);
                    lat = coord.lat;
                    lon = coord.lon;
                } catch (IOException ignored) {
                    logger.warning("Fallback route could not geocode delivery address: "
                            + delivery.getCompany().getAddress().value
                            + ". Using depot coordinates instead.");
                }
            }

            stops.add(new RouteResult.Stop(
                    originalIndex,
                    delivery.getCompany().getAddress().value,
                    lat,
                    lon,
                    (int) estimatedArrival,
                    formatTime(estimatedArrival)
            ));

            estimatedArrival += DEFAULT_SERVICE_SECS;
        }

        List<double[]> geometry = new ArrayList<>();
        List<RouteResult.VehicleRoute> routes = new ArrayList<>();
        routes.add(new RouteResult.VehicleRoute(1, stops, geometry, depotLat, depotLon));

        return new RouteResult(routes, new ArrayList<>());
    }

    private String formatTime(long unixTimestamp) {
        LocalTime time = Instant.ofEpochSecond(unixTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }
}