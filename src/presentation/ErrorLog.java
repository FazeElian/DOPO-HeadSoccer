package presentation;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Error log for the programmers (presentation package: the domain only throws, the interface handles).
 * Each error is appended to {@link #LOG_FILE} with its date,
 * message and stack trace, while the user only sees the message.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public final class ErrorLog {

    /** File where the errors are written. */
    public static final String LOG_FILE = "DOPOSoccer.log";

    /** Logger that writes in the log file. */
    private static final Logger LOGGER = Logger.getLogger("DOPOSoccer");
    /** Whether the file of the log was already opened. */
    private static boolean ready = false;

    /** Not used: every method is static. */
    private ErrorLog() {
    }

    /**
     * Writes an error in the log. It never throws: if the file cannot be written,
     * the error is lost but the game goes on.
     *
     * @param e the error to record
     */
    public static synchronized void log(Exception e) {
        try {
            if (!ready) {
                FileHandler handler = new FileHandler(LOG_FILE, true);
                handler.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(handler);
                LOGGER.setUseParentHandlers(false);
                ready = true;
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException | SecurityException ignored) {
            // The log itself failed; there is nowhere else to report it.
        }
    }
}
