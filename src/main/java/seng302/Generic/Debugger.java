package seng302.Generic;


import java.io.*;
import java.sql.Timestamp;

/**
 * The latest in debug technology!
 */
public class Debugger {
    private Debugger() {}

    private static final boolean consoleEnabled = true;
    private static final boolean logToFile = true;
    private static final String fileName = "debuglog.txt";

    /**
     * Prints the object as a console message
     * @param o object to be printed
     */
    public static void log(Object o) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String callerClassName = new Exception().getStackTrace()[1].getClassName();
        String message = timestamp + ": " + callerClassName + " -> " + o.toString();

        if (Debugger.logToFile) {
            appendLog(message);
        }

        if (Debugger.consoleEnabled) {
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

        if (Debugger.logToFile) {
            appendLog(message);
        }

        if (Debugger.consoleEnabled) {
            System.out.println(ANSI_YELLOW + message + ANSI_RESET);
        }
    }

    /**
     * Appends the debug message to a logfile
     * @param message to be appended to log
     */
    private static void appendLog(String message) {
        File log = new File(Debugger.fileName);
        try {
            boolean fileCreated = log.createNewFile(); // if file already exists will do nothing
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
