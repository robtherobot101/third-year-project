package seng302.Generic;

import seng302.User.User;

import java.io.*;
import java.time.LocalDateTime;

public class History {

    private static File actionHistory;
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
            actionHistory = new File(IO.getJarPath() + File.separatorChar + "actionHistory.txt");
            FileOutputStream fout = new FileOutputStream(actionHistory, true);
            PrintStream out = new PrintStream(fout);
            out.println(User.dateTimeFormat.format(LocalDateTime.now()) + " ==== NEW SESSION ====");
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
        String text = User.dateTimeFormat.format(LocalDateTime.now()) + " CLI";
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
                description = "[Created a user with name: " + parameterOne + ", and date of birth:" + parameterTwo + "]";
                break;
            case "addorgan":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                description = "[Added organ of type " + parameterTwo + " to user " + parameterOne + ".]";
                break;
            case "delete":
                parameterOne = nextCommand[1];
                description = "[Deleted user " + parameterOne + " from the list.]";
                break;
            case "deleteorgan":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                description = "[Removed organ of type " + parameterTwo + " from user " + parameterOne + ".]";
                break;
            case "set":
                parameterOne = nextCommand[1];
                parameterTwo = nextCommand[2];
                parameterThree = nextCommand[3];
                description = "[Attempted to change the attribute " + parameterTwo + " of user " + parameterOne + ".]";
                break;
            case "describe":
                parameterOne = nextCommand[1];
                description = "[Listed the attributes of user " + nextCommand[1] + ".]";
                break;
            case "describeorgans":
                parameterOne = nextCommand[1];
                description = "[Listed organs available from user " + parameterOne + ".]";
                break;
            case "list":
                description = "[Listed all users.]";
                break;
            case "listorgans":
                description = "[Listed all organs available from all users.]";
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
                        description = "[Imported users from relative path with filename " + parameterTwo + ".]";
                    } else {
                        if (nextCommand[1].contains("\"")) {
                            parameterOne = String.join(" ", nextCommand).split("\"")[1];
                        } else {
                            parameterOne = nextCommand[1];
                        }
                        description = "[Imported users from path " + parameterOne + ".]";
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
                        description = "[Saved users to relative path with filename " + parameterTwo + ".]";
                    } else {
                        if (nextCommand[1].contains("\"")) {
                            parameterOne = String.join(" ", nextCommand).split("\"")[1];
                        } else {
                            parameterOne = nextCommand[1];
                        }
                        description = "[Saved users to path " + parameterOne + ".]";
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
        String text = User.dateTimeFormat.format(LocalDateTime.now()) + " GUI";
        User userInfo = Main.getUserById(userId);
        System.out.println("Command: "+command);

        switch(command) {
                case "login":
                    description = "[User " + userId + " logged in successfully.]";
                    break;
                case "logout":
                    description = "[User " + userId + " logged out successfully.]";
                    break;
                case "create":
                    if (userInfo != null) {
                        description = "[Created a new user profile with id of " + userId + " and name " + userInfo.getName() + ".]";
                    }
                    break;
                case "update":
                    description = "[Updated user attributes.]";
                    break;
                case "updateAccountSettings":
                    description = "[Updated user account settings.]";
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
                    //TODO get user viewed id (method in main or clinician or something)
                    description = "[-Clinician- Viewed user " + userInfo.getName() + " .]";
                    break;
                case "modifyUser":
                    description = "[-Clinician- Modified user " + userInfo.getName() + "'s attributes.]";
                    break;
                case "waitinglist":
                    description = "[-Clinician- Modified user " + userInfo.getName() + "'s waiting list.]";
                    break;
                case "medications":
                    description = "[-Clinician- Modified user " + userInfo.getName() + "'s medications.]";
                    break;
                case "diseases":
                    description = "[-Clinician- Modified user " + userInfo.getName() + "'s diseases.]";
                    break;
                case "procedures":
                    description = "[-Clinician- Modified user " + userInfo.getName() + "'s procedures.]";
                    break;
                case "search":
                    description = "[-Clinician- Searched user database.]";
                    break;
                case "deregisterDeath":
                    description = "[-Clinician- Deregistered all organs from " + userInfo.getName() + "'s waiting list due to receiver deceased.";
                    break;
                case "deregisterError":
                    description = "[-Clinician- Deregistered an organ from user " + userInfo.getName() + "'s due to a register error.]";
                    break;
            }

        text = String.join(" ", text, Long.toString(userId), command, description);
        return text;
    }

    /**
     * Function which takes the action history text file and parses it to create a string of all the user history.
     * @return A string of all the action history for the application.
     */
    public static String readFile(){

        String line;
        String actionHistoryString = "";

        try {
            FileReader fileReader =
                    new FileReader(actionHistory);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                actionHistoryString += line;
                actionHistoryString += " \n";
            }
            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found / initialized");
        } catch (IOException e) {
            System.out.println("Error Reading file");
            e.printStackTrace();
        }
        return actionHistoryString;
    }

    /**
     * Function which parses the history text file for all the actions for the given user.
     * @param userid the given user which we are looking for the history
     * @return A two dimensional string list for each users actions with the second list being the action history broken up
     */
    public static String[][] getUserHistory(long userid) {
        String history = readFile();
        String[] historyList = history.split("\n");
        String[][] userHistory = new String[historyList.length][6];
        int index = 0;
        for (String action : historyList) {
            String[] actionDetails = action.split(" ");
            if (actionDetails[2].equals("====")) {

            } else if (actionDetails[3].length() < 4) {
                if (Long.parseLong(actionDetails[3]) == userid) {
                    userHistory[index] = actionDetails;
                    index++;
                }
            }
        }
        return userHistory;
    }
}
