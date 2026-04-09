package seedu.address;

import java.util.logging.Logger;

import javafx.application.Application;
import seedu.address.commons.core.LogsCenter;

/**
 * The main entry point to the application.
 *
 * This is a workaround for the following error when MainApp is made the
 * entry point of the application:
 *
 *     Error: JavaFX runtime components are missing, and are required to run this application
 *
 * The reason is that MainApp extends Application. In that case, the
 * LauncherHelper will check for the javafx.graphics module to be present
 * as a named module. We don't use JavaFX via the module system so it can't
 * find the javafx.graphics module, and so the launch is aborted.
 *
 * By having a separate main class (Main) that doesn't extend Application
 * to be the entry point of the application, we avoid this issue.
 */
public class Main {
    private static Logger logger = LogsCenter.getLogger(Main.class);

    public static void main(String[] args) {
        // 1. Determine platform app data directory and set mycelia.appdir system property
        Bootstrapper.run();

        // 2. Relocate log file from working directory to app data directory
        LogsCenter.relocateLogFile();

        logger.warning("The warning about Unsupported JavaFX configuration below (if any) can be ignored.");
        Application.launch(MainApp.class, args);
    }
}
