package seedu.address.routing.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import seedu.address.routing.client.OrsHttpClient;
import seedu.address.routing.model.Coordinate;

/**
 * Converts human-readable addresses into lat/lon coordinates
 * using the ORS Geocoding (Pelias) API.
 */
public class GeocodingService {

    private final OrsHttpClient client;

    public GeocodingService(OrsHttpClient client) {
        this.client = client;
    }

    /** Geocode a single address string into a Coordinate. */
    public Coordinate geocode(String address) throws IOException {
        String path = "/geocode/search?text=" + OrsHttpClient.encode(address) + "&size=1";
        String response = client.get(path);

        JSONObject json = new JSONObject(response);
        JSONArray features = json.getJSONArray("features");

        if (features.isEmpty()) {
            throw new IOException("No geocoding result for: " + address);
        }

        JSONArray coords = features.getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONArray("coordinates");

        double lon = coords.getDouble(0);
        double lat = coords.getDouble(1);
        return new Coordinate(lon, lat, address);
    }

    /** Geocode a list of addresses, preserving order. */
    public List<Coordinate> geocodeAll(List<String> addresses) throws IOException {
        List<Coordinate> results = new ArrayList<>();
        for (String address : addresses) {
            results.add(geocode(address));
        }
        return results;
    }
}
