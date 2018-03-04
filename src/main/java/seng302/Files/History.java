package seng302.Files;

import seng302.Core.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;

public class History {

    /**
     * Initialises the printstream writing to file.
     * @return the printstream if no IO error otherwise null.
     */
    public static PrintStream init(){
        try {
            File actionHistory = new File(".\\src\\files\\actionHistory.txt");
            FileOutputStream fout = new FileOutputStream(actionHistory);
            PrintStream out = new PrintStream(fout);
            return out;
        } catch(IOException e) {
            System.out.println("I/O Error writing command history to file!");
            e.printStackTrace();
            return null;
        }


    }

    /**
     * Prints the given string to the file via the given printstream.
     * @param out the printstream.
     * @param text the string being written to the file.
     */
    public static void printToFile(PrintStream out, String text) {
       out.println(text);
    }

    /**
     * Adds the current time to the command string being added to file, and formats the string, breaking up the split.
     * @param nextCommand the text from the command line.
     * @return the modified string.
     */
    public static String prepareFileString(String[] nextCommand) {
       String text = LocalDateTime.now().toString() + "|";
        for (String command : nextCommand){
            text += (" " + command);
        }
        return text;

    }
}
