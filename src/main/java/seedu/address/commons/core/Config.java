package seedu.address.commons.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Config values used by the app.
 * All default paths are resolved relative to the {@code mycelia.appdir}
 * system property set by the Bootstrapper, so that config and data files
 * are written to the platform app data directory rather than the JAR
 * launch directory.
 */
public class Config {

    public static final Path DEFAULT_CONFIG_FILE = resolveAppPath("config.json");

    private Level logLevel = Level.INFO;
    private Path userPrefsFilePath = resolveAppPath("preferences.json");

    /**
     * Resolves a filename relative to the mycelia.appdir system property.
     * Falls back to the current directory if the property is not set.
     */
    public static Path resolveAppPath(String filename) {
        String appDir = System.getProperty("mycelia.appdir");
        return (appDir != null)
            ? Paths.get(appDir, filename)
            : Paths.get(filename);
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public Path getUserPrefsFilePath() {
        return userPrefsFilePath;
    }

    public void setUserPrefsFilePath(Path userPrefsFilePath) {
        this.userPrefsFilePath = userPrefsFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Config)) {
            return false;
        }
        Config otherConfig = (Config) other;
        return Objects.equals(logLevel, otherConfig.logLevel)
                && Objects.equals(userPrefsFilePath, otherConfig.userPrefsFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logLevel, userPrefsFilePath);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("logLevel", logLevel)
                .add("userPrefsFilePath", userPrefsFilePath)
                .toString();
    }
}
