package seedu.address.routing.security;

/**
 * Stub KeyDeriver for development builds where the real encrypted
 * KeyDeriver.java is not available.
 *
 * The real KeyDeriver.java is git-crypt encrypted and only available
 * to maintainers with the decryption key. For routing to work in
 * development, place your ORS API key in:
 *
 *   Windows: %APPDATA%\MyCelia\ors.key
 *   macOS:   ~/Library/Application Support/MyCelia/ors.key
 *   Linux:   ~/.local/share/MyCelia/ors.key
 *
 * The file should contain only the raw API key, no extra whitespace.
 *
 * To obtain an ORS key, register at https://openrouteservice.org/
 */
public class KeyDeriverStub {

    public static String securePost(String path, String body) throws java.io.IOException {
        throw new UnsupportedOperationException("stub");
    }

    public static String secureGet(String path) throws java.io.IOException {
        throw new UnsupportedOperationException("stub");
    }
}
