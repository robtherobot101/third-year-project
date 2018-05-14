package seng302.TUI;

import javafx.collections.ObservableList;
import seng302.Generic.DataManager;
import seng302.Generic.History;
import seng302.Generic.IO;
import seng302.Generic.SearchUtils;
import seng302.User.Attribute.BloodType;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.LoginType;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class runs a command line interface (or text user interface), supplying the core functionality to a user through a terminal.
 */
public class CommandLineInterface {

    private Scanner scanner;

    /**
     * Split the line of commands and arguments into an array of components. Characters within quotation marks will turn into one String, words
     * outside quotation marks separated by spaces will be returned as separate Strings.
     *
     * @param line The line to split
     * @return The array of components
     */
    private String[] splitByQuotationThenSpace(String line) {
        int outSize = 1;
        boolean withinQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            switch (line.charAt(i)) {
                case ' ':
                    if (!withinQuotes) {
                        outSize++;
                    }
                    break;
                case '\"':
                    withinQuotes = !withinQuotes;
            }
        }

        String[] split = new String[outSize];
        withinQuotes = false;
        int lastInIndex = 0, lastOutIndex = 0;
        for (int i = 0; i < line.length(); i++) {
            switch (line.charAt(i)) {
                case ' ':
                    if (!withinQuotes) {
                        if (i == 0) {
                            lastInIndex = 1;
                        } else if (i - lastInIndex > 0) {
                            split[lastOutIndex] = line.substring(lastInIndex, i);
                            lastInIndex = i + 1;
                            lastOutIndex++;
                        } else {
                            lastInIndex = i + 1;
                        }
                    }
                    break;
                case '\"':
                    if (withinQuotes) {
                        split[lastOutIndex] = line.substring(lastInIndex, i);
                        lastInIndex = i + 1;
                        lastOutIndex++;
                    } else {
                        lastInIndex = i + 1;
                    }
                    withinQuotes = !withinQuotes;
            }
        }
        if (lastOutIndex == outSize - 1) {
            split[lastOutIndex] = line.substring(lastInIndex);
            return split;
        } else if (lastOutIndex == outSize) {
            return split;
        } else {
            throw new IllegalStateException("Output array was not filled.");
        }
    }

    /**
     * Print the given string.
     *
     * @param line The string to print
     */
    private void print(String line) {
        System.out.print(line);
    }

    /**
     * Print the given string with a newline char appended.
     *
     * @param line The string to print
     */
    private void printLine(String line) {
        print(line + "\n");
    }

    /**
     * The main loop for the command line interface, which calls specific methods to process each command.
     */
    public void run(InputStream inputStream) {
        scanner = new Scanner(inputStream);
        PrintStream streamOut = History.init();
        if (streamOut == null) {
            printLine("Failed to create action history file, please run again in a directory that the program has access to.");
            return;
        }
        boolean success = false;
        String[] nextCommand;
        do {
            do {
                try {
                    print("TF > ");
                    nextCommand = splitByQuotationThenSpace(scanner.nextLine());
                } catch (NullPointerException e) {
                    nextCommand = new String[]{};
                }
            } while (nextCommand.length == 0);
            switch (nextCommand[0].toLowerCase()) {
                case "add":
                    success = addUser(nextCommand);
                    break;
                case "addorgan":
                    success = addOrgan(nextCommand);
                    break;
                case "delete":
                    success = deleteUser(nextCommand);
                    break;
                case "deleteorgan":
                    success = deleteOrgan(nextCommand);
                    break;
                case "set":
                    success = setUserAttribute(nextCommand);
                    break;
                case "describe":
                    success = describeUser(nextCommand);
                    break;
                case "describeorgans":
                    success = listUserOrgans(nextCommand);
                    break;
                case "list":
                    success = listUsers(nextCommand);
                    break;
                case "listorgans":
                    success = listOrgans(nextCommand);
                    break;
                case "import":
                    success = importUsers(nextCommand);
                    break;
                case "save":
                    success = saveUsers(nextCommand);
                    break;
                case "help":
                    success = showHelp(nextCommand);
                    break;
                case "sql":
                    success = queryDatabase(nextCommand);
                case "quit":
                    success = true;
                    break;
                default:
                    printLine("Command not recognised. Enter 'help' to view available commands, or help <command> to view information " +
                            "about a specific command.");
            }
            if (success) {
                String text = History.prepareFileStringCLI(nextCommand);
                History.printToFile(streamOut, text);
                success = false;
            }
        } while (!nextCommand[0].equals("quit"));
        scanner.close();
    }

    /**
     * Prints a message to the console advising the user on how to correctly use a command they failed to use.
     *
     * @param command The command name
     * @param argc    The argument count
     * @param args    The arguments
     */
    private void printIncorrectUsageString(String command, int argc, String args) {
        switch (argc) {
            case 0:
                printLine(String.format("The %s command does not accept arguments.", command));
                break;
            case 1:
                printLine(String.format("The %s command must be used with 1 argument (%s %s).", command, command, args));
                break;
            default:
                printLine(String.format("The %s command must be used with %d arguments (%s %s).", command, argc, command, args));
        }

    }

    private boolean queryDatabase(String[] nextCommand){
        String[] sqlArray = Arrays.copyOfRange(nextCommand, 1, nextCommand.length);
        String query = String.join(" ", sqlArray);
        String result = SqlSanitation.sanitizeSqlString(query);
        if (!result.equals("")){
            printLine(result);
            return false;
        } else {
            return true;
        }
    }


    /**
     * Creates a new user with a name and date of birth.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean addUser(String[] nextCommand) {
        if (nextCommand.length == 3) {
            try {
                DataManager.users.add(new User(nextCommand[1].replace("\"", ""), LocalDate.parse(nextCommand[2], User.dateFormat)));
                printLine("New user created.");
                return true;
            } catch (DateTimeException e) {
                printLine("Please enter a valid date of birth in the format dd/mm/yyyy.");
            }
        } else {
            printIncorrectUsageString("add", 2, "\"name part 1,name part 2\" <date of birth>");
        }
        return false;
    }

    /**
     * Adds an organ object to a users list of available organs.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean addOrgan(String[] nextCommand) {
        User toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = SearchUtils.getUserById(Long.parseLong(nextCommand[1]));
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("addOrgan", 2, "<id> <organ>");
            return false;
        }

        if (toSet == null) {
            printLine(String.format("User with ID %s not found.", nextCommand[1]));
            return false;
        }
        try {
            toSet.setOrgan(Organ.parse(nextCommand[2]));
            return true;
        } catch (IllegalArgumentException e) {
            printLine("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
            return false;
        }
    }

    /**
     * Ask for confirmation to delete the specified user, and then delete it if the user confirms the action.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean deleteUser(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                long id = Long.parseLong(nextCommand[1]);
                User user = SearchUtils.getUserById(id);
                if (user == null) {
                    printLine(String.format("User with ID %d not found.", id));
                    return false;
                }
                print(String.format("Are you sure you want to delete %s, ID %d? (y/n) ", user.getName(), user.getId()));
                String nextLine = scanner.nextLine();
                while (!nextLine.toLowerCase().equals("y") && !nextLine.toLowerCase().equals("n")) {
                    print("Answer not recognised. Please enter y or n: ");
                    nextLine = scanner.nextLine();
                }
                if (nextLine.equals("y")) {
                    DataManager.users.remove(user);
                    printLine("User removed. This change will permanent once the user list is saved.");
                } else {
                    printLine("User was not removed.");
                }
                return true;
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
            }
        } else {
            printIncorrectUsageString("delete", 1, "<id>");
        }
        return false;
    }

    /**
     * Deletes an organ object from a users available organ set, if it exists.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean deleteOrgan(String[] nextCommand) {
        User toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = SearchUtils.getUserById(Long.parseLong(nextCommand[1]));
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("deleteOrgan", 2, "<id> <organ>");
            return false;
        }

        if (toSet == null) {
            printLine(String.format("User with ID %s not found.", nextCommand[1]));
            return false;
        }
        try {
            toSet.removeOrgan(Organ.parse(nextCommand[2]));
            return true;
        } catch (IllegalArgumentException e) {
            printLine("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, " +
                    "bone-marrow, connective-tissue");
            return false;
        }
    }

    /**
     * Sets a new value for one attribute of a user.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean setUserAttribute(String[] nextCommand) {
        long id;
        String attribute, value;
        /*if (nextCommand.length >= 4 && nextCommand[2].toLowerCase().equals("name") && nextCommand[3].contains("\"")) {
            id = Long.parseLong(nextCommand[1]);
            attribute = "name";
            nextCommand = String.join(" ", nextCommand).split("\"");
            if (nextCommand.length > 1) {
                try {
                    value = nextCommand[1];
                } catch (NumberFormatException e) {
                    printLine("Please enter a valid ID number.");
                    return false;
                }
            } else {
                printLine("Please enter a name.");
                return false;
            }
        } else */

        if (nextCommand.length == 4) {
            try {
                id = Long.parseLong(nextCommand[1]);
                attribute = nextCommand[2];
                value = nextCommand[3];
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("set", 3, "<id> <attribute> <value>");
            return false;
        }

        User toSet = SearchUtils.getUserById(id);
        if (toSet == null) {
            printLine(String.format("User with ID %d not found.", id));
            return false;
        }
        switch (attribute.toLowerCase()) {
            case "name":
                toSet.setName(value);
                printLine("New name set.");
                return true;
            case "dateofbirth":
                try {
                    toSet.setDateOfBirth(LocalDate.parse(value, User.dateFormat));
                    printLine("New date of birth set.");
                    return true;
                } catch (DateTimeException e) {
                    printLine("Please enter a valid date in the format dd/mm/yyyy.");
                }
                return false;
            case "dateofdeath":
                try {
                    toSet.setDateOfDeath(LocalDate.parse(value, User.dateFormat));
                    printLine("New date of death set.");
                    return true;
                } catch (DateTimeException e) {
                    printLine("Please enter a valid date in the format dd/mm/yyyy.");
                }
                return false;
            case "gender":
                try {
                    toSet.setGender(Gender.parse(value));
                    printLine("New gender set.");
                    return true;
                } catch (IllegalArgumentException e) {
                    printLine("Please enter gender as other, female, or male.");
                }
                return false;
            case "height":
                try {
                    double height = Double.parseDouble(value);
                    if (height <= 0) {
                        printLine("Please enter a height which is larger than 0.");
                    } else {
                        toSet.setHeight(height);
                        printLine("New height set.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    printLine("Please enter a numeric height.");
                }
                return false;
            case "weight":
                try {
                    double weight = Double.parseDouble(value);
                    if (weight <= 0) {
                        printLine("Please enter a weight which is larger than 0.");
                    } else {
                        toSet.setWeight(weight);
                        printLine("New weight set.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    printLine("Please enter a numeric weight.");
                }
                return false;
            case "bloodtype":
                try {
                    toSet.setBloodType(BloodType.parse(value));
                    printLine("New blood type set.");
                    return true;
                } catch (IllegalArgumentException e) {
                    printLine("Please enter blood type as A-, A+, B-, B+, AB-, AB+, O-, or O+.");
                }
                return false;
            case "region":
                toSet.setRegion(value);
                printLine("New region set.");
                return true;
            case "currentaddress":
                toSet.setCurrentAddress(value);
                printLine("New address set.");
                return true;
            default:
                printLine("Attribute '" + attribute + "' not recognised. Try name, dateOfBirth, dateOfDeath, gender, height, weight, " +
                        "bloodType, region, or currentAddress.");
                return false;
        }
    }

    /**
     * Searches for users and displays information about them.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean describeUser(String[] nextCommand) {
        String idString;
        /*if (nextCommand.length > 1 && nextCommand[1].contains("\"")) {
            idString = String.join(" ", nextCommand).split("\"")[1];
        } else */
        if (nextCommand.length == 2) {
            idString = nextCommand[1];
        } else {
            printIncorrectUsageString("describe", 1, "<id>");
            return false;
        }

        try {
            User toDescribe = SearchUtils.getUserById(Long.parseLong(idString));
            if (toDescribe == null) {
                printLine(String.format("User with ID %s not found.", idString));
            } else {
                printLine(toDescribe.toString());
            }
        } catch (NumberFormatException e) {
            ObservableList<User> toDescribe = SearchUtils.getUserByName(idString.split(","));
            if (toDescribe.size() == 0) {
                printLine(String.format("No users with names matching %s were found.", idString));
            } else {
                printLine(User.tableHeader);
                for (User user : toDescribe) {
                    printLine(user.getString(true));
                }
            }
        }
        return true;
    }

    /**
     * Lists a specific user and their available organs. If they have none a message is displayed.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean listUserOrgans(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                User user = SearchUtils.getUserById(Long.parseLong(nextCommand[1]));
                if (user == null) {
                    printLine(String.format("User with ID %s not found.", nextCommand[1]));
                } else if (!user.getOrgans().isEmpty()) {
                    printLine(user.getName() + ": " + user.getOrgans());
                    return true;
                } else {
                    printLine("No organs available from user!");
                }
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
            }
        } else {
            printIncorrectUsageString("describeOrgans", 1, "<id>");
        }
        return false;
    }

    /**
     * Displays a table containing information about all users.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean listUsers(String[] nextCommand) {
        if (nextCommand.length == 1) {
            if (DataManager.users.size() > 0) {
                printLine(User.tableHeader);
                for (User user : DataManager.users) {
                    printLine(user.getString(true));
                }
            } else {
                printLine("There are no users to list. Please add or import some before using list.");
            }
            return true;
        } else {
            printIncorrectUsageString("list", 0, null);
            return false;
        }
    }

    /**
     * Lists all users who have at least 1 organ to donate and their available organs.
     * If none exist, a message is displayed.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean listOrgans(String[] nextCommand) {
        if (nextCommand.length == 1) {
            boolean organsAvailable = false;
            for (User user : DataManager.users) {
                if (!user.getOrgans().isEmpty()) {
                    printLine(user.getName() + ": " + user.getOrgans());
                    organsAvailable = true;
                }
            }
            if (!organsAvailable) {
                printLine("No organs available from any user!");
            }
            return true;
        } else {
            printIncorrectUsageString("listOrgans", 0, null);
            return false;
        }
    }

    /**
     * Clear the user list and load a new one from a file.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean importUsers(String[] nextCommand) {
        if (nextCommand.length >= 2) {
            boolean relative = nextCommand[1].equals("-r");
            if ((relative && nextCommand.length == 3) || (!relative && nextCommand.length == 2)) {
                String path;
                if (relative) {
                    path = IO.getJarPath() + File.separatorChar + nextCommand[2].replace('/', File.separatorChar);
                } else {
                    path = nextCommand[1];
                }
                if (IO.importUsers(path, LoginType.USER)) {
                    printLine("User imported from " + path + ".");
                    return true;
                } else {
                    printLine("Failed to import from " + path + ". Make sure the program has access to this file.");
                    return false;
                }
            }
        }
        printLine("The import command must be used with 1 or 2 arguments (import -r <filepath> or import <filepath>).");
        return false;
    }

    /**
     * Save the user list to a file.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean saveUsers(String[] nextCommand) {
        if (nextCommand.length >= 2) {
            boolean relative = nextCommand[1].equals("-r");
            if ((relative && nextCommand.length == 3) || (!relative && nextCommand.length == 2)) {
                String path;
                if (relative) {
                    path = IO.getJarPath() + File.separatorChar + nextCommand[2].replace('/', File.separatorChar);
                } else {
                    path = nextCommand[1];
                }
                if (IO.saveUsers(path, LoginType.USER)) {
                    printLine("User saved to " + path + ".");
                    return true;
                } else {
                    printLine("Failed to save to " + path + ". Make sure the program has access to this file.");
                    return false;
                }
            }
        }
        printLine("The save command must be used with 1 or 2 arguments (save -r <filepath> or save <filepath>).");
        return false;
    }

    /**
     * Shows help either about which commands are available or about a specific command's usage.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean showHelp(String[] nextCommand) {
        if (nextCommand.length == 1) {
            printLine("Valid commands are: "
                    + "\n\t-add \"First Name,name part 2,name part n\" <date of birth>"
                    + "\n\t-addOrgan <id> <organ>"
                    + "\n\t-delete <id>"
                    + "\n\t-deleteOrgan <id> <organ>"
                    + "\n\t-set <id> <attribute> <value>"
                    + "\n\t-describe <id> OR describe \"name substring 1,name substring 2,name substring n\""
                    + "\n\t-describeOrgans <id>"
                    + "\n\t-list"
                    + "\n\t-listOrgans"
                    + "\n\t-import [-r] <filename>"
                    + "\n\t-save [-r] <path> OR save [-r] \"File path with spaces\""
                    + "\n\t-help [<command>]"
                    + "\n\t-quit");
        } else if (nextCommand.length == 2) {
            switch (nextCommand[1].toLowerCase()) {
                case "add":
                    printLine("This command adds a new user with a name and date of birth.\n"
                            + "The syntax is: add <name> <date of birth>\n"
                            + "Rules:\n"
                            + "-The names must be comma separated without a space around the comma (eg. Andrew,Neil,Davidson)\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "-The date of birth must be entered in the format: dd/mm/yyyy\n"
                            + "Example valid usage: add \"Test,User with,SpacesIn Name\" 01/05/1994");
                    break;
                case "addorgan":
                    printLine("This command adds one organ to donate to a user. To find the id of a user, use the list and "
                            + "describe commands.\n"
                            + "The syntax is: addOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: addOrgan 0 skin");
                    break;
                case "delete":
                    printLine("This command deletes one user. To find the id of a user, use the list and describe commands.\n"
                            + "The syntax is: delete <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-You will be asked to confirm that you want to delete this user\n"
                            + "Example valid usage: delete 1");
                    break;
                case "deleteorgan":
                    printLine("This command removes one offered organ from a user. To find the id of a user, use the list and "
                            + "describe commands.\n"
                            + "The syntax is: deleteOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: deleteOrgan 5 kidney");
                    break;
                case "set":
                    printLine("This command sets one attribute (apart from organs to be donated) of a user. To find the id of a user, "
                            + "use the list and describe commands. To add or delete organs, instead use the addOrgan and deleteOrgan commands.\n"
                            + "The syntax is: set <id> <attribute> <value>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The attribute must be one of the following (case insensitive): name, dateOfBirth, dateOfDeath, gender, height, "
                            + "weight, bloodType, region, currentAddress\n"
                            + "-If a name or names are used, all users whose names contain the input names in order will be returned as matches\n"
                            + "-The gender must be: male, female, or other\n"
                            + "-The bloodType must be: A-, A+, B-, B+, AB-, AB+, O-, or O+\n"
                            + "-The height and weight must be numbers that are larger than 0\n"
                            + "-The date of birth and date of death values must be entered in the format: dd/mm/yyyy\n"
                            + "Example valid usage: set 2 bloodtype ab+");
                    break;
                case "describe":
                    printLine("This command searches users and displays information about them. To find the id of a user, use the list "
                            + "and describe commands.\n"
                            + "The syntax is: describe <id> OR describe <name>\n"
                            + "Rules:\n"
                            + "-If an id number is to be used as search criteria, it must be a number that is 0 or larger\n"
                            + "-If a name or names are used, the names must be comma separated without a space around the comma (eg. drew,david)\n"
                            + "-If a name or names are used, all users whose names contain the input names in order will be returned as matches\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "Example valid usage: describe \"andrew,son\'");
                    break;
                case "describeorgans":
                    printLine("This command displays the organs which a user will donate or has donated. To find the id of a user, "
                            + "use the list and describe commands.\n"
                            + "The syntax is: describeOrgans <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "Example valid usage: describeOrgans 4");
                    break;
                case "list":
                    printLine("This command lists all information about all users in a table.\n"
                            + "Example valid usage: list");
                    break;
                case "listorgans":
                    printLine("This command displays all of the organs that are currently offered by each user. User that are "
                            + "not yet offering any organs are not shown.\n"
                            + "Example valid usage: listOrgans");
                    break;
                case "import":
                    printLine("This command replaces all user data in the system with an imported JSON object.\n"
                            + "The syntax is: import [-r] <filename>\n"
                            + "Rules:\n"
                            + "-If the -r flag is present, the filepath will be interpreted as relative\n"
                            + "-If the filepath has spaces in it, it must be enclosed with quotation marks (\")\n"
                            + "-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows\n"
                            + "-The file must be of the same format as those saved from this application\n"
                            + "Example valid usage: import -r ../user_list_FINAL.txt");
                    break;
                case "save":
                    printLine("This command saves the current user database to a file in JSON format.\n"
                            + "The syntax is: save [-r] <filepath>\n"
                            + "Rules:\n"
                            + "-If the -r flag is present, the filepath will be interpreted as relative\n"
                            + "-If the filepath has spaces in it, it must be enclosed with quotation marks (\")\n"
                            + "-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows\n"
                            + "Example valid usage: save -r \"new folder/users.json\"");
                    break;
                case "help":
                    printLine("This command displays information about how to use this program.\n"
                            + "The syntax is: help OR help <command>\n"
                            + "Rules:\n"
                            + "-If the command argument is passed, the command must be: add, addOrgan, delete, deleteOrgan, set, describe, "
                            + "describeOrgans, list, listOrgans, import, save, help, or quit.\n"
                            + "Example valid usage: help help");
                    break;
                case "sql":
                    printLine("This command can be used to query sql select commands to the database.\n"
                            + "The syntax is: sql <SQL query>\n"
                            + "Rules:\n"
                            + "-Only SELECT queries can be sent"
                            + "Example valid usage: sql SELECT id from user where first_name = 'Steve' and last_name = 'Johnson'");
                    break;
                case "quit":
                    printLine("This command exits the program.\n"
                            + "Example valid usage: quit");
                    break;
                default:
                    printLine("Can not offer help with this command as it is not a valid command.");
                    return false;
            }
        } else {
            printLine("The help command must be used with 0 or 1 arguments (help or help <command>).");
            return false;
        }
        return true;
    }
}
