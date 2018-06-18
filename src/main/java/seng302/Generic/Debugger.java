package seng302.Generic;


import java.io.*;
import java.sql.Timestamp;

/**
 * The latest in debug technology!
 */
public class Debugger {
    private Debugger() {}

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static boolean consoleEnabled(){
        // Toggle this value to toggle debug messages globally
        return true;
    }

    public static boolean logToFile(){
        // Toggle this value to toggle logging to file
        return true;
    }

    public static void log(Object o){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String callerClassName = new Exception().getStackTrace()[1].getClassName();
        String message = timestamp + ": " + callerClassName + " -> " + o.toString();

        if (Debugger.logToFile()) {
            appendLog(message);
        }

        if (Debugger.consoleEnabled()) {
            System.out.println(message);
        }
    }

    public static void error(Object o){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String callerClassName = new Exception().getStackTrace()[1].getClassName();
        String message = timestamp + ": " + callerClassName + " -> " + o.toString();

        if (Debugger.logToFile()) {
            appendLog(message);
        }

        if (Debugger.consoleEnabled()) {
            System.out.println(ANSI_YELLOW + message + ANSI_RESET);
        }
    }

    private static void appendLog(String message) {
        File log = new File("debuglog.txt");
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
