package seedu.address.routing.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import seedu.address.routing.AppConfig;

/**
 * Lightweight HTTP client for OpenRouteService.
 * Uses only java.net — no third-party HTTP libraries needed.
 */
public class OrsHttpClient {

    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 30_000;

    /** POST to an ORS endpoint, returns raw JSON response string. */
    public String post(String path, String jsonBody) throws IOException {
        URL url = new URL(AppConfig.orsBaseUrl() + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        addAuthHeader(conn);
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        return readResponse(conn);
    }

    /** GET to an ORS endpoint, returns raw JSON response string. */
    public String get(String path) throws IOException {
        URL url = new URL(AppConfig.orsBaseUrl() + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        addAuthHeader(conn);
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);

        return readResponse(conn);
    }

    private void addAuthHeader(HttpURLConnection conn) {
        String key = AppConfig.orsApiKey();
        if (key != null && !key.isEmpty()) {
            conn.setRequestProperty("Authorization", key);
        }
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        InputStream is = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (is == null) {
            throw new IOException("ORS returned " + status + " with no body");
        }
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        if (status >= 400) {
            throw new IOException("ORS error " + status + ": " + body);
        }
        return body;
    }

    /** URL-encodes a string for use in query parameters. */
    public static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
