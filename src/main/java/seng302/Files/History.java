package seng302.Files;

import seng302.Core.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;

public class History {

    private static String command;
    private static String parameterOne = null;
    private static String parameterTwo = null;
    private static String parameterThree = null;
    private static String description = null;

    /**
     * Initialises the printstream writing to file.
     * <p>
     * Currently this is setup to go to the target folder. This will probably change when we have a more robust export system.
     *
     * @return the printstream if no IO error otherwise null.
     */
    public static PrintStream init() {
        try {
            File actionHistory = new File(Main.getJarPath() + File.separatorChar + "actionHistory.txt");
            FileOutputStream fout = new FileOutputStream(actionHistory, true);
            PrintStream out = new PrintStream(fout);
            out.println(Donor.dateTimeFormat.format(LocalDateTime.now()) + " ==== NEW SESSION ====");
            return out;
        } catch (IOException e) {
            System.out.println("I/O Error writing command history to file!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Prints the given string to the file via the given printstream.
     *
     * @param out  the printstream.
     * @param text the string being written to the file.
     */
    public static void printToFile(PrintStream out, String text) {
        out.println(text);
    }

    /**
     * Adds the current time to the command string being added to file, and formats the string, breaking up the split.
     * Formatted in such a way that it is machine readable.
     *
     * @param nextCommand the text from the command line.
     * @return the modified string.
     */
    public static String prepareFileStringCLI(String[] nextCommand) {
        String text = Donor.dateTimeFormat.format(LocalDateTime.now()) + " CLI";
        command = nextCommand[0];
        switch (command.toLowerCase()) {
            case "add":
                if (nextCommand[1].contains("\"")) {
                    parameterOne = String.join(" ", nextCommand).split("\"")[1];
                    parameterTwo = nextCommand[nextCommand.length - 1];
                } else {
                    parameterOne = nextCommand[1];
                    parameterTwo = nextCommand[2];
                }
                description = "[Created a donor with name: " + parameterOne + ", and date of birth:" + parameterTwo + "]";
                break;
            case "addorgan":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                description = "[Added organ of type " + parameterTwo + " to donor " + parameterOne + ".]";
                break;
            case "delete":
                parameterOne = nextCommand[1];
                description = "[Deleted donor " + parameterOne + " from the list.]";
                break;
            case "deleteorgan":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                description = "[Removed organ of type " + parameterTwo + " from donor " + parameterOne + ".]";
                break;
            case "set":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                parameterThree = nextCommand[3];
                description = "[Attempted to change the attribute " + parameterTwo + " of donor " + parameterOne + ".]";
                break;
            case "describe":
                parameterOne = nextCommand[1];
                description = "[Listed the attributes of donor " + nextCommand[1] + ".]";
                break;
            case "describeorgans":
                parameterOne = nextCommand[1];
                description = "[Listed organs available from donor " + parameterOne + ".]";
                break;
            case "list":
                description = "[Listed all donors.]";
                break;
            case "listorgans":
                description = "[Listed all organs available from all donors.]";
                break;
            case "import":
                if (nextCommand.length >= 2) {
                    if (nextCommand[1].equals("-r")) {
                        parameterOne = nextCommand[1];
                        if (nextCommand[2].contains("\"")) {
                            parameterTwo = String.join(" ", nextCommand).split("\"")[1];
                        } else {
                            parameterTwo = nextCommand[2];
                        }
                        description = "[Imported donors from relative path with filename " + parameterTwo + ".]";
                    } else {
                        if (nextCommand[1].contains("\"")) {
                            parameterOne = String.join(" ", nextCommand).split("\"")[1];
                        } else {
                            parameterOne = nextCommand[1];
                        }
                        description = "[Imported donors from path " + parameterOne + ".]";
                    }
                }
                break;
            case "save":
                if (nextCommand.length >= 2) {
                    if (nextCommand[1].equals("-r")) {
                        parameterOne = nextCommand[1];
                        if (nextCommand[2].contains("\"")) {
                            parameterTwo = String.join(" ", nextCommand).split("\"")[1];
                        } else {
                            parameterTwo = nextCommand[2];
                        }
                        description = "[Saved donors to relative path with filename " + parameterTwo + ".]";
                    } else {
                        if (nextCommand[1].contains("\"")) {
                            parameterOne = String.join(" ", nextCommand).split("\"")[1];
                        } else {
                            parameterOne = nextCommand[1];
                        }
                        description = "[Saved donors to path " + parameterOne + ".]";
                    }
                }
                break;
            case "help":
                if (nextCommand.length == 1) {
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
        text = String.join(" ", text, command, parameterOne, parameterTwo, parameterThree, description);

        //reset for next call
        command = null;
        parameterOne = null;
        parameterTwo = null;
        parameterThree = null;
        description = null;

        return text;
    }

    /**
     * Records all actions performed in the GUI in the same action history file.
     * Uses a slightly different format, identified by the GUI tag after the timestamp.
     *
     * @param userId the ID of the user performing the action.
     * @param command the action being performed in the app.
     * @return a string to be printed to file containing the action and a brief description.
     */
    public static String prepareFileStringGUI(long userId, String command){
        String text = Donor.dateTimeFormat.format(LocalDateTime.now()) + " GUI";
        Donor donorInfo = Main.getDonorById(userId);
        switch(command) {
            case "login":
                description = "[User " + userId + " logged in successfully.]";
                break;
            case "logout":
                description = "[User " + userId + " logged out successfully.]";
                break;
            case "create":
                if (donorInfo != null) {
                    description = "[Created a new user profile with id of " + userId + " and name " + donorInfo.getName() + ".]";
                }
                break;
            case "update":
                description = "[Updated user attributes.]";
                break;
            case "undo":
                description = "[Reversed last action.]";
                break;
            case "redo":
                description = "[Reverted last undo.]";
                break;
            case "quit":
                description = "[Quit the application.]";
                break;

            //clinician exclusive

            case "view":
                //TODO get donor viewed id (method in main or clinician or something)
                description = "[Viewed donor " + " .]";
                break;
            case "modifyDonor":
                description = "[Modified donor " + "'s attributes.]";
                break;
            case "addMed":
                description = "[Added medications to donor " + ".]";
                break;
            case "removeMed":
                description = "[Removed medications from donor " + ".]";
                break;
            case "search":
                description = "[Searched donor database.]";
                break;
        }
        text = String.join(" ", text, Long.toString(userId), command, description);
        return text;
    }

}
