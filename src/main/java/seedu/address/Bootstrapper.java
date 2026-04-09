package seedu.address;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Handles first-run setup for the application.
 * Resolves the appropriate user data directory for the current platform:
 * <ul>
 *   <li>Windows: %APPDATA%\MyCelia</li>
 *   <li>macOS:   ~/Library/Application Support/MyCelia</li>
 *   <li>Linux:   ~/.local/share/MyCelia</li>
 * </ul>
 * Falls back to a MyCelia/ subdirectory next to the JAR if the platform
 * directory cannot be created or written to.
 *
 * <p>Sets the {@code mycelia.appdir} system property so that Config and
 * LogsCenter resolve all paths relative to the app data directory rather
 * than the JAR launch directory.
 *
 * <p>Uses plain string writes only — no Jackson or SampleDataUtil dependency
 * so that no annotation loading occurs before JavaFX is initialised.
 */
class Bootstrapper {

    private static final Logger logger = LogsCenter.getLogger(Bootstrapper.class);
    private static final String APP_NAME = "MyCelia";

    /**
     * Runs the bootstrap sequence.
     * Determines the app data directory, sets the {@code mycelia.appdir}
     * system property, and writes default config files if missing.
     */
    static void run() {
        logger.info("=== BOOTSTRAP START ===");

        File appDir = resolveAppDir();
        File dataDir = new File(appDir, "data");

        logger.info("App directory: " + appDir.getAbsolutePath());

        // Set system property so Config.java and LogsCenter resolve absolute paths
        System.setProperty("mycelia.appdir", appDir.getAbsolutePath());

        logFileState(new File(appDir, "config.json"));
        logFileState(new File(appDir, "preferences.json"));
        logFileState(dataDir);

        try {
            if (!appDir.exists() && !appDir.mkdirs()) {
                throw new IOException("Could not create app directory: " + appDir);
            }
            if (!dataDir.exists() && !dataDir.mkdirs()) {
                throw new IOException("Could not create data directory: " + dataDir);
            }
            createIfMissing(new File(appDir, "preferences.json"), buildDefaultPrefs(dataDir));
        } catch (IOException e) {
            logger.warning("Bootstrap warning: " + e.getMessage());
        }

        logger.info("=== BOOTSTRAP END ===");
        logFileState(new File(appDir, "config.json"));
        logFileState(new File(appDir, "preferences.json"));
        logFileState(dataDir);
    }

    /**
     * Builds the default preferences JSON with absolute paths to data files.
     */
    private static String buildDefaultPrefs(File dataDir) {
        String addr = new File(dataDir, "addressbook.json").getAbsolutePath().replace("\\", "/");
        String delivery = new File(dataDir, "deliverybook.json").getAbsolutePath().replace("\\", "/");
        String user = new File(dataDir, "user.json").getAbsolutePath().replace("\\", "/");
        return "{\n"
            + "  \"guiSettings\" : {\n"
            + "    \"windowWidth\" : 1200.0,\n"
            + "    \"windowHeight\" : 700.0,\n"
            + "    \"windowCoordinates\" : {\n"
            + "      \"x\" : 100,\n"
            + "      \"y\" : 100\n"
            + "    }\n"
            + "  },\n"
            + "  \"addressBookFilePath\" : \"" + addr + "\",\n"
            + "  \"deliveryBookFilePath\" : \"" + delivery + "\",\n"
            + "  \"userFilePath\" : \"" + user + "\"\n"
            + "}";
    }

    /**
     * Resolves the platform-appropriate application data directory.
     * Falls back to a MyCelia/ folder next to the JAR if unavailable.
     */
    private static File resolveAppDir() {
        String os = System.getProperty("os.name").toLowerCase();
        File candidate;

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            candidate = (appData != null) ? new File(appData, APP_NAME) : null;
        } else if (os.contains("mac")) {
            candidate = new File(System.getProperty("user.home"),
                "Library/Application Support/" + APP_NAME);
        } else {
            String xdgData = System.getenv("XDG_DATA_HOME");
            candidate = (xdgData != null)
                ? new File(xdgData, APP_NAME)
                : new File(System.getProperty("user.home"), ".local/share/" + APP_NAME);
        }

        if (candidate != null) {
            try {
                if (!candidate.exists()) {
                    candidate.mkdirs();
                }
                File test = new File(candidate, ".write_test");
                test.createNewFile();
                test.delete();
                logger.info("Using platform data dir: " + candidate.getAbsolutePath());
                return candidate;
            } catch (Exception e) {
                logger.warning("Platform data dir unavailable (" + e.getMessage()
                    + "), falling back to local directory");
            }
        }

        File fallback = new File(System.getProperty("user.dir"), APP_NAME);
        logger.info("Using fallback data dir: " + fallback.getAbsolutePath());
        return fallback;
    }

    private static void createIfMissing(File file, String content) throws IOException {
        if (!file.exists() || file.length() == 0) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(content);
            }
            logger.info("Bootstrap: created " + file.getAbsolutePath());
        }
    }

    private static void logFileState(File file) {
        if (!file.exists()) {
            logger.info("  [MISSING] " + file.getAbsolutePath());
            return;
        }
        if (file.length() == 0) {
            logger.info("  [EMPTY]   " + file.getAbsolutePath());
            return;
        }
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            String preview = content.length() > 100 ? content.substring(0, 100) + "..." : content;
            logger.info("  [OK " + file.length() + "b] " + file.getName() + " => " + preview);
        } catch (IOException e) {
            logger.info("  [UNREADABLE] " + file.getAbsolutePath() + ": " + e.getMessage());
        }
    }
}
