package seng302.Logic;

import seng302.Logic.Database.*;
import seng302.Model.*;
import seng302.Model.Attribute.BloodType;
import seng302.Model.Attribute.Gender;
import seng302.Model.Attribute.Organ;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class runs a command line interface (or text user interface), supplying the core functionality to a user through a terminal.
 */
public class CommandLineInterface {
    private boolean isDeleting = false;
    private User userToDelete;
    private Clinician clinicianToDelete;
    private String[] deleteCommand;
    private List<String> outputString;

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
     *
     * @param command The given command
     * @return returns the string response
     */
    public CommandLineResponse readCommand(String command) {
        // TODO make client ignore newline inputs
        CommandLineResponse response;
        String[] nextCommand;
        //TODO fix deleting
        if (isDeleting) {
            response = checkBeforeDelete(command);
            if (isDeleting) {
                return null;
            }
            nextCommand = deleteCommand;
        } else {
            nextCommand = splitByQuotationThenSpace(command);
            switch (nextCommand[0].toLowerCase()) {
                case "adduser":
                    response = addUser(nextCommand);
                    if (response.isSuccess()) {
                        try {
                            new UserHistory().insertHistoryItem(new HistoryItem(LocalDateTime.now(), "Created", "This profile was created"), Math.toIntExact(response.getUserId()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "addclinician":
                    response = addClinician(nextCommand);
                    break;

                case "adddonationorgan":
                    response = addDonationOrgan(nextCommand);
                    if (response.isSuccess()) {
                        try {
                            new UserHistory().insertHistoryItem(new HistoryItem(LocalDateTime.now(), "Updated Attribute", "A user attribute was updated."), Math.toIntExact(response.getUserId()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "addwaitinglistorgan":
                    response = addWaitingListOrgan(nextCommand);
                    if (response.isSuccess()) {
                        try {
                            new UserHistory().insertHistoryItem(new HistoryItem(LocalDateTime.now(), "Updated Attribute", "A user attribute was updated."), Math.toIntExact(response.getUserId()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "deleteuser":
                    response = deleteUser(nextCommand);
                    break;
                case "deleteclinician":
                    response = deleteClinician(nextCommand);
                    break;
                case "removewaitinglistorgan":
                    response = removeWaitingListOrgan(nextCommand);
                    if (response.isSuccess()) {
                        try {
                            new UserHistory().insertHistoryItem(new HistoryItem(LocalDateTime.now(), "Updated Attribute", "A user attribute was updated."), Math.toIntExact(response.getUserId()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "removedonationorgan":
                    response = removeDonationOrgan(nextCommand);
                    if (response.isSuccess()) {
                        try {
                            new UserHistory().insertHistoryItem(new HistoryItem(LocalDateTime.now(), "Updated Attribute", "A user attribute was updated."), Math.toIntExact(response.getUserId()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "updateuser":
                    response = updateUser(nextCommand);
                    if (response.isSuccess()) {
                        try {
                            new UserHistory().insertHistoryItem(new HistoryItem(LocalDateTime.now(), "Updated Attribute", "A user attribute was updated."), Math.toIntExact(response.getUserId()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "updateclinician":
                    response = updateClinician(nextCommand);
                    break;
                case "describeuser":
                    response = describeUser(nextCommand);
                    break;

                case "describeclinician":
                    response = describeClinician(nextCommand);
                    break;
                case "describeorgans":
                    response = listUserOrgans(nextCommand);
                    break;
                case "listusers":
                    response = listUsers(nextCommand);
                    break;

                case "listclinicians":
                    response = listClinicians(nextCommand);
                    break;
                case "listorgans":
                    response = listOrgans(nextCommand);
                    break;
                case "clear":
                    // TODO make sure that this works (history)
                    response = new CommandLineResponse(true, "CLEAR");
                    break;
                case "help":
                    response = showHelp(nextCommand);
                    break;
                case "sql":
                    response = queryDatabase(nextCommand);
                    break;
                default:
                    response = new CommandLineResponse(false, "Command not recognised. Enter 'help' to view available commands, or help <command> to view information " +
                            "about a specific command.");
            }
        }
        return response;
    }

    private CommandLineResponse describeClinician(String[] nextCommand) {
        String idString;

        if (nextCommand.length == 2) {
            idString = nextCommand[1];
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("describe", 1, "<id>"));
        }

        try {
            Clinician toDescribe = new GeneralClinician().getClinicianFromId(Integer.parseInt(idString));
            if (toDescribe == null) {
                return new CommandLineResponse(true, String.format("clinician with ID %s not found.", idString));
            } else {
                return new CommandLineResponse(true, toDescribe.getString(false));
            }
        } catch (NumberFormatException e) {
            return new CommandLineResponse(false, "ID entered was not valid.");
        } catch (SQLException e) {
            return new CommandLineResponse(false, "Could not describe clinician. An error occurred on the database.");
        }
    }


    /**
     * Returns a message advising the user on how to correctly use a command they failed to use.
     *
     * @param command The command name
     * @param argc    The argument count
     * @param args    The arguments
     */
    private String getIncorrectUsageString(String command, int argc, String args) {
        switch (argc) {
            case 0:
                return (String.format("The %s command does not accept arguments.", command));
            case 1:
                return (String.format("The %s command must be used with 1 argument (%s %s).", command, command, args));
            default:
                return (String.format("The %s command must be used with %d arguments (%s %s).", command, argc, command, args));
        }

    }

    private CommandLineResponse queryDatabase(String[] nextCommand) {
        SqlSanitation sqlSanitation = new SqlSanitation();
        String[] sqlArray = Arrays.copyOfRange(nextCommand, 1, nextCommand.length);
        String query = String.join(" ", sqlArray);
        String result = sqlSanitation.sanitizeSqlString(query);
        if (!result.equals("")) {
            return new CommandLineResponse(false, result);
        } else {
            return new CommandLineResponse(true, sqlSanitation.executeQuery(query).getResponse());
        }
    }


    /**
     * Creates a new user with a name and date of birth.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse addUser(String[] nextCommand) {
        if (nextCommand.length == 5) {
            try {
                LocalDate dob = LocalDate.parse(nextCommand[4], User.dateFormat);
                if(dob.isBefore(LocalDate.now())) {
                    User insertUser = new User(nextCommand[3].replace("\"", ""), dob);
                    insertUser.setUsername(nextCommand[1]);
                    insertUser.setPassword(nextCommand[2]);
                    new GeneralUser().insertUser(insertUser);
                    return new CommandLineResponse(true, "New user created.", new Authorization().loginUser(insertUser.getUsername(), insertUser.getPassword()).getId());
                } else {
                    return new CommandLineResponse(false, "Date of birth must not be in the future.");
                }
            } catch (DateTimeException e) {
                return new CommandLineResponse(false, "Please enter a valid date of birth in the format dd/mm/yyyy.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "An error occurred creating this user. This username may already be taken");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("addUser", 4, "<username> <password> \"name part 1,name part 2\" <date of birth>"));
        }
    }


    private CommandLineResponse addClinician(String[] nextCommand) {
        if (nextCommand.length == 4) {

            try {
                Clinician insertClinician = new Clinician(nextCommand[1], nextCommand[2], nextCommand[3].replace("\"", ""));
                new GeneralClinician().insertClinician(insertClinician);
                // TODO Ensure the client somehow gets the DB assigned ID of the new user if/when needed
                return new CommandLineResponse(true, "New clinician created.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "An error occurred creating this clinician. This username may already be taken");
            }


        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("addClinician", 3, "<username> <password> <name>"));
        }
    }

    /**
     * Adds an organ object to a users list of available organs.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse addDonationOrgan(String[] nextCommand) {
        User toSet = null;
        if (nextCommand.length == 3) {
            try {
                toSet = new GeneralUser().getUserFromId(Integer.parseInt(nextCommand[1]));
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid ID number.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Item could not be added. An error occurred on the database.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("addDonationOrgan", 2, "<id> <organ>"));
        }

        if (toSet == null) {
            return new CommandLineResponse(false, String.format("user with ID %s not found.", nextCommand[1]));
        }
        try {
            new UserDonations().insertDonation(Organ.parse(nextCommand[2]), (int) toSet.getId(), toSet.getDateOfDeath());
            return new CommandLineResponse(true, "Successful update of Organs", toSet.getId());
        } catch (IllegalArgumentException e) {
            return new CommandLineResponse(false, "Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
        } catch (SQLException e) {
            return new CommandLineResponse(false, "Donation could not be added. An error occurred on the database.");
        }
    }

    /**
     * Adds the given item to the users's transplant waiting list.
     * Returns true if the organ was added, otherwise returns false.
     *
     * @param nextCommand The command entered by the user
     * @return True if the organ was added, otherwise returns false.
     */
    private CommandLineResponse addWaitingListOrgan(String[] nextCommand) {
        User toSet = null;
        if (nextCommand.length == 3) {
            try {
                toSet = new GeneralUser().getUserFromId(Integer.parseInt(nextCommand[1]));
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid ID number.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Item could not be added. An error occurred on the database.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("addWaitingListOrgan", 2, "<id> <organ>"));
        }

        if (toSet == null) {
            return new CommandLineResponse(false, String.format("user with ID %s not found.", nextCommand[1]));
        }
        try {
            WaitingListItem item = new WaitingListItem(Organ.parse(nextCommand[2]), LocalDate.now(), -1, (int) toSet.getId(), null, 0);
            new UserWaitingList().insertWaitingListItem(item, (int) toSet.getId());
            return new CommandLineResponse(true, "Successful update of Waiting List Items", toSet.getId());
        } catch (IllegalArgumentException e) {
            return new CommandLineResponse(false, "Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
        } catch (SQLException e) {
            return new CommandLineResponse(false, "Item could not be added. An error occurred on the database.");
        }
    }

    /**
     * Finds out which user the user wants to delete, and ask for confirmation.
     *
     * @param nextCommand The command entered by the user
     */
    private CommandLineResponse deleteUser(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                int id = Integer.parseInt(nextCommand[1]);
                User user = new GeneralUser().getUserFromId((int) id);
                if (user == null) {
                    return new CommandLineResponse(false, String.format("user with ID %d not found.", id));
                } else {
                    deleteCommand = nextCommand;
                    isDeleting = true;
                    userToDelete = user;
                    return new CommandLineResponse(false, String.format("Are you sure you want to delete %s, ID %d? (y/n) ", user.getName(), user.getId()));
                }
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid ID number.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Could not delete user. An error occurred on the database.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("delete", 1, "<id>"));
        }
    }

    /**
     * Finds out which user the user wants to delete, and ask for confirmation.
     *
     * @param nextCommand The command entered by the user
     */
    private CommandLineResponse deleteClinician(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                int id = Integer.parseInt(nextCommand[1]);
                if (id != 0) {
                    Clinician clinician = new GeneralClinician().getClinicianFromId(id);
                    if (clinician == null) {
                        return new CommandLineResponse(false, String.format("clinician with staff ID %d not found.", id));
                    } else {
                        deleteCommand = nextCommand;
                        isDeleting = true;
                        clinicianToDelete = clinician;
                        return new CommandLineResponse(false, String.format("Are you sure you want to delete %s, ID %d? (y/n) ", clinician.getName(), clinician.getStaffID()));
                    }
                } else {
                    return new CommandLineResponse(true, "The default clinician cannot be deleted.");
                }
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid ID number.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Could not delete user. An error occurred on the server.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("delete", 1, "<id>"));
        }
    }

    /**
     * Delete the clinician if the user confirms the action.
     *
     * @param nextLine The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse checkBeforeDelete(String nextLine) {
        if (!nextLine.toLowerCase().equals("y") && !nextLine.toLowerCase().equals("n")) {
            return new CommandLineResponse(false, "Answer not recognised. Please enter y or n: ");
        }
        if (nextLine.equals("y")) {
            if (userToDelete == null) {
                try {
                    new GeneralClinician().removeClinician(clinicianToDelete);
                    isDeleting = false;
                    clinicianToDelete = null;
                    return new CommandLineResponse(true, "clinician removed");
                } catch (SQLException e) {
                    return new CommandLineResponse(false, "Could not remove clinician. An error occurred on the database.");
                }
            } else {
                try {
                    new GeneralUser().removeUser(userToDelete);
                    isDeleting = false;
                    userToDelete = null;
                    return new CommandLineResponse(true, "user removed");
                } catch (SQLException e) {
                    return new CommandLineResponse(false, "Could not remove user. An error occurred on the database.");
                }
            }
        } else {
            isDeleting = false;
            userToDelete = null;
            clinicianToDelete = null;
            return new CommandLineResponse(false, "Deletion cancelled.");
        }
    }

    /**
     * Deletes an organ object from a users available organ set, if it exists.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse removeDonationOrgan(String[] nextCommand) {
        User toSet = null;
        if (nextCommand.length == 3) {
            try {
                toSet = new GeneralUser().getUserFromId(Integer.parseInt(nextCommand[1]));
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid ID number.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Could not remove donation item. An error occurred on the server.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("removeDonationOrgan", 2, "<id> <organ>"));
        }

        if (toSet == null) {
            return new CommandLineResponse(false, String.format("user with ID %s not found.", nextCommand[1]));
        }
        try {
            new UserDonations().removeDonationListItem((int) toSet.getId(), nextCommand[2]);
            return new CommandLineResponse(true, "Item removed successfully.", toSet.getId());
        } catch (IllegalArgumentException e) {
            return new CommandLineResponse(false, "Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, " +
                    "bone-marrow, connective-tissue");
        } catch (SQLException e) {
            return new CommandLineResponse(false, "Could not remove donation item. An error occurred on the server.");
        }
    }

    /**
     * Removes the given item for the users's transplant waiting list.
     * Returns true if the removal was successful, otherwise returns false.
     *
     * @param nextCommand The command entered by the user
     * @return True if removal was successful, otherwise false
     */
    private CommandLineResponse removeWaitingListOrgan(String[] nextCommand) {
        User toSet;
        if (nextCommand.length == 3) {
            try {
                toSet = new GeneralUser().getUserFromId(Integer.parseInt(nextCommand[1]));
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid ID number.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Item could not be removed. An error occurred on the database.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("removeWaitingListOrgan", 2, "<id> <organ>"));
        }

        if (toSet == null) {
            return new CommandLineResponse(false, String.format("user with ID %s not found.", nextCommand[1]));
        }
        try {
            new UserWaitingList().removeWaitingListItem((int) toSet.getId(), Organ.parse(nextCommand[2]));
            return new CommandLineResponse(true, "Waiting list item was de-registered.", toSet.getId());
        } catch (IllegalArgumentException e) {
            return new CommandLineResponse(false, "Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, " +
                    "bone-marrow, connective-tissue");
        } catch (SQLException e) {
            return new CommandLineResponse(false, "Item could not be removed. An error occurred on the database.");
        }
    }

    /**
     * Sets a new value for one attribute of a user.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse updateUser(String[] nextCommand) {
        long id;
        String outputString;
        String attribute, value;
        if (nextCommand.length == 4) {
            try {
                id = Long.parseLong(nextCommand[1]);
                attribute = nextCommand[2];
                value = nextCommand[3];
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid ID number.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("updateUser", 3, "<id> <attribute> <value>"));
        }

        boolean wasSuccessful = false;
        User toSet = null;
        try {
            toSet = new GeneralUser().getUserFromId((int) id);
            if (toSet == null) {
                return new CommandLineResponse(false, String.format("user with ID %d not found.", id));
            }
            switch (attribute.toLowerCase()) {
                case "name":
                    toSet.setName(value);

                    outputString = ("New name set.");
                    wasSuccessful = true;
                    break;
                case "prefname":
                    toSet.setPreferredName(value);
                    outputString = ("New preferred name set.");
                    wasSuccessful = true;
                    break;
                case "dateofbirth":
                    try {
                        LocalDate dob = LocalDate.parse(value, User.dateFormat);
                        if(dob.isBefore(LocalDate.now())) {
                            if(toSet.getDateOfDeath() == null || dob.isBefore(toSet.getDateOfDeath().toLocalDate())) {
                                toSet.setDateOfBirth(dob);
                                outputString = ("New date of birth set.");
                                wasSuccessful = true;
                            } else {
                                outputString = (String.format("Date of birth cannot after the date and time of death (" + User.dateTimeFormat.format(toSet.getDateOfDeath()) + ")"));
                                wasSuccessful = false;
                            }
                        } else {
                            outputString = ("Date of birth cannot be in the future.");
                            wasSuccessful = false;
                        }
                    } catch (DateTimeException e) {
                        outputString = ("Please enter a valid date in the format dd/mm/yyyy.");
                        wasSuccessful = false;
                    }
                    break;
                case "datetimeofdeath":
                    try {
                        LocalDateTime dod = LocalDateTime.parse(value, User.dateTimeFormat);
                        if(dod.isBefore(LocalDateTime.now())) {
                            if(toSet.getDateOfBirth() == null || dod.isAfter(toSet.getDateOfBirth().atStartOfDay())) {
                                toSet.setDateOfDeath(dod);
                                outputString = ("New date and time of death set.");
                                wasSuccessful = true;
                            } else {
                                outputString = ("Date and time of death cannot be before the date of birth (" + User.dateFormat.format(toSet.getDateOfBirth()) + ")");
                                wasSuccessful = false;
                            }
                        } else {
                            outputString = ("Date and time of death cannot be in the future.");
                            wasSuccessful = false;

                        }
                    } catch (DateTimeException e) {
                        outputString = ("Please enter a valid date and time in the format: dd/MM/yyyy, HH:mm:ss");
                        wasSuccessful = false;
                    }
                    break;
                case "gender":
                    try {
                        toSet.setGender(Gender.parse(value));

                        outputString = ("New gender set.");
                        wasSuccessful = true;
                    } catch (IllegalArgumentException e) {
                        outputString = ("Please enter gender as other, female, or male.");
                        wasSuccessful = false;
                    }

                    break;
                case "height":
                    try {
                        double height = Double.parseDouble(value);
                        if (height <= 0) {
                            outputString = ("Please enter a height which is larger than 0.");
                            wasSuccessful = false;
                        } else {
                            toSet.setHeight(height);

                            outputString = ("New height set.");
                            wasSuccessful = true;
                        }
                    } catch (NumberFormatException e) {
                        outputString = ("Please enter a numeric height.");
                        wasSuccessful = false;
                    }
                    break;
                case "weight":
                    try {
                        double weight = Double.parseDouble(value);
                        if (weight <= 0) {
                            outputString = ("Please enter a weight which is larger than 0.");
                            wasSuccessful = false;
                        } else {
                            toSet.setWeight(weight);

                            outputString = ("New weight set.");
                            wasSuccessful = true;
                        }
                    } catch (NumberFormatException e) {
                        outputString = ("Please enter a numeric weight.");
                        wasSuccessful = false;
                    }

                    break;
                case "bloodtype":
                    try {
                        toSet.setBloodType(BloodType.parse(value));

                        outputString = ("New blood type set.");
                        wasSuccessful = true;
                    } catch (IllegalArgumentException e) {
                        outputString = ("Please enter blood type as A-, A+, B-, B+, AB-, AB+, O-, or O+.");
                        wasSuccessful = false;
                    }

                    break;
                case "region":
                    toSet.setRegion(value);

                    outputString = ("New region set.");
                    wasSuccessful = true;
                    break;
                case "currentaddress":
                    toSet.setCurrentAddress(value);

                    outputString = ("New address set.");
                    wasSuccessful = true;
                    break;
                case "country":
                    for (Country c : new GeneralCountries().getCountries()) {
                        if (value.toLowerCase().equals(c.getCountryName().toLowerCase()) && c.getValid()) {
                            value = c.getCountryName();
                            toSet.setCountry(value);
                            wasSuccessful = true;
                            break;
                        }
                    }
                    if (!wasSuccessful) {
                        outputString = ("Country was not updated. Invalid country: " + value);
                    } else {
                        outputString = ("New country set.");
                    }
                    break;

                default:
                    outputString = ("Attribute '" + attribute + "' not recognised. Try name, dateOfBirth, dateTimeOfDeath, gender, height, weight, " +
                            "bloodType, region, or currentAddress.");
                    wasSuccessful = false;
                    break;
            }
            new GeneralUser().updateUserAttributes(toSet, (int) toSet.getId());
        } catch (SQLException e) {
            return new CommandLineResponse(false, "user could not be updated. An error occurred on the server.");
        }


        return new CommandLineResponse(wasSuccessful, outputString, toSet.getId());
    }


    private CommandLineResponse updateClinician(String[] nextCommand) {
        long id;
        String outputString;
        String attribute, value;
        if (nextCommand.length == 4) {
            try {
                id = Long.parseLong(nextCommand[1]);
                attribute = nextCommand[2];
                value = nextCommand[3];
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid staff ID number.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("updateClinician", 3, "<id> <attribute> <value>"));
        }

        boolean wasSuccessful = false;
        Clinician toSet = null;
        try {
            toSet = new GeneralClinician().getClinicianFromId((int) id);
            if (toSet == null) {
                return new CommandLineResponse(false, String.format("clinician with staff ID %d not found.", id));
            }

            switch (attribute.toLowerCase()) {
                case "name":
                    toSet.setName(value);
                    outputString = ("New name set.");
                    wasSuccessful = true;
                    break;
                case "region":
                    toSet.setRegion(value);
                    outputString = ("New region set.");
                    wasSuccessful = true;
                    break;
                case "workaddress":
                    toSet.setWorkAddress(value);
                    outputString = ("New work address set.");
                    wasSuccessful = true;
                    break;
                case "username":
                    toSet.setUsername(value);
                    outputString = ("New username set.");
                    wasSuccessful = true;
                    break;
                case "password":
                    toSet.setPassword(value);
                    outputString = ("New password set.");
                    wasSuccessful = true;
                    break;
                default:
                    outputString = ("Attribute '" + attribute + "' not recognised. Try name, region, workaddress, username, password.");
                    wasSuccessful = false;
                    break;
            }
            new GeneralClinician().updateClinicianDetails(toSet, (int)toSet.getStaffID());
        } catch (SQLException e) {
            return new CommandLineResponse(false, "clinician could not be updated. An error occurred on the server.");

        }

        return new CommandLineResponse(wasSuccessful, outputString);
    }

    /**
     * Searches for users and displays information about them.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse describeUser(String[] nextCommand) {
        String idString;
        /*if (nextCommand.length > 1 && nextCommand[1].contains("\"")) {
            idString = String.join(" ", nextCommand).split("\"")[1];
        } else */
        if (nextCommand.length == 2) {
            idString = nextCommand[1];
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("describeUser", 1, "<id>"));
        }

        try {
            User toDescribe = new GeneralUser().getUserFromId((int) Long.parseLong(idString));
            if (toDescribe == null) {
                return new CommandLineResponse(false, String.format("user with ID %s not found.", idString));
            } else {
                return new CommandLineResponse(true, toDescribe.toString());
            }
        } catch (NumberFormatException e) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("name", idString);

            List<User> toDescribe = null;
            try {
                toDescribe = new GeneralUser().getUsers(params);
            } catch (SQLException e1) {
                return new CommandLineResponse(false, "Could not return user info. An error occurred on the server.");
            }

            if (toDescribe.size() == 0) {
                return new CommandLineResponse(true, "No user fits given information");
            } else {
                String outputString = "";
                for (User user : toDescribe) {
                    outputString = outputString + (user.getSummaryString() + "\n");
                }
                return new CommandLineResponse(true, outputString);
            }
        } catch (SQLException e) {
            return new CommandLineResponse(false, "Could not return user info. An error occurred on the server.");
        }
    }

    /**
     * Lists a specific user and their available organs. If they have none a message is displayed.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse listUserOrgans(String[] nextCommand) {
        if (nextCommand.length == 2) {
            try {
                User user = new GeneralUser().getUserFromId(Integer.parseInt(nextCommand[1]));
                if (user == null) {
                    return new CommandLineResponse(false, String.format("user with ID %s not found.", nextCommand[1]));
                } else if (!user.getOrgans().isEmpty()) {
                    return new CommandLineResponse(true, user.getName() + ": " + user.getOrgans());
                } else {
                    return new CommandLineResponse(true, "No organs available from user!");
                }
            } catch (NumberFormatException e) {
                return new CommandLineResponse(false, "Please enter a valid ID number.");
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Could not list user organs. An error occurred on the server.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("describeOrgans", 1, "<id>"));
        }
    }

    /**
     * Displays a table containing information about all users.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse listUsers(String[] nextCommand) {
        String outputString = "";
        if (nextCommand.length == 1) {
            try {
                if (new GeneralUser().getUsers(new HashMap<String, String>()).size() > 0) {
                    for (User user : new GeneralUser().getUsers(new HashMap<String, String>())) {
                        outputString = outputString + (user.getSummaryString() + "\n");
                    }
                    return new CommandLineResponse(true, outputString);
                } else {
                    return new CommandLineResponse(true, "There are no users to list. Please add some before using listUsers.");
                }
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Could not list all users. An error occurred on the server.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("listUsers", 0, null));
        }
    }

    /**
     * Displays a table containing information about all clinicians.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse listClinicians(String[] nextCommand) {
        String outputString = "";
        if (nextCommand.length == 1) {
            try {
                if (new GeneralClinician().getAllClinicians().size() > 0) {
                    outputString = (Clinician.tableHeader);
                    for (Clinician clinician : new GeneralClinician().getAllClinicians()) {
                        outputString = outputString + (clinician.getString(false)) + "\n";
                    }
                    return new CommandLineResponse(true, outputString);
                } else {
                    return new CommandLineResponse(true, "There are no clinicians to list. Please add some before using list.");
                }
            } catch (SQLException e) {
                return new CommandLineResponse(false, "Could not list all clinicians. An error occurred on the server.");
            }
        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("listClinicians", 0, null));
        }
    }


    /**
     * Lists all users who have at least 1 organ to donate and their available organs.
     * If none exist, a message is displayed.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse listOrgans(String[] nextCommand) {
        String outputString = "";
        if (nextCommand.length == 1) {
            boolean organsAvailable = false;
            try {
                for (User user : new GeneralUser().getUsers(new HashMap<String, String>())) {
                    if (!user.getOrgans().isEmpty()) {
                        outputString = outputString + (user.getName() + ": " + user.getOrgans()) + "\n";
                        organsAvailable = true;
                    }
                }
                if (organsAvailable) {
                    return new CommandLineResponse(true, outputString);
                }
                if (!organsAvailable) {
                    return new CommandLineResponse(true, "No organs available from any user!");
                }

            } catch (SQLException e) {
                return new CommandLineResponse(false, "Could not list all organs. An error occurred on the server.");
            }

        } else {
            return new CommandLineResponse(false, getIncorrectUsageString("listOrgans", 0, null));

        }
        return null;
    }


    /**
     * Shows help either about which commands are available or about a specific command's usage.
     *
     * @param nextCommand The command entered by the user
     * @return Whether the command was executed
     */
    private CommandLineResponse showHelp(String[] nextCommand) {
        if (nextCommand.length == 1) {
            return new CommandLineResponse(false, "Valid commands are: "
                    + "\n\t-addClinician <username> <password> <name>"
                    + "\n\t-addUser <username> <password> \"First Name,name part 2,name part n\" <date of birth>"
                    + "\n\t-addDonationOrgan <id> <organ>"
                    + "\n\t-addWaitingListOrgan <id> <organ>"
                    + "\n\t-deleteClinician <id>"
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
                    + "\n\t-clear"
                    + "\n\t-help [<command>]"
                    + "\n\t-sql <command>");
        } else if (nextCommand.length == 2) {
            switch (nextCommand[1].toLowerCase()) {
                case "adduser":
                    return new CommandLineResponse(false, "This command adds a new user with a username, password, name and date of birth.\n"
                            + "The syntax is: addUser <name> <date of birth>\n"
                            + "Rules:\n"
                            + "-The names must be comma separated without a space around the comma (eg. Andrew,Neil,Davidson)\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "-The date of birth must be entered in the format: dd/mm/yyyy\n"
                            + "Example valid usage: add my_username my_password \"Test,user with,SpacesIn Name\" 01/05/1994");
                case "addclinician":
                    return new CommandLineResponse(false, "This command adds a new clinician with a username, password, and name.\n"
                            + "The syntax is: addClinician <username> <password> <name> \n"
                            + "Rules:\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "Example valid usage: addClinician <username> <password> \"clinician name\"");
                case "adddonationorgan":
                    return new CommandLineResponse(false, "This command adds one organ to donate to a user. To find the id of a user, use the listUsers and "
                            + "describeUser commands.\n"
                            + "The syntax is: addDonationOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: addDonationOrgan 0 skin");
                case "addwaitinglistorgan":
                    return new CommandLineResponse(false, "This command adds one organ which the user is waiting to receive. To find the id of a user, use the listUsers and describeUser commands. \n"
                            + "The syntax is: addWaitingListOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: addWaitingListOrgan 0 skin");
                case "deleteuser":
                    return new CommandLineResponse(false, "This command deletes one user. To find the id of a user, use the list and describe commands.\n"
                            + "The syntax is: delete <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-You will be asked to confirm that you want to delete this user\n"
                            + "Example valid usage: delete 1");
                case "deleteclinician":
                    return new CommandLineResponse(false, "This command deletes one clinician. To find the id of a clinician, use the listClinician and describeClinician commands.\n"
                            + "The syntax is: deleteClinician <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-You will be asked to confirm that you want to delete this clinician\n"
                            + "Example valid usage: deleteClinician 1");
                case "removedonationorgan":
                    return new CommandLineResponse(false, "This command removes one offered organ from a user. To find the id of a user, use the listUsers and "
                            + "describeUser commands.\n"
                            + "The syntax is: removeDonationOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: removeDonationOrgan 5 kidney");
                case "removewaitinglistorgan":
                    return new CommandLineResponse(false, "This command removes one organ which the user is waiting to receive. To find the id of a user, use the listUsers and "
                            + "describeUser commands.\n"
                            + "The syntax is: removeWaitingListOrgan <id> <organ>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                            + "bone-marrow, or connective-tissue.\n"
                            + "Example valid usage: removeWaitingListOrgan 5 kidney");
                case "updateclinician":
                    return new CommandLineResponse(false, "This command sets one attributeof a clincian. To find the id of a clincian, "
                            + "use the listClincian and describeClincian commands.\n"
                            + "The syntax is: updateClinician <id> <attribute> <value>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The attribute must be one of the following (case insensitive): name, workAddress, region, username, password\n"
                            + "-If a name or names are used, all users whose names contain the input names in order will be returned as matches\n"
                            + "Example valid usage: updateClincian 2 region Christchurch");
                case "updateuser":
                    return new CommandLineResponse(false, "This command sets one attribute (apart from organs to be donated) of a user. To find the id of a user, "
                            + "use the listUsers and describeUser commands. To add or delete organs, instead use the addDonationOrgan and removeDonationOrgan commands.\n"
                            + "The syntax is: updateUser <id> <attribute> <value>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "-The attribute must be one of the following (case insensitive): name, prefname, dateOfBirth, dateTimeOfDeath, gender, height, "
                            + "weight, bloodType, region, currentAddress\n"
                            + "-If a name or names are used, all users whose names contain the input names in order will be returned as matches\n"
                            + "-The gender must be: male, female, or other\n"
                            + "-The bloodType must be: A-, A+, B-, B+, AB-, AB+, O-, or O+\n"
                            + "-The height and weight must be numbers that are larger than 0\n"
                            + "-The date of birth values must be entered in the format: dd/mm/yyyy\n"
                            + "-The date/time of death values must be entered in the format: \"dd/mm/yyyy, HH:mm:ss\"\n"
                            + "Example valid usage: updateUser 2 bloodtype ab+");
                case "describeuser":
                    return new CommandLineResponse(false, "This command searches users and displays information about them. To find the id of a user, use the listUsers "
                            + "and describeUser commands.\n"
                            + "The syntax is: describeUser <id> OR describeUser <name>\n"
                            + "Rules:\n"
                            + "-If an id number is to be used as search criteria, it must be a number that is 0 or larger\n"
                            + "-If a name or names are used, the names must be comma separated without a space around the comma (eg. drew,david)\n"
                            + "-If a name or names are used, all users whose names contain the input names in order will be returned as matches\n"
                            + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                            + "Example valid usage: describeUser \"andrew,son\'");
                case "describeclinician":
                    return new CommandLineResponse(false, "This command searches clinicians and displays information about them. To find the id of a clinician, use the listClinicians "
                            + " command.\n"
                            + "The syntax is: describeClinician <id>\n"
                            + "Rules:\n"
                            + "-If an id number is to be used as search criteria, it must be a number that is 0 or larger\n"
                            + "Example valid usage: describeClinician 1");
                case "describeorgans":
                    return new CommandLineResponse(false, "This command displays the organs which a user will donate or has donated. To find the id of a user, "
                            + "use the listUsers and describeUser commands.\n"
                            + "The syntax is: describeOrgans <id>\n"
                            + "Rules:\n"
                            + "-The id number must be a number that is 0 or larger\n"
                            + "Example valid usage: describeOrgans 4");
                case "listusers":
                    return new CommandLineResponse(false, "This command lists all information about all users in a table.\n"
                            + "Example valid usage: listUsers");
                case "listclinicians":
                    return new CommandLineResponse(false, "This command lists all information about all clinicians in a table.\n"
                            + "Example valid usage: listClinicians");
                case "listorgans":
                    return new CommandLineResponse(false, "This command displays all of the organs that are currently offered by each user. user that are "
                            + "not yet offering any organs are not shown.\n"
                            + "Example valid usage: listOrgans");
                case "save":
                    return new CommandLineResponse(false, "This command saves the current user database to a file in JSON format.\n"
                            + "The syntax is: save [-r] <type> <filepath>\n"
                            + "Rules:\n"
                            + "-If the -r flag is present, the filepath will be interpreted as relative\n"
                            + "-If the filepath has spaces in it, it must be enclosed with quotation marks (\")\n"
                            + "-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows\n"
                            + "-The <type> argument denotes the type of user to save. This should be either 'users', for regular users, or 'clinicians'."
                            + "Example valid usage: save -r users\"new folder/users.json\"");
                case "clear":
                    return new CommandLineResponse(false, "This command clears the command panel.\n"
                            + "The syntax is: clear\n"
                            + "Example valid usage: clear");
                case "help":
                    return new CommandLineResponse(false, "This command displays information about how to use this program.\n"
                            + "The syntax is: help OR help <command>\n"
                            + "Rules:\n"
                            + "-If the command argument is passed, the command must be: add, addDonationOrgan, delete, removeDonationOrgan, updateUser, describeUser, "
                            + "describeOrgans, listUsers, listOrgans, save, help, or quit.\n"
                            + "Example valid usage: help help");
                case "sql":
                    return new CommandLineResponse(false, "This command can be used to query sql select commands to the database.\n"
                            + "The syntax is: sql <SQL query>\n"
                            + "Rules:\n"
                            + "-Only SELECT queries can be sent"
                            + "Example valid usage: sql SELECT id from user where first_name = 'Steve' and last_name = 'Johnson'");
                default:
                    return new CommandLineResponse(false, "Can not offer help with this command as it is not a valid command.");
            }
        } else {
            return new CommandLineResponse(false, "The help command must be used with 0 or 1 arguments (help or help <command>).");
        }
    }
}