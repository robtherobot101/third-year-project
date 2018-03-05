package seng302.Files;

import seng302.Core.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

public class History {

    private static String command;
    private static String parameterOne = null;
    private static String parameterTwo = null;
    private static String parameterThree = null;
    private static String description = null;

    /**
     * Initialises the printstream writing to file.
     *
     * Currently this is setup to go to the target folder. This will probably change when we have a more robust export system.
     * @return the printstream if no IO error otherwise null.
     */
    public static PrintStream init() {
        try {
            String directoryPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            File actionHistory = new File(directoryPath + "\\actionHistory.txt");
            FileOutputStream fout = new FileOutputStream(actionHistory);
            PrintStream out = new PrintStream(fout);
            return out;
        } catch(IOException e) {
            System.out.println("I/O Error writing command history to file!");
            e.printStackTrace();
            return null;
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            return null;
        }


    }

    /**
     * Prints the given string to the file via the given printstream.
     *
     * @param out the printstream.
     * @param text the string being written to the file.
     */
    public static void printToFile(PrintStream out, String text) {
       out.println(text);
    }

    /**
     * Adds the current time to the command string being added to file, and formats the string, breaking up the split.
     * Formatted in such a way that it is machine readable.
     * @param nextCommand the text from the command line.
     * @return the modified string.
     */
    public static String prepareFileString(String[] nextCommand) {
        String text = LocalDateTime.now().toString();
        command = nextCommand[0];
        switch (command) {
            case "create":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                description = "[Created a donor with name: " + parameterOne + ", and date of birth:" + parameterTwo + "]";
                break;
            case "describe":
                parameterOne = nextCommand[1];
                description = "[Listed the attributes of donor " + nextCommand[1] + ".]";
                break;
            case "list":
                description = "[Listed all donors.]";
                break;
            case "set":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                parameterThree = nextCommand[3];
                description = "[Attempted to change the attribute " + parameterTwo + " of donor " + parameterOne + ".]";
                break;
            case "delete":
                parameterOne = nextCommand[1];
                description = "[Deleted donor " + parameterOne + " from the list.]";
                break;
            case "add":
               parameterOne = nextCommand[1];
               parameterTwo = nextCommand[2];
               description = "[Added organ of type " + parameterTwo + " to donor " + parameterOne + ".]";
               break;
            case "remove":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                description = "[Removed organ of type " + parameterTwo + " from donor " + parameterOne + ".]";
                break;
            case "organ_list":
                description = "[Listed all organs available from all donors.]";
                break;
            case "donor_organs":
                parameterOne = nextCommand[1];
                description = "[Listed organs available from donor " + parameterOne + ".]";
                break;
            case "save":
                parameterOne = nextCommand[1];
                description = "[Saved all donors to file " + parameterOne + ".]";
                break;
            case "help":
                if(nextCommand.length == 1) {
                    description = "[Queried available commands.]";
                } else {
                    parameterOne = nextCommand[1];
                    description = "[Queried information about the " + parameterOne + " command.]";
                }
                break;
            case "quit":
                description = "[Quit the program.]";
                break;
       }

       //join the elements
        text = text.join(" ", text, command, parameterOne, parameterTwo, parameterThree, description);

        //reset for next call
        command = null;
        parameterOne = null;
        parameterTwo = null;
        parameterThree = null;
        description = null;

        return text;
    }
}
