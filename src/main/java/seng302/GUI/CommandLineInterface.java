package seng302.GUI;

import java.sql.SQLException;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import seng302.GUI.Controllers.UserWindowController;
import seng302.Generic.*;
import seng302.User.Attribute.BloodType;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.User;

import javax.xml.crypto.Data;
import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import static seng302.Generic.IO.streamOut;
import java.util.ArrayList;
import java.util.List;

import static seng302.Generic.IO.streamOut;



/**
 * This class runs a command line interface (or text user interface), supplying the core functionality to a user through a terminal.
 */
public class CommandLineInterface {
    private boolean isDeleting = false;
    private User userToDelete;
    private Clinician clinicianToDelete;
    private String[] deleteCommand;
    private List<String> outputString;

    public void setOutput(List<String> outputString) {
        this.outputString = outputString;
    }

    /**
     * Update a user to display the most up to date information in the GUI.
     *
     * @param user The user to update
     */
    private void refreshUser(User user) {
        for (UserWindowController userWindowController: WindowManager.getCliniciansUserWindows().values()) {
            if (user == userWindowController.getCurrentUser()) {
                userWindowController.populateUserAttributes();
                userWindowController.populateHistoryTable();
            }
        }
    }

    /**
     * Split the line of commands and arguments into an array of components. Characters within quotation marks will turn into one String, words
     * outside quotation marks separated by spaces will be returned as separate Strings.
     *
     * @param line The line to split
     * @return The array of components
     */
    public String[] splitByQuotationThenSpace(String line) {
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
        outputString.add(line);
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
     * Interprets the given command and calls the relevant method. If the command
     * is executed successfully, the action history file is updated. If the command
     * is not recognised, a message is printed to the console
     * @param command The given command
     */
    public void readCommand(String command) {
        if (command == null || command.isEmpty()) {
            return;
        }
        boolean success = false;
        String[] nextCommand;
        if (isDeleting) {
            success = checkBeforeDelete(command);
            if (isDeleting) {
                return;
            }
            nextCommand = deleteCommand;
        } else {
            nextCommand = splitByQuotationThenSpace(command);
            switch (nextCommand[0].toLowerCase()) {
                case "adduser":
                    success = addUser(nextCommand);
                    break;
                case "addclinician":
                    success = addClinician(nextCommand);
                    break;
                case "adddonationorgan":
                    success = addDonationOrgan(nextCommand);
                    break;
                case "addwaitinglistorgan":
                    success = addWaitingListOrgan(nextCommand);
                    break;
                case "deleteuser":
                    deleteUser(nextCommand);
                    return; //Returns as the command will not be complete until later confirmation
                case "deleteclinician":
                    deleteClinician(nextCommand);
                    return; //Returns as the command will not be complete until later confirmation
                case "removewaitinglistorgan":
                    success = removeWaitingListOrgan(nextCommand);
                    break;
                case "removedonationorgan":
                    success = removeDonationOrgan(nextCommand);
                    break;
                case "updateuser":
                    success = updateUser(nextCommand);
                    break;
                case "updateclinician":
                    success = updateClinician(nextCommand);
                    break;
                case "describeuser":
                    success = describeUser(nextCommand);
                    break;

                case "describeclinician":
                    success = describeClinician(nextCommand);
                    break;
                case "describeorgans":
                    success = listUserOrgans(nextCommand);
                    break;
                case "listusers":
                    success = listUsers(nextCommand);
                    break;

                case "listclinicians":
                    success = listClinicians(nextCommand);
                    break;
                case "listorgans":
                    success = listOrgans(nextCommand);
                    break;
                case "import":
                    success = importUsers(nextCommand);
                    break;
                case "save":
                    success = save(nextCommand);
                    break;
                case "clear":
                    success = true;
                    outputString.clear();
                    break;
                case "help":
                    success = showHelp(nextCommand);
                    break;
                case "sql":
                    queryDatabase(nextCommand);
                    break;
                default:
                    printLine("Command not recognised. Enter 'help' to view available commands, or help <command> to view information " +
                            "about a specific command.");
            }
        }
        if (success) {
            String text = History.prepareFileStringCLI(nextCommand);
            History.printToFile(streamOut, text);
        }
    }

    private boolean describeClinician(String[] nextCommand) {
        String idString;

        if (nextCommand.length == 2) {
            idString = nextCommand[1];
        } else {
            printIncorrectUsageString("describe", 1, "<id>");
            return false;
        }

        try {
            Clinician toDescribe = SearchUtils.getClinicianById(Long.parseLong(idString));
            if (toDescribe == null) {
                printLine(String.format("Clinician with ID %s not found.", idString));
            } else {
                printLine(toDescribe.getString(false));
            }
        } catch (NumberFormatException e) {
            System.out.println("ID entered was not valid.");
        }
        return true;
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

    private void queryDatabase(String[] nextCommand){
        SqlSanitation sqlSanitation = new SqlSanitation();
        String[] sqlArray = Arrays.copyOfRange(nextCommand, 1, nextCommand.length);
        String query = String.join(" ", sqlArray);
        String result = sqlSanitation.sanitizeSqlString(query);
        if (!result.equals("")){
            printLine(result);
        } else {
            printLine(sqlSanitation.executeQuery(query));
        }
    }


    /**
     * Creates a new user with a name and date of birth.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean addUser(String[] nextCommand) {
        if (nextCommand.length == 5) {
            try {

                User insertUser = new User(nextCommand[3].replace("\"", ""), LocalDate.parse(nextCommand[4], User.dateFormat));
                insertUser.setUsername(nextCommand[1]);
                insertUser.setPassword(nextCommand[2]);
                WindowManager.getDatabase().insertUser(insertUser);

                printLine("New user created.");

                DataManager.users.add(insertUser);

                return true;
            } catch (DateTimeException e) {
                printLine("Please enter a valid date of birth in the format dd/mm/yyyy.");
            } catch (SQLException e) {
                printLine("An error occurred creating this user.");
                e.printStackTrace();
            }
        } else {
            printIncorrectUsageString("addUser", 2, "\"name part 1,name part 2\" <date of birth>");
        }
        return false;
    }


    private boolean addClinician(String[] nextCommand) {
        if (nextCommand.length == 4) {

            try {
                Clinician insertClinician = new Clinician(nextCommand[1], nextCommand[2],nextCommand[3].replace("\"", ""));
                WindowManager.getDatabase().insertClinician(insertClinician);
                DataManager.clinicians.add(insertClinician);
                printLine("New clinician created.");
            } catch (SQLException e) {
                printLine("An error occurred creating this clinician.");
                e.printStackTrace();
            }
            return true;

        } else {
            printIncorrectUsageString("addClinician", 3, "<username> <password> <name>");
        }
        return false;
    }

    /**
     * Adds an organ object to a users list of available organs.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean addDonationOrgan(String[] nextCommand) {
        User toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = SearchUtils.getUserById(Long.parseLong(nextCommand[1]));
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("addDonationOrgan", 2, "<id> <organ>");
            return false;
        }

        if (toSet == null) {
            printLine(String.format("User with ID %s not found.", nextCommand[1]));
            return false;
        }
        try {
            toSet.setOrgan(Organ.parse(nextCommand[2]));
            WindowManager.getDatabase().updateUserAttributesAndOrgans(toSet);
            refreshUser(toSet);
            return true;
        } catch (IllegalArgumentException e) {
            printLine("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds the given item to the users's transplant waiting list.
     * Returns true if the organ was added, otherwise returns false.
     * @param nextCommand The command entered by the user
     * @return True if the organ was added, otherwise returns false.
     */
    private boolean addWaitingListOrgan(String[] nextCommand) {
        User toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = SearchUtils.getUserById(Long.parseLong(nextCommand[1]));
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("addWaitingListOrgan", 2, "<id> <organ>");
            return false;
        }

        if (toSet == null) {
            printLine(String.format("User with ID %s not found.", nextCommand[1]));
            return false;
        }
        try {
            ReceiverWaitingListItem item = new ReceiverWaitingListItem(Organ.parse(nextCommand[2]), Long.parseLong(nextCommand[1]));
            toSet.getWaitingListItems().add(item);
            refreshUser(toSet);
            return true;
        } catch (IllegalArgumentException e) {
            printLine("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
            return false;
        }
    }

    /**
     * Finds out which user the user wants to delete, and ask for confirmation.
     *
     * @param nextCommand The command entered by the user
     */
    private void deleteUser(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                long id = Long.parseLong(nextCommand[1]);
                User user = SearchUtils.getUserById(id);
                if (user == null) {
                    printLine(String.format("User with ID %d not found.", id));
                } else {
                    print(String.format("Are you sure you want to delete %s, ID %d? (y/n) ", user.getName(), user.getId()));
                    deleteCommand = nextCommand;
                    isDeleting = true;
                    userToDelete = user;
                }
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
            }
        } else {
            printIncorrectUsageString("delete", 1, "<id>");
        }
    }

    /**
     * Finds out which user the user wants to delete, and ask for confirmation.
     *
     * @param nextCommand The command entered by the user
     */
    private void deleteClinician(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                long id = Long.parseLong(nextCommand[1]);
                if(id!=0) {
                    Clinician clinician = SearchUtils.getClinicianById(id);
                    if (clinician == null) {
                        printLine(String.format("Clinician with staff ID %d not found.", id));
                    }
                    print(String.format("Are you sure you want to delete %s, ID %d? (y/n) ", clinician.getName(), clinician.getStaffID()));
                    deleteCommand = nextCommand;
                    isDeleting = true;
                    clinicianToDelete = clinician;
                }else{
                    printLine("The default clinician cannot be deleted.");
                }
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
            }
        } else {
            printIncorrectUsageString("delete", 1, "<id>");
        }
    }

    /**
     * Delete the clinician if the user confirms the action.
     *
     * @param nextLine The command entered by the user
     * @return Whether the command was executed
     */
    private boolean checkBeforeDelete(String nextLine) {
        if (!nextLine.toLowerCase().equals("y") && !nextLine.toLowerCase().equals("n")) {
            print("Answer not recognised. Please enter y or n: ");
            return false;
        }
        if (nextLine.equals("y")) {
            boolean deleted;
            if(userToDelete == null){
                deleted = DataManager.clinicians.remove(clinicianToDelete);
                if (deleted) {
                    printLine("Clinician removed. This change will permanent once the user list is saved.");
                    isDeleting = false;
                    clinicianToDelete = null;
                    return true;
                } else {
                    printLine("The clinician has already been removed in the GUI.");
                }
            }else{
                deleted = DataManager.users.remove(userToDelete);
                if (deleted) {
                    //Close the window for the deleted user if it is open.
                    Stage toClose;
                    do {
                        toClose = null;
                        for (Stage stage : WindowManager.getCliniciansUserWindows().keySet()) {
                            if (WindowManager.getCliniciansUserWindows().get(stage).getCurrentUser().getId() == userToDelete.getId()) {
                                toClose = stage;
                                break;
                            }
                        }
                        if (toClose != null) {
                            WindowManager.getCliniciansUserWindows().remove(toClose);
                            toClose.close();
                        }
                    } while (toClose != null);
                    printLine("User removed. This change will permanent once the user list is saved.");
                    isDeleting = false;
                    userToDelete = null;
                    return true;
                } else {
                    printLine("The user has already been removed in the GUI.");
                }
            }

        } else {
            printLine("Deletion cancelled.");
        }
        isDeleting = false;
        userToDelete = null;
        clinicianToDelete = null;
        return false;
    }

    /**
     * Deletes an organ object from a users available organ set, if it exists.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean removeDonationOrgan(String[] nextCommand) {
        User toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = SearchUtils.getUserById(Long.parseLong(nextCommand[1]));
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("removeDonationOrgan", 2, "<id> <organ>");
            return false;
        }

        if (toSet == null) {
            printLine(String.format("User with ID %s not found.", nextCommand[1]));
            return false;
        }
        try {
            toSet.removeOrgan(Organ.parse(nextCommand[2]));
            refreshUser(toSet);
            return true;
        } catch (IllegalArgumentException e) {
            printLine("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, " +
                    "bone-marrow, connective-tissue");
            return false;
        }
    }

    /**
     * Removes the given item for the users's transplant waiting list.
     * Returns true if the removal was successful, otherwise returns false.
     * @param nextCommand The command entered by the user
     * @return True if removal was successful, otherwise false
     */
    private boolean removeWaitingListOrgan(String[] nextCommand) {
        User toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = SearchUtils.getUserById(Long.parseLong(nextCommand[1]));
            } catch (NumberFormatException e) {
                printLine("Please enter a valid ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("removeWaitingListOrgan", 2, "<id> <organ>");
            return false;
        }

        if (toSet == null) {
            printLine(String.format("User with ID %s not found.", nextCommand[1]));
            return false;
        }
        try {
            toSet.removeWaitingListItem(Organ.parse(nextCommand[2]));
            refreshUser(toSet);
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
    private boolean updateUser(String[] nextCommand) {
        long id;
        String attribute, value;
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
            printIncorrectUsageString("updateUser", 3, "<id> <attribute> <value>");
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
                refreshUser(toSet);
                printLine("New name set.");
                return true;
            case "prefname":
                toSet.setPreferredName(value);
                printLine("New preferred name set.");
                return true;

            case "dateofbirth":
                try {
                    toSet.setDateOfBirth(LocalDate.parse(value, User.dateFormat));
                    refreshUser(toSet);
                    printLine("New date of birth set.");
                    return true;
                } catch (DateTimeException e) {
                    printLine("Please enter a valid date in the format dd/mm/yyyy.");
                }
                return false;
            case "dateofdeath":
                try {
                    toSet.setDateOfDeath(LocalDate.parse(value, User.dateFormat));
                    refreshUser(toSet);
                    printLine("New date of death set.");
                    return true;
                } catch (DateTimeException e) {
                    printLine("Please enter a valid date in the format dd/mm/yyyy.");
                }
                return false;
            case "gender":
                try {
                    toSet.setGender(Gender.parse(value));
                    refreshUser(toSet);
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
                        refreshUser(toSet);
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
                        refreshUser(toSet);
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
                    refreshUser(toSet);
                    printLine("New blood type set.");
                    return true;
                } catch (IllegalArgumentException e) {
                    printLine("Please enter blood type as A-, A+, B-, B+, AB-, AB+, O-, or O+.");
                }
                return false;
            case "region":
                toSet.setRegion(value);
                refreshUser(toSet);
                printLine("New region set.");
                return true;
            case "currentaddress":
                toSet.setCurrentAddress(value);
                refreshUser(toSet);
                printLine("New address set.");
                return true;
            default:
                printLine("Attribute '" + attribute + "' not recognised. Try name, dateOfBirth, dateOfDeath, gender, height, weight, " +
                        "bloodType, region, or currentAddress.");
                return false;
        }
    }



    private boolean updateClinician(String[] nextCommand) {
        long id;
        String attribute, value;
        if (nextCommand.length == 4) {
            try {
                id = Long.parseLong(nextCommand[1]);
                attribute = nextCommand[2];
                value = nextCommand[3];
            } catch (NumberFormatException e) {
                printLine("Please enter a valid staff ID number.");
                return false;
            }
        } else {
            printIncorrectUsageString("updateClinician", 3, "<id> <attribute> <value>");
            return false;
        }

        Clinician toSet = SearchUtils.getClinicianById(id);
        if (toSet == null) {
            printLine(String.format("Clinician with staff ID %d not found.", id));
            return false;
        }
        switch (attribute.toLowerCase()) {
            case "name":
                toSet.setName(value);
                printLine("New name set.");
                return true;
            case "region":
                toSet.setRegion(value);
                printLine("New region set.");
                return true;
            case "workaddress":
                toSet.setWorkAddress(value);
                printLine("New work address set.");
                return true;
            case "username":
                toSet.setUsername(value);
                printLine("New username set.");
                return true;
            case "password":
                toSet.setPassword(value);
                printLine("New password set.");
                return true;
            default:
                printLine("Attribute '" + attribute + "' not recognised. Try name, region, workaddress, username, password.");
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
            printIncorrectUsageString("describeUser", 1, "<id>");
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
                for (User user : toDescribe) {
                    printLine(user.getSummaryString());
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
                for (User user : DataManager.users) {
                    printLine(user.getSummaryString());
                }
            } else {
                printLine("There are no users to list. Please add or import some before using listUsers.");
            }
            return true;
        } else {
            printIncorrectUsageString("listUsers", 0, null);
            return false;
        }
    }

    /**
     * Displays a table containing information about all clinicians.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private boolean listClinicians(String[] nextCommand) {
        if (nextCommand.length == 1) {
            if (DataManager.clinicians.size() > 0) {
                printLine(Clinician.tableHeader);
                for (Clinician clinician : DataManager.clinicians) {
                    printLine(clinician.getString(true));
                }
            } else {
                printLine("There are no clinicians to list. Please add or import some before using list.");
            }
            return true;
        } else {
            printIncorrectUsageString("listClinicians", 0, null);
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
                if (IO.importUsers(path, ProfileType.USER)) {
                    printLine("User imported from " + path + ".");
                    WindowManager.closeAllChildren();
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
    private boolean save(String[] nextCommand) {
        if (nextCommand.length >= 3) {
            boolean relative = nextCommand[1].equals("-r");
            if ((relative && nextCommand.length == 4) || (!relative && nextCommand.length == 3)) {
                String path;
                if (relative) {
                    path = IO.getJarPath() + File.separatorChar + nextCommand[3].replace('/', File.separatorChar);
                } else {
                    path = nextCommand[1];
                }
                String type = nextCommand[2];
                if(type.equals("users")){
                    if (IO.saveUsers(path, ProfileType.USER)) {
                        printLine("Users saved to " + path + ".");
                        return true;
                    } else {
                        printLine("Failed to save to " + path + ". Make sure the program has access to this file.");
                        return false;
                    }
                }else if(type.equals("clinicians")){
                    if (IO.saveUsers(path, ProfileType.CLINICIAN)) {
                        printLine("Clinicians saved to " + path + ".");
                        return true;
                    } else {
                        printLine("Failed to save to " + path + ". Make sure the program has access to this file.");
                        return false;
                    }
                }else{
                    printLine("Unknown login type " + type + ". Should be either 'users' or 'clinicians'");
                    return false;
                }

            }
        }
        printLine("The save command must be used with 2 or 3 arguments (save -r <type> <filepath> or save <type> <filepath>).");
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
                    + "\n\t-addClinician <username> <password> <name>"
                    + "\n\t-addUser <username> <password> \"First Name,name part 2,name part n\" <date of birth>"
                    + "\n\t-addDonationOrgan <id> <organ>"
                    + "\n\t-addWaitingListOrgan <id> <organ>"
                    + "\n\t-deleteClinician<id>"
                    + "\n\t-deleteUser <id>"
                    + "\n\t-describeClinician <id>"
                    + "\n\t-describeUser <id> OR describeUser \"name substring 1,name substring 2,name substring n\""
                    + "\n\t-describeOrgans <id>"
                    + "\n\t-listClinicians"
                    + "\n\t-listUsers"
                    + "\n\t-listOrgans"
                    + "\n\t-removeDonationOrgan <id> <organ>"
                    + "\n\t-removeWaitingListOrgan <id> <organ>"
                    + "\n\t-updateClinician <id> <attribute> <value>"
                    + "\n\t-updateUser <id> <attribute> <value>"
                    + "\n\t-import [-r] <filename>"
                    + "\n\t-save [-r] <path> OR save [-r] \"File path with spaces\""
                    + "\n\t-clear"
                    + "\n\t-help [<command>]"
                    + "\n\t-quit");
        } else if (nextCommand.length == 2) {
            switch (nextCommand[1].toLowerCase()) {
                case "adduser":
                    printLine("This command adds a new user with a name and date of birth.\n"
                            + "The syntax is: addUser <name> <date of birth>\n"
                            + "Rules:\n"
                            + "-The names must be comma separated without a space around the comma (eg. Andrew,Neil,Davidson)\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "-The date of birth must be entered in the format: dd/mm/yyyy\n"
                            + "Example valid usage: add \"Test,User with,SpacesIn Name\" 01/05/1994");
                    break;

                case "addclinician":
                    printLine("This command adds a new clinician with a username, password, and name.\n"
                            + "The syntax is: addClinician <username> <password> <name> \n"
                            + "Rules:\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "Example valid usage: addClinician <username> <password> \"clinician name\"");
                    break;
                case "adddonationorgan":
                    printLine("This command adds one organ to donate to a user. To find the id of a user, use the listUsers and "
                            + "describeUser commands.\n"
                            + "The syntax is: addDonationOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: addDonationOrgan 0 skin");
                    break;
                case "addwaitinglistorgan":
                    printLine("This command adds one organ which the user is waiting to receive. To find the id of a user, use the listUsers and describeUser commands. \n"
                            + "The syntax is: addWaitingListOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: addWaitingListOrgan 0 skin");
                    break;
                case "deleteuser":
                    printLine("This command deletes one user. To find the id of a user, use the list and describe commands.\n"
                            + "The syntax is: delete <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-You will be asked to confirm that you want to delete this user\n"
                            + "Example valid usage: delete 1");
                    break;

                case "deleteclinician":
                    printLine("This command deletes one clinician. To find the id of a clinician, use the listClinician and describeClinician commands.\n"
                            + "The syntax is: deleteClinician <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-You will be asked to confirm that you want to delete this clinician\n"
                            + "Example valid usage: deleteClinician 1");
                    break;
                case "removedonationorgan":
                    printLine("This command removes one offered organ from a user. To find the id of a user, use the listUsers and "
                            + "describeUser commands.\n"
                            + "The syntax is: removeDonationOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: removeDonationOrgan 5 kidney");
                    break;
                case "removewaitinglistorgan":
                    printLine("This command removes one organ which the user is waiting to receive. To find the id of a user, use the listUsers and "
                            + "describeUser commands.\n"
                            + "The syntax is: removeWaitingListOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: removeWaitingListOrgan 5 kidney");
                    break;
                case "updateclinician":
                    printLine("This command sets one attributeof a clincian. To find the id of a clincian, "
                            + "use the listClincian and describeClincian commands.\n"
                            + "The syntax is: updateClinician <id> <attribute> <value>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The attribute must be one of the following (case insensitive): name, workAddress, region, username, password\n"
                            + "-If a name or names are used, all users whose names contain the input names in order will be returned as matches\n"
                            + "Example valid usage: updateClincian 2 region Christchurch");
                    break;
                case "updateuser":
                    printLine("This command sets one attribute (apart from organs to be donated) of a user. To find the id of a user, "
                            + "use the listUsers and describeUser commands. To add or delete organs, instead use the addDonationOrgan and removeDonationOrgan commands.\n"
                            + "The syntax is: updateUser <id> <attribute> <value>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The attribute must be one of the following (case insensitive): name, prefname, dateOfBirth, dateOfDeath, gender, height, "
                            + "weight, bloodType, region, currentAddress\n"
                            + "-If a name or names are used, all users whose names contain the input names in order will be returned as matches\n"
                            + "-The gender must be: male, female, or other\n"
                            + "-The bloodType must be: A-, A+, B-, B+, AB-, AB+, O-, or O+\n"
                            + "-The height and weight must be numbers that are larger than 0\n"
                            + "-The date of birth and date of death values must be entered in the format: dd/mm/yyyy\n"
                            + "Example valid usage: updateUser 2 bloodtype ab+");
                    break;
                case "describeuser":
                    printLine("This command searches users and displays information about them. To find the id of a user, use the listUsers "
                            + "and describeUser commands.\n"
                            + "The syntax is: describeUser <id> OR describeUser <name>\n"
                            + "Rules:\n"
                            + "-If an id number is to be used as search criteria, it must be a number that is 0 or larger\n"
                            + "-If a name or names are used, the names must be comma separated without a space around the comma (eg. drew,david)\n"
                            + "-If a name or names are used, all users whose names contain the input names in order will be returned as matches\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "Example valid usage: describeUser \"andrew,son\'");
                    break;
                case "describeclinician":
                    printLine("This command searches clinicians and displays information about them. To find the id of a clinician, use the listClinicians "
                            + " command.\n"
                            + "The syntax is: describeClinician <id>\n"
                            + "Rules:\n"
                            + "-If an id number is to be used as search criteria, it must be a number that is 0 or larger\n"
                            + "Example valid usage: describeClinician 1");
                    break;
                case "describeorgans":
                    printLine("This command displays the organs which a user will donate or has donated. To find the id of a user, "
                            + "use the listUsers and describeUser commands.\n"
                            + "The syntax is: describeOrgans <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "Example valid usage: describeOrgans 4");
                    break;
                case "listusers":
                    printLine("This command lists all information about all users in a table.\n"
                            + "Example valid usage: listUsers");
                    break;
                case "listclinicians":
                    printLine("This command lists all information about all clinicians in a table.\n"
                            + "Example valid usage: listClinicians");
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
                            + "The syntax is: save [-r] <type> <filepath>\n"
                            + "Rules:\n"
                            + "-If the -r flag is present, the filepath will be interpreted as relative\n"
                            + "-If the filepath has spaces in it, it must be enclosed with quotation marks (\")\n"
                            + "-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows\n"
                            + "-The <type> argument denotes the type of user to save. This should be either 'users', for regular users, or 'clinicians'."
                            + "Example valid usage: save -r users\"new folder/users.json\"");
                    break;
                case "clear":
                    printLine("This command clears the command panel.\n"
                            + "The syntax is: clear\n"
                            + "Example valid usage: clear");
                    break;
                case "help":
                    printLine("This command displays information about how to use this program.\n"
                            + "The syntax is: help OR help <command>\n"
                            + "Rules:\n"
                            + "-If the command argument is passed, the command must be: add, addDonationOrgan, delete, removeDonationOrgan, updateUser, describeUser, "
                            + "describeOrgans, listUsers, listOrgans, import, save, help, or quit.\n"
                            + "Example valid usage: help help");
                    break;
                case "sql":
                    printLine("This command can be used to query sql select commands to the database.\n"
                            + "The syntax is: sql <SQL query>\n"
                            + "Rules:\n"
                            + "-Only SELECT queries can be sent"
                            + "Example valid usage: sql SELECT id from user where first_name = 'Steve' and last_name = 'Johnson'");
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