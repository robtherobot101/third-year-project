package seng302.TUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import seng302.Core.*;
import seng302.Files.History;

import java.io.PrintStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.logging.*;

/**
 * This class runs a command line interface (or text user interface), supplying the core functionality to a user through a terminal.
 */
public class CommandLineInterface {
    private LineReader scanner;

    /**
     * The main loop for the command line interface, which calls specific methods to process each command.
     */
    public void run() {
        Terminal terminal;
        try {
            Logger logger = Logger.getLogger("");
            logger.getHandlers()[0].setLevel(Level.OFF);
            logger.setLevel(Level.OFF);
            terminal = TerminalBuilder.terminal();
            scanner = LineReaderBuilder.builder().terminal(terminal).build();
        } catch (IOException e) {
            System.err.println("Failed to start JLine terminal, exiting.");
            return;
        }
        PrintStream streamOut = History.init();
        if (streamOut == null) {
            System.out.println("Failed to create action history file, please run again in a directory that the program has access to.");
            return;
        }
        boolean success = false;
        String[] nextCommand;
        do {
            do {
                try {
                    nextCommand = scanner.readLine("TF > ").split(" ");
                } catch (NullPointerException | UserInterruptException e) {
                    nextCommand = new String[]{};
                } catch (EndOfFileException e) {
                    return;
                }
            } while (nextCommand.length == 0);
            switch (nextCommand[0].toLowerCase()) {
                case "add":
                    success = addDonor(nextCommand);
                    break;
                case "addorgan":
                    success = addOrgan(nextCommand);
                    break;
                case "delete":
                    success = deleteDonor(nextCommand);
                    break;
                case "deleteorgan":
                    success = deleteOrgan(nextCommand);
                    break;
                case "set":
                    success = setDonorAttribute(nextCommand);
                    break;
                case "describe":
                    success = describeDonor(nextCommand);
                    break;
                case "describeorgans":
                    success = listDonorOrgans(nextCommand);
                    break;
                case "list":
                    success = listDonors(nextCommand);
                    break;
                case "listorgans":
                    success = listOrgans(nextCommand);
                    break;
                case "import":
                    success = importDonors(nextCommand);
                    break;
                case "save":
                    success = saveDonors(nextCommand);
                    break;
                case "help":
                    success = showHelp(nextCommand);
                    break;
                case "quit":
                    success = true;
                    break;
                default:
                    System.out.println("Command not recognised. Enter 'help' to view available commands, or help <command> to view information " +
                            "about a specific command.");
            }
            if (success) {
                String text = History.prepareFileStringCLI(nextCommand);
                History.printToFile(streamOut, text);
                success = false;
            }
        } while (!nextCommand[0].equals("quit"));
        try {
            terminal.close();
        } catch (IOException e) {
            System.err.println("Failed to close JLine terminal.");
        }

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
                System.out.println(String.format("The %s command does not accept arguments.", command));
                break;
            case 1:
                System.out.println(String.format("The %s command must be used with 1 argument (%s %s).", command, command, args));
                break;
            default:
                System.out.println(String.format("The %s command must be used with %d arguments (%s %s).", command, argc, command, args));
        }

    }

    /**
     * Creates a new donor with a name and date of birth.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean addDonor(String[] nextCommand) {
        if (nextCommand.length == 3) {
            try {
                Main.donors.add(new Donor(nextCommand[1].replace("\"", ""), LocalDate.parse(nextCommand[2], Donor.dateFormat)));
                System.out.println("New donor created.");
                return true;
            } catch (DateTimeException e) {
                System.out.println("Please enter a valid date of birth in the format dd/mm/yyyy.");
            }
        } else if (nextCommand.length > 3 && nextCommand[1].contains("\"") && nextCommand[nextCommand.length - 2].contains("\"")) {
            String date = nextCommand[nextCommand.length - 1];
            nextCommand = String.join(" ", nextCommand).split("\"");
            if (nextCommand.length == 3) {
                try {
                    Main.donors.add(new Donor(nextCommand[1], LocalDate.parse(date, Donor.dateFormat)));
                    System.out.println("New donor added.");
                    return true;
                } catch (DateTimeException e) {
                    System.out.println("Please enter a valid date of birth in the format dd/mm/yyyy.");
                }
            } else {
                printIncorrectUsageString("add", 2, "\"name part 1,name part 2\" <date of birth>");
            }
        } else {
            printIncorrectUsageString("add", 2, "\"name part 1,name part 2\" <date of birth>");
        }
        return false;
    }

    /**
     * Adds an organ object to a donors list of available organs.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean addOrgan(String[] nextCommand) {
        Donor toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = Main.getDonorById(Long.parseLong(nextCommand[1]));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("addOrgan", 2, "<id> <organ>");
            return false;
        }

        if (toSet == null) {
            System.out.println(String.format("Donor with ID %s not found.", nextCommand[1]));
            return false;
        }
        try {
            toSet.setOrgan(Organ.parse(nextCommand[2]));
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
            return false;
        }
    }

    /**
     * Ask for confirmation to delete the specified donor, and then delete it if the user confirms the action.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    public boolean deleteDonor(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                long id = Long.parseLong(nextCommand[1]);
                Donor donor = Main.getDonorById(id);
                if (donor == null) {
                    System.out.println(String.format("Donor with ID %d not found.", id));
                    return false;
                }
                String nextLine = scanner.readLine(String.format("Are you sure you want to delete %s, ID %d? (y/n) ", donor.getName(), donor.getId
                        ()));
                while (!nextLine.toLowerCase().equals("y") && !nextLine.toLowerCase().equals("n")) {
                    nextLine = scanner.readLine("Answer not recognised. Please enter y or n: ");
                }
                if (nextLine.equals("y")) {
                    Main.donors.remove(donor);
                    System.out.println("Donor removed. This change will permanent once the donor list is saved.");
                } else {
                    System.out.println("Donor was not removed.");
                }
                return true;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID number.");
            }
        } else {
            printIncorrectUsageString("delete", 1, "<id>");
        }
        return false;
    }

    /**
     * Deletes an organ object from a donors available organ set, if it exists.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean deleteOrgan(String[] nextCommand) {
        Donor toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = Main.getDonorById(Long.parseLong(nextCommand[1]));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("deleteOrgan", 2, "<id> <organ>");
            return false;
        }

        if (toSet == null) {
            System.out.println(String.format("Donor with ID %s not found.", nextCommand[1]));
            return false;
        }
        try {
            toSet.removeOrgan(Organ.parse(nextCommand[2]));
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, " +
                    "bone-marrow, connective-tissue");
            return false;
        }
    }

    /**
     * Sets a new value for one attribute of a donor.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean setDonorAttribute(String[] nextCommand) {
        long id;
        String attribute, value;
        if (nextCommand.length >= 4 && nextCommand[2].toLowerCase().equals("name") && nextCommand[3].contains("\"")) {
            id = Long.parseLong(nextCommand[1]);
            attribute = "name";
            nextCommand = String.join(" ", nextCommand).split("\"");
            if (nextCommand.length > 1) {
                try {
                    value = nextCommand[1];
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid ID number.");
                    return false;
                }
            } else {
                System.out.println("Please enter a name.");
                return false;
            }
        } else if (nextCommand.length == 4) {
            try {
                id = Long.parseLong(nextCommand[1]);
                attribute = nextCommand[2];
                value = nextCommand[3];
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("set", 3, "<id> <attribute> <value>");
            return false;
        }

        Donor toSet = Main.getDonorById(id);
        if (toSet == null) {
            System.out.println(String.format("Donor with ID %d not found.", id));
            return false;
        }
        switch (attribute.toLowerCase()) {
            case "name":
                toSet.setName(value);
                System.out.println("New name set.");
                return true;
            case "dateofbirth":
                try {
                    toSet.setDateOfBirth(LocalDate.parse(value, Donor.dateFormat));
                    System.out.println("New date of birth set.");
                    return true;
                } catch (DateTimeException e) {
                    System.out.println("Please enter a valid date in the format dd/mm/yyyy.");
                }
                return false;
            case "dateofdeath":
                try {
                    toSet.setDateOfDeath(LocalDate.parse(value, Donor.dateFormat));
                    System.out.println("New date of death set.");
                    return true;
                } catch (DateTimeException e) {
                    System.out.println("Please enter a valid date in the format dd/mm/yyyy.");
                }
                return false;
            case "gender":
                try {
                    toSet.setGender(Gender.parse(value));
                    System.out.println("New gender set.");
                    return true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Please enter gender as other, female, or male.");
                }
                return false;
            case "height":
                try {
                    double height = Double.parseDouble(value);
                    if (height <= 0) {
                        System.out.println("Please enter a height which is larger than 0.");
                    } else {
                        toSet.setHeight(height);
                        System.out.println("New height set.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a numeric height.");
                }
                return false;
            case "weight":
                try {
                    double weight = Double.parseDouble(value);
                    if (weight <= 0) {
                        System.out.println("Please enter a weight which is larger than 0.");
                    } else {
                        toSet.setWeight(weight);
                        System.out.println("New weight set.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a numeric weight.");
                }
                return false;
            case "bloodtype":
                try {
                    toSet.setBloodType(BloodType.parse(value));
                    System.out.println("New blood type set.");
                    return true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Please enter blood type as A-, A+, B-, B+, AB-, AB+, O-, or O+.");
                }
                return false;
            case "region":
                toSet.setRegion(value);
                System.out.println("New region set.");
                return true;
            case "currentaddress":
                toSet.setCurrentAddress(value);
                System.out.println("New address set.");
                return true;
            default:
                System.out.println("Attribute '" + attribute + "' not recognised. Try name, dateOfBirth, dateOfDeath, gender, height, weight, " +
                        "bloodType, region, or currentAddress.");
                return false;
        }
    }

    /**
     * Searches for donors and displays information about them.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean describeDonor(String[] nextCommand) {
        String idString;
        if (nextCommand.length > 1 && nextCommand[1].contains("\"")) {
            idString = String.join(" ", nextCommand).split("\"")[1];
        } else if (nextCommand.length == 2) {
            idString = nextCommand[1];
        } else {
            printIncorrectUsageString("describe", 1, "<id>");
            return false;
        }

        try {
            Donor toDescribe = Main.getDonorById(Long.parseLong(idString));
            if (toDescribe == null) {
                System.out.println(String.format("Donor with ID %s not found.", idString));
            } else {
                System.out.println(toDescribe);
            }
        } catch (NumberFormatException e) {
            ArrayList<Donor> toDescribe = Main.getDonorByName(idString.split(","));
            if (toDescribe.size() == 0) {
                System.out.println(String.format("No donors with names matching %s were found.", idString));
            } else {
                System.out.println(Donor.tableHeader);
                for (Donor donor : toDescribe) {
                    System.out.println(donor.getString(true));
                }
            }
        }
        return true;
    }

    /**
     * Lists a specific donor and their available organs. If they have none a message is displayed.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean listDonorOrgans(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                Donor donor = Main.getDonorById(Long.parseLong(nextCommand[1]));
                if (donor == null) {
                    System.out.println(String.format("Donor with ID %s not found.", nextCommand[1]));
                } else if (!donor.getOrgans().isEmpty()) {
                    System.out.println(donor.getName() + ": " + donor.getOrgans());
                    return true;
                } else {
                    System.out.println("No organs available from donor!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID number.");
            }
        } else {
            printIncorrectUsageString("describeOrgans", 1, "<id>");
        }
        return false;
    }

    /**
     * Displays a table containing information about all donors.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean listDonors(String[] nextCommand) {
        if (nextCommand.length == 1) {
            if (Main.donors.size() > 0) {
                System.out.println(Donor.tableHeader);
                for (Donor donor : Main.donors) {
                    System.out.println(donor.getString(true));
                }
            } else {
                System.out.println("There are no donors to list. Please add or import some before using list.");
            }
            return true;
        } else {
            printIncorrectUsageString("list", 0, null);
            return false;
        }
    }

    /**
     * Lists all donors who have at least 1 organ to donate and their available organs.
     * If none exist, a message is displayed.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean listOrgans(String[] nextCommand) {
        if (nextCommand.length == 1) {
            boolean organsAvailable = false;
            for (Donor donor : Main.donors) {
                if (!donor.getOrgans().isEmpty()) {
                    System.out.println(donor.getName() + ": " + donor.getOrgans());
                    organsAvailable = true;
                }
            }
            if (!organsAvailable) {
                System.out.println("No organs available from any donor!");
            }
            return true;
        } else {
            printIncorrectUsageString("listOrgans", 0, null);
            return false;
        }
    }

    /**
     * Clear the donor list and load a new one from a file.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean importDonors(String[] nextCommand) {
        if (nextCommand.length >= 2) {
            boolean relative = nextCommand[1].equals("-r");
            String path = null;
            int startLength = relative ? 2 : 1;
            if (nextCommand.length >= 1 + startLength) {
                if (nextCommand[startLength].contains("\"")) {
                    path = String.join(" ", nextCommand).split("\"")[1];
                } else if (nextCommand.length == startLength + 1) {
                    path = nextCommand[startLength];
                }
            }
            if (path != null) {
                if (relative) {
                    path = Main.getJarPath() + File.separatorChar + path.replace('/', File.separatorChar);
                }
                if (Main.importDonors(path)) {
                    System.out.println("Donors imported from " + path + ".");
                    return true;
                } else {
                    System.out.println("Failed to import from " + path + ". Make sure the program has access to this file.");
                }
            } else {
                System.out.println("The import command must be used with 1 or 2 arguments (import -r <filepath> or import <filepath>).");
            }
        } else {
            System.out.println("The import command must be used with 1 or 2 arguments (import -r <filepath> or import <filepath>).");
        }
        return false;
    }

    /**
     * Save the donor list to a file.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean saveDonors(String[] nextCommand) {
        if (nextCommand.length >= 2) {
            boolean relative = nextCommand[1].equals("-r");
            String path = null;
            int startLength = relative ? 2 : 1;
            if (nextCommand.length >= 1 + startLength) {
                if (nextCommand[startLength].contains("\"")) {
                    path = String.join(" ", nextCommand).split("\"")[1];
                } else if (nextCommand.length == startLength + 1) {
                    path = nextCommand[startLength];
                }
            }
            if (path != null) {
                if (relative) {
                    path = Main.getJarPath() + File.separatorChar + path.replace('/', File.separatorChar);
                }
                if (Main.saveDonors(path)) {
                    System.out.println("Donors saved to " + path + ".");
                    return true;
                } else {
                    System.out.println("Failed to save to " + path + ". Make sure the program has access to this file.");
                }
            } else {
                System.out.println("The save command must be used with 1 or 2 arguments (save -r <filepath> or save <filepath>).");
            }
        } else {
            System.out.println("The save command must be used with 1 or 2 arguments (save -r <filepath> or save <filepath>).");
        }
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
            System.out.println("Valid commands are: "
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
                    System.out.println("This command adds a new donor with a name and date of birth.\n"
                            + "The syntax is: add <name> <date of birth>\n"
                            + "Rules:\n"
                            + "-The names must be comma separated without a space around the comma (eg. Andrew,Neil,Davidson)\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "-The date of birth must be entered in the format: dd/mm/yyyy\n"
                            + "Example valid usage: add \"Test,User with,SpacesIn Name\" 01/05/1994");
                    break;
                case "addorgan":
                    System.out.println("This command adds one organ to donate to a donor. To find the id of a donor, use the list and "
                            + "describe commands.\n"
                            + "The syntax is: addOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: addOrgan 0 skin");
                    break;
                case "delete":
                    System.out.println("This command deletes one donor. To find the id of a donor, use the list and describe commands.\n"
                            + "The syntax is: delete <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-You will be asked to confirm that you want to delete this donor\n"
                            + "Example valid usage: delete 1");
                    break;
                case "deleteorgan":
                    System.out.println("This command removes one offered organ from a donor. To find the id of a donor, use the list and "
                            + "describe commands.\n"
                            + "The syntax is: deleteOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: deleteOrgan 5 kidney");
                    break;
                case "set":
                    System.out.println("This command sets one attribute (apart from organs to be donated) of a donor. To find the id of a donor, "
                            + "use the list and describe commands. To add or delete organs, instead use the addOrgan and deleteOrgan commands.\n"
                            + "The syntax is: set <id> <attribute> <value>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The attribute must be one of the following (case insensitive): name, dateOfBirth, dateOfDeath, gender, height, "
                            + "weight, bloodType, region, currentAddress\n"
                            + "-If a name or names are used, all donors whose names contain the input names in order will be returned as matches\n"
                            + "-The gender must be: male, female, or other\n"
                            + "-The bloodType must be: A-, A+, B-, B+, AB-, AB+, O-, or O+\n"
                            + "-The height and weight must be numbers that are larger than 0\n"
                            + "-The date of birth and date of death values must be entered in the format: dd/mm/yyyy\n"
                            + "Example valid usage: set 2 bloodtype ab+");
                    break;
                case "describe":
                    System.out.println("This command searches donors and displays information about them. To find the id of a donor, use the list "
                            + "and describe commands.\n"
                            + "The syntax is: describe <id> OR describe <name>\n"
                            + "Rules:\n"
                            + "-If an id number is to be used as search criteria, it must be a number that is 0 or larger\n"
                            + "-If a name or names are used, the names must be comma separated without a space around the comma (eg. drew,david)\n"
                            + "-If a name or names are used, all donors whose names contain the input names in order will be returned as matches\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "Example valid usage: describe \"andrew,son\'");
                    break;
                case "describeorgans":
                    System.out.println("This command displays the organs which a donor will donate or has donated. To find the id of a donor, "
                            + "use the list and describe commands.\n"
                            + "The syntax is: describeOrgans <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "Example valid usage: describeOrgans 4");
                    break;
                case "list":
                    System.out.println("This command lists all information about all donors in a table.\n"
                            + "Example valid usage: list");
                    break;
                case "listorgans":
                    System.out.println("This command displays all of the organs that are currently offered by each donor. Donors that are "
                            + "not yet offering any organs are not shown.\n"
                            + "Example valid usage: listOrgans");
                    break;
                case "import":
                    System.out.println("This command replaces all donor data in the system with an imported JSON object.\n"
                            + "The syntax is: import [-r] <filename>\n"
                            + "Rules:\n"
                            + "-If the -r flag is present, the filepath will be interpreted as relative\n"
                            + "-If the filepath has spaces in it, it must be enclosed with quotation marks (\")\n"
                            + "-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows\n"
                            + "-The file must be of the same format as those saved from this application\n"
                            + "Example valid usage: import -r ../donor_list_FINAL.txt");
                    break;
                case "save":
                    System.out.println("This command saves the current donor database to a file in JSON format.\n"
                            + "The syntax is: save [-r] <filepath>\n"
                            + "Rules:\n"
                            + "-If the -r flag is present, the filepath will be interpreted as relative\n"
                            + "-If the filepath has spaces in it, it must be enclosed with quotation marks (\")\n"
                            + "-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows\n"
                            + "Example valid usage: save -r \"new folder/donors.json\"");
                    break;
                case "help":
                    System.out.println("This command displays information about how to use this program.\n"
                            + "The syntax is: help OR help <command>\n"
                            + "Rules:\n"
                            + "-If the command argument is passed, the command must be: add, addOrgan, delete, deleteOrgan, set, describe, "
                            + "describeOrgans, list, listOrgans, import, save, help, or quit.\n"
                            + "Example valid usage: help help");
                    break;
                case "quit":
                    System.out.println("This command exits the program.\n"
                            + "Example valid usage: quit");
                    break;
                default:
                    System.out.println("Can not offer help with this command as it is not a valid command.");
                    return false;
            }
        } else {
            System.out.println("The help command must be used with 0 or 1 arguments (help or help <command>).");
            return false;
        }
        return true;
    }
}
