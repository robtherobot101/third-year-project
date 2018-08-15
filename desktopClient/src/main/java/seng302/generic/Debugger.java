package seng302.generic;


import java.io.*;
import java.sql.Timestamp;

/**
 * The latest in debug technology!
 */
public class Debugger {
    private Debugger() {}

    private static final boolean CONSOLE_ENABLED = true;
    private static final boolean LOG_TO_FILE = true;
    private static final String FILE_NAME = "debuglog.txt";

    /**
     * Prints the object as a console message
     * @param o object to be printed
     */
    public static void log(Object o) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String callerClassName = new Exception().getStackTrace()[1].getClassName();
        String message = timestamp + ": " + callerClassName + " -> " + o.toString();

        if (Debugger.LOG_TO_FILE) {
            appendLog(message);
        }

        if (Debugger.CONSOLE_ENABLED) {
            System.out.println(message);
        }
    }

    /**
     * Prints the object as a yellow console message
     * @param o object to be printed
     */
    public static void error(Object o) {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_YELLOW = "\u001B[33m";

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String callerClassName = new Exception().getStackTrace()[1].getClassName();
        String message = timestamp + ": " + callerClassName + " -> " + o.toString();

        if (Debugger.LOG_TO_FILE) {
            appendLog(message);
        }

        if (Debugger.CONSOLE_ENABLED) {
            System.out.println(ANSI_YELLOW + message + ANSI_RESET);
        }
    }

    /**
     * Appends the debug message to a logfile
     * @param message to be appended to log
     */
    private static void appendLog(String message) {
        File log = new File(Debugger.FILE_NAME);
        try {
            log.createNewFile(); // if file already exists will do nothing
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(log, true))) {
            writer.append('\n');
            writer.append(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
