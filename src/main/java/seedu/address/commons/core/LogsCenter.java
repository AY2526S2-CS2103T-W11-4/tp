package seedu.address.commons.core;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Configures and manages loggers and handlers, including their logging level.
 * Named {@link Logger}s can be obtained from this class.<br>
 * These loggers have been configured to output messages to the console and a {@code .log} file by default,
 * at the {@code INFO} level. A new {@code .log} file with a new numbering will be created after the log
 * file reaches 5MB big, up to a maximum of 5 files.<br>
 *
 * <p>Call {@link #relocateLogFile()} after the {@code mycelia.appdir} system property has been set
 * by the Bootstrapper to move log output into the platform app data directory.
 */
public class LogsCenter {
    private static final int MAX_FILE_COUNT = 5;
    private static final int MAX_FILE_SIZE_IN_BYTES = (int) (Math.pow(2, 20) * 5); // 5MB
    private static final String LOG_FILENAME = "addressbook.log";
    private static final Logger logger;
    private static Logger baseLogger;
    private static Level currentLogLevel = Level.INFO;

    // Static initializer — appdir not yet set, so log file goes to working directory temporarily.
    // relocateLogFile() must be called after Bootstrapper sets mycelia.appdir.
    static {
        setBaseLogger(LOG_FILENAME);
        logger = LogsCenter.getLogger(LogsCenter.class);
    }

    /**
     * Replaces the file handler on the base logger with one pointing to the
     * platform app data directory. Should be called immediately after
     * {@code Bootstrapper.run()} sets the {@code mycelia.appdir} system property.
     *
     * <p>Also deletes any stray log file written to the working directory
     * before this method was called.
     */
    public static void relocateLogFile() {
        String appDir = System.getProperty("mycelia.appdir");
        if (appDir == null) {
            return;
        }

        String targetPath = appDir + File.separator + LOG_FILENAME;

        // Remove existing file handlers (pointing to working directory)
        Arrays.stream(baseLogger.getHandlers())
              .filter(h -> h instanceof FileHandler)
              .forEach(h -> {
                  h.close();
                  baseLogger.removeHandler(h);
              });

        // Add new file handler pointing to appdir
        try {
            FileHandler fileHandler = new FileHandler(targetPath, MAX_FILE_SIZE_IN_BYTES, MAX_FILE_COUNT, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            baseLogger.addHandler(fileHandler);
        } catch (IOException e) {
            // Console logging still works — not fatal
            System.err.println("LogsCenter: could not create log file at " + targetPath + ": " + e.getMessage());
        }

        // Brief pause to let Windows release the .lck file before deletion
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
            //do nothing
        }

        // Clean up stray log files written to working directory before relocation
        deleteStrayLogFiles(System.getProperty("user.dir"));
    }

    /**
     * Deletes addressbook.log and addressbook.log.lck from the given directory
     * if they exist, to avoid leaving clutter in the JAR launch directory.
     */
    private static void deleteStrayLogFiles(String dir) {
        if (dir == null) {
            return;
        }
        // FileHandler rotation produces addressbook.log.0, .1, .2 etc
        // Also try plain name in case rotation hasn't kicked in
        for (int i = 0; i < MAX_FILE_COUNT; i++) {
            new File(dir, LOG_FILENAME + "." + i).delete();
            new File(dir, LOG_FILENAME + "." + i + ".lck").delete();
        }
        new File(dir, LOG_FILENAME).delete();
        new File(dir, LOG_FILENAME + ".lck").delete();
    }

    /**
     * Initializes loggers with the log level specified in the {@code config} object.
     */
    public static void init(Config config) {
        currentLogLevel = config.getLogLevel();
        logger.info("Log level will be set as: " + currentLogLevel);
        baseLogger.setLevel(currentLogLevel);
    }

    /**
     * Creates a logger with the given name as a descendant of the base logger.
     */
    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(baseLogger.getName() + "." + name);
        removeHandlers(logger);
        logger.setUseParentHandlers(true);
        return logger;
    }

    /**
     * Creates a Logger for the given class.
     */
    public static <T> Logger getLogger(Class<T> clazz) {
        requireNonNull(clazz);
        return getLogger(clazz.getSimpleName());
    }

    private static void removeHandlers(Logger logger) {
        Arrays.stream(logger.getHandlers()).forEach(logger::removeHandler);
    }

    private static void setBaseLogger(String logFilePath) {
        baseLogger = Logger.getLogger("ab3");
        baseLogger.setUseParentHandlers(false);
        removeHandlers(baseLogger);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        baseLogger.addHandler(consoleHandler);

        try {
            FileHandler fileHandler = new FileHandler(logFilePath, MAX_FILE_SIZE_IN_BYTES, MAX_FILE_COUNT, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            baseLogger.addHandler(fileHandler);
        } catch (IOException e) {
            // Console only until relocateLogFile() is called
        }
    }
}
