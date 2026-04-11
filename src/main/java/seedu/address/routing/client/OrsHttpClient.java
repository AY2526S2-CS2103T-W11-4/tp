package seedu.address.routing.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import seedu.address.routing.security.KeyDeriver;

/**
 * Lightweight HTTP client for OpenRouteService.
 * Uses only java.net — no third-party HTTP libraries needed.
 *
 * <p>Key resolution order:
 * <ol>
 *   <li>KeyDeriver — real encoded key, assembled in a secure subprocess</li>
 *   <li>Local key file at {@code <appdir>/ors.key} — for development builds
 *       where the real KeyDeriver.java is not available</li>
 *   <li>IOException — routing unavailable, user is told where to place the key</li>
 * </ol>
 */
public class OrsHttpClient {

    private static final String BASE_URL = "https://api.openrouteservice.org";
    private static final int CONNECT_TIMEOUT_MS = 15_000;
    private static final int READ_TIMEOUT_MS = 30_000;
    private static final String KEY_FILE_NAME = "ors.key";

    /**
     * POST to an ORS endpoint, returns raw JSON response string.
     *
     * @param path     the ORS API path e.g. {@code /optimization}
     * @param jsonBody the JSON request body
     * @return raw JSON response string
     * @throws IOException if the request fails
     */
    public String post(String path, String jsonBody) throws IOException {
        try {
            return KeyDeriver.securePost(path, jsonBody);
        } catch (UnsupportedOperationException e) {
            return postWithLocalKey(path, jsonBody);
        }
    }

    /**
     * GET to an ORS endpoint, returns raw JSON response string.
     *
     * @param path the ORS API path e.g. {@code /geocode/search?text=...}
     * @return raw JSON response string
     * @throws IOException if the request fails
     */
    public String get(String path) throws IOException {
        try {
            return KeyDeriver.secureGet(path);
        } catch (UnsupportedOperationException e) {
            return getWithLocalKey(path);
        }
    }

    /** URL-encodes a string for use in query parameters. */
    public static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // ── Local key fallback ────────────────────────────────────────────────────

    private String loadLocalKey() throws IOException {
        File keyFile = resolveKeyFile();
        if (!keyFile.exists()) {
            throw new IOException(
                    "ORS unavailable: no API key found.\n"
                            + "If you are a developer,\n"
                            + "place your ORS API key in: " + keyFile.getAbsolutePath() + "\n"
                            + "get free key at https://openrouteservice.org/ \n"
                            + "local failsafe will now run");
        }
        return new String(Files.readAllBytes(keyFile.toPath()), StandardCharsets.UTF_8).trim();
    }

    private String postWithLocalKey(String path, String jsonBody) throws IOException {
        String key = loadLocalKey();
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", key);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setDoOutput(true);
        conn.getOutputStream().write(jsonBody.getBytes(StandardCharsets.UTF_8));
        return readResponse(conn);
    }

    private String getWithLocalKey(String path) throws IOException {
        String key = loadLocalKey();
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", key);
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        return readResponse(conn);
    }

    private File resolveKeyFile() {
        String appDir = System.getProperty("mycelia.appdir");
        if (appDir != null) {
            return new File(appDir, KEY_FILE_NAME);
        }
        return new File(System.getProperty("user.dir"), KEY_FILE_NAME);
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
}
