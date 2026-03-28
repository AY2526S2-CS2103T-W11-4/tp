package seedu.address.routing.model;

import java.util.List;

/**
 * Holds the result of a route optimization request.
 * Passed from the routing service back to the UI (MainWindow / WebView).
 */
public class RouteResult {

    public final List<VehicleRoute> routes;
    public final List<Integer> unassigned;

    public RouteResult(List<VehicleRoute> routes, List<Integer> unassigned) {
        this.routes = routes;
        this.unassigned = unassigned;
    }

    /** One vehicle's ordered list of stops with road-following geometry. */
    public static class VehicleRoute {
        public final int vehicleId;
        public final List<Stop> stops;
        /** GeoJSON coordinates [[lon,lat],...] decoded from ORS geometry. May be null. */
        public final List<double[]> geometry;
        /** Depot start coordinates [lat, lon]. */
        public final double depotLat;
        public final double depotLon;

        public VehicleRoute(int vehicleId, List<Stop> stops, List<double[]> geometry,
                            double depotLat, double depotLon) {
            this.vehicleId = vehicleId;
            this.stops = stops;
            this.geometry = geometry;
            this.depotLat = depotLat;
            this.depotLon = depotLon;
        }
    }

    /** One delivery stop within a vehicle's route. */
    public static class Stop {
        public final int deliveryIndex;
        public final String address;
        public final double lat;
        public final double lon;
        public final int arrivalTime;
        public final String arrivalTimeFormatted;

        public Stop(int deliveryIndex, String address, double lat, double lon,
                    int arrivalTime, String arrivalTimeFormatted) {
            this.deliveryIndex = deliveryIndex;
            this.address = address;
            this.lat = lat;
            this.lon = lon;
            this.arrivalTime = arrivalTime;
            this.arrivalTimeFormatted = arrivalTimeFormatted;
        }
    }
}
