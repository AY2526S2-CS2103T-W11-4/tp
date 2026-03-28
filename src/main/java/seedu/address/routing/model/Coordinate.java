package seedu.address.routing.model;

/** A geocoded latitude/longitude pair with its original address string. */
public class Coordinate {
    public final double lon;
    public final double lat;
    public final String originalAddress;

    public Coordinate(double lon, double lat, String originalAddress) {
        this.lon = lon;
        this.lat = lat;
        this.originalAddress = originalAddress;
    }
}
