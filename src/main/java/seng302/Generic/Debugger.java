package seng302.Generic;


import java.sql.Timestamp;

/**
 * The latest in debug technology!
 */
public class Debugger {
    private Debugger() {}

    public static boolean consoleEnabled(){
        // Toggle this value to toggle debug messages globally
        return true;
    }

    public static void log(Object o){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String callerClassName = new Exception().getStackTrace()[1].getClassName();
        String message = timestamp + ": " + callerClassName + " -> " + o.toString();

        //TODO log to file

        if (Debugger.consoleEnabled()) {
            System.out.println(message);
        }
    }
}
