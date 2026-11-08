package domain;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Registry of the unexpected errors of the game. Each error is added at the end of the
 * file {@value #NAME}.log (next to the project), with the date, the class and the stack trace,
 * so the user only sees a short message and the developers can read what happened.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public final class Log {

    /** Name of the logger and of the file. */
    public static final String NAME = "DOPOSoccer";

    private Log() {
    }

    /**
     * Writes an unexpected error at the end of the log file. If the file can't be written,
     * the error goes to the console, so it is never lost.
     *
     * @param e the error
     */
    public static void record(Throwable e) {
        Logger logger = Logger.getLogger(NAME);
        logger.setUseParentHandlers(false); // only to the file, not to the console
        FileHandler file = null;
        try {
            file = new FileHandler(NAME + ".log", true); // true: keep the old errors
            file.setFormatter(new SimpleFormatter());
            logger.addHandler(file);
            logger.log(Level.SEVERE, e.toString(), e);
        } catch (IOException | SecurityException ioe) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                logger.removeHandler(file);
                file.close();
            }
        }
    }
}
