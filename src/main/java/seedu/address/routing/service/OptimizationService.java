package seedu.address.routing.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import seedu.address.routing.client.OrsHttpClient;
import seedu.address.routing.model.Coordinate;
import seedu.address.routing.model.RouteResult;

/**
 * Calls the ORS Optimization API (VROOM) to solve the VRPTW.
 * Requests road-following geometry via the options.g flag,
 * then decodes the returned encoded polyline.
 */
public class OptimizationService {

    private final OrsHttpClient client;

    public OptimizationService(OrsHttpClient client) {
        this.client = client;
    }

    /**
     * Builds a request to the API to obtain the optimized route for the given locations
     *
     * @param vehicleCoords
     * @param deliveryCoords
     * @param timeWindows
     * @param serviceDurations
     * @param vehicleProfile
     * @return
     * @throws IOException
     */

    public RouteResult optimize(
            List<Coordinate> vehicleCoords,
            List<Coordinate> deliveryCoords,
            List<int[]> timeWindows,
            List<Integer> serviceDurations,
            String vehicleProfile) throws IOException {

        String body = buildRequest(vehicleCoords, deliveryCoords,
                timeWindows, serviceDurations, vehicleProfile);
        String response = client.post("/optimization", body);
        return parseResponse(response, vehicleCoords, deliveryCoords);
    }

    // ── Request builder ───────────────────────────────────────────────────────

    private String buildRequest(
            List<Coordinate> vehicleCoords,
            List<Coordinate> deliveryCoords,
            List<int[]> timeWindows,
            List<Integer> serviceDurations,
            String vehicleProfile) {

        long midnightToday = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();

        JSONObject root = new JSONObject();

        // Request road-following geometry — must be inside "options" as "g"
        JSONObject options = new JSONObject();
        options.put("g", true);
        root.put("options", options);

        // ── vehicles ──
        JSONArray vehicles = new JSONArray();
        for (int i = 0; i < vehicleCoords.size(); i++) {
            Coordinate c = vehicleCoords.get(i);
            JSONObject v = new JSONObject();
            v.put("id", i + 1);
            v.put("profile", vehicleProfile);
            v.put("start", new JSONArray().put(c.lon).put(c.lat));
            v.put("end", new JSONArray().put(c.lon).put(c.lat));
            v.put("time_window", new JSONArray()
                    .put(midnightToday)
                    .put(midnightToday + 86399));
            vehicles.put(v);
        }
        root.put("vehicles", vehicles);

        // ── jobs ──
        JSONArray jobs = new JSONArray();
        for (int i = 0; i < deliveryCoords.size(); i++) {
            Coordinate c = deliveryCoords.get(i);
            JSONObject job = new JSONObject();
            job.put("id", i + 1);
            job.put("location", new JSONArray().put(c.lon).put(c.lat));
            int[] tw = timeWindows.get(i);
            job.put("time_windows", new JSONArray().put(
                    new JSONArray()
                            .put(tw[0])
                            .put(tw[1])));
            job.put("service", serviceDurations.get(i));
            jobs.put(job);
        }
        root.put("jobs", jobs);

        return root.toString();
    }

    // ── Response parser ───────────────────────────────────────────────────────

    private RouteResult parseResponse(String response, List<Coordinate> vehicleCoords,
            List<Coordinate> deliveryCoords) {
        JSONObject json = new JSONObject(response);

        List<RouteResult.VehicleRoute> routes = new ArrayList<>();
        List<Integer> unassigned = new ArrayList<>();

        if (json.has("unassigned")) {
            JSONArray ua = json.getJSONArray("unassigned");
            for (int i = 0; i < ua.length(); i++) {
                unassigned.add(ua.getJSONObject(i).getInt("id") - 1);
            }
        }

        if (json.has("routes")) {
            JSONArray jsonRoutes = json.getJSONArray("routes");
            for (int r = 0; r < jsonRoutes.length(); r++) {
                JSONObject route = jsonRoutes.getJSONObject(r);
                int vehicleId = route.getInt("vehicle");
                List<RouteResult.Stop> stops = new ArrayList<>();

                JSONArray steps = route.getJSONArray("steps");
                for (int s = 0; s < steps.length(); s++) {
                    JSONObject step = steps.getJSONObject(s);
                    if (!"job".equals(step.getString("type"))) {
                        continue;
                    }
                    int jobId = step.getInt("id");
                    int deliveryIdx = jobId - 1;
                    long arrival = step.getLong("arrival");
                    Coordinate coord = deliveryCoords.get(deliveryIdx);

                    stops.add(new RouteResult.Stop(
                            deliveryIdx,
                            coord.originalAddress,
                            coord.lat,
                            coord.lon,
                            (int) arrival,
                            formatTime(arrival)
                    ));
                }

                // Decode road-following geometry from encoded polyline
                List<double[]> geometry = new ArrayList<>();
                if (route.has("geometry")) {
                    String encoded = route.getString("geometry");
                    geometry = decodePolyline(encoded);
                }

                Coordinate depot = vehicleCoords.get(vehicleId - 1);
                routes.add(new RouteResult.VehicleRoute(vehicleId, stops, geometry,
                        depot.lat, depot.lon));
            }
        }

        return new RouteResult(routes, unassigned);
    }

    /**
     * Decodes a standard Google-encoded polyline string into [lon, lat] pairs.
     * ORS uses standard Google polyline encoding (precision 1e5).
     *
     * @param encodedGeometry
     * @return
     */
    private List<double[]> decodePolyline(String encodedGeometry) {
        List<double[]> points = new ArrayList<>();
        int len = encodedGeometry.length();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            // Decode latitude
            int result = 0;
            int shift = 0;
            int b;
            do {
                b = encodedGeometry.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            // Decode longitude
            result = 0;
            shift = 0;
            do {
                b = encodedGeometry.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            // ORS returns lat/lng — store as [lon, lat] for Leaflet consistency
            points.add(new double[]{lng / 1e5, lat / 1e5});
        }

        return points;
    }

    private String formatTime(long unixTimestamp) {
        LocalTime time = Instant.ofEpochSecond(unixTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }
}
