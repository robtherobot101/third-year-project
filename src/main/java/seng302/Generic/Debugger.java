package seng302.Generic;


import java.sql.Timestamp;

/**
 * The latest in debug technology!
 */
public class Debugger {
    private Debugger() {}

    public static boolean isEnabled(){
        // Toggle this value to toggle debug messages globally
        return true;
    }

    public static void log(Object o){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (Debugger.isEnabled()) {
            System.out.println(timestamp + ": " + o.toString());
        }
    }
}
