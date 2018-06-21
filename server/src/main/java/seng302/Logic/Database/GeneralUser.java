package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.*;
import seng302.Model.Attribute.*;
import seng302.Model.Medication.Medication;
import seng302.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class GeneralUser {

    private Connection connection;
    private String currentDatabase;

    public GeneralUser() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        connection = databaseConfiguration.getConnection();
        currentDatabase = databaseConfiguration.getCurrentDatabase();
    }

    public ArrayList<User> getAllUsers() throws SQLException{
        ArrayList<User> allUsers = new ArrayList<>();
        String query = "SELECT * FROM " + currentDatabase + ".USER";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            allUsers.add(getUserFromResultSet(resultSet));
        }

        return allUsers;
    }

    public User getUserFromId(int id) throws SQLException {
        // SELECT * FROM USER id = id;
        String query = "SELECT * FROM " + currentDatabase + ".USER WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new User Object with the fields from the database
            return getUserFromResultSet(resultSet);
        }
    }

    public void insertUser(User user) throws SQLException{

        String insert = "INSERT INTO " + currentDatabase + ".USER(first_name, middle_names, last_name, preferred_name, preferred_middle_names, preferred_last_name, creation_time, last_modified, username," +
                " email, password, date_of_birth) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, user.getNameArray()[0]);
        statement.setString(2, user.getNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null);
        statement.setString(3, user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null);
        statement.setString(4, user.getPreferredNameArray()[0]);
        statement.setString(5, user.getPreferredNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getPreferredNameArray(), 1, user.getPreferredNameArray().length - 1)) : null);
        statement.setString(6, user.getPreferredNameArray().length > 1 ? user.getPreferredNameArray()[user.getPreferredNameArray().length - 1] : null);
        statement.setTimestamp(7, java.sql.Timestamp.valueOf(user.getCreationTime()));
        statement.setTimestamp(8, java.sql.Timestamp.valueOf(user.getCreationTime()));
        statement.setString(9, user.getUsername());
        statement.setString(10, user.getEmail());
        statement.setString(11, user.getPassword());
        statement.setDate(12, java.sql.Date.valueOf(user.getDateOfBirth()));
        System.out.println("Inserting new user -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public int getIdFromUser(String username) throws SQLException{

        String query = "SELECT id FROM " + currentDatabase + ".USER WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");

    }

    public User getUserFromResultSet(ResultSet resultSet) throws SQLException{


        User user = new User(
                resultSet.getString("first_name"),
                resultSet.getString("middle_names") != null ? resultSet.getString("middle_names").split(",") : null,
                resultSet.getString("last_name"),
                resultSet.getDate("date_of_birth").toLocalDate(),
                resultSet.getDate("date_of_death") != null ? resultSet.getDate("date_of_death").toLocalDate() : null,
                resultSet.getString("gender") != null ? Gender.parse(resultSet.getString("gender")) : null,
                resultSet.getDouble("height"),
                resultSet.getDouble("weight"),
                resultSet.getString("blood_type") != null ? BloodType.parse(resultSet.getString("blood_type")) : null,
                resultSet.getString("region"),
                resultSet.getString("current_address"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password"));
        user.setLastModifiedForDatabase(resultSet.getTimestamp("last_modified").toLocalDateTime());
        user.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());


        String preferredNameString = "";
        preferredNameString += resultSet.getString("preferred_name") + " ";
        if (resultSet.getString("preferred_middle_names") != null) {
            for (String middleName : resultSet.getString("preferred_middle_names").split(",")) {
                preferredNameString += middleName + " ";
            }
        }
        preferredNameString += resultSet.getString("preferred_last_name");

        user.setPreferredNameArray(preferredNameString.split(" "));


        user.setGenderIdentity(resultSet.getString("gender_identity") != null ? Gender.parse(resultSet.getString("gender_identity")) : null);

        if (resultSet.getString("blood_pressure") != null) {
            user.setBloodPressure(resultSet.getString("blood_pressure"));
        } else {
            user.setBloodPressure("");
        }

        if (resultSet.getString("smoker_status") != null) {
            user.setSmokerStatus(SmokerStatus.parse(resultSet.getString("smoker_status")));
        } else {
            user.setSmokerStatus(null);
        }

        if (resultSet.getString("alcohol_consumption") != null) {
            user.setAlcoholConsumption(AlcoholConsumption.parse(resultSet.getString("alcohol_consumption")));
        } else {
            user.setAlcoholConsumption(null);
        }

        //Get all the organs for the given user

        int userId = getIdFromUser(resultSet.getString("username"));
        user.setId(userId);

        String organsQuery = "SELECT * FROM " + currentDatabase + ".DONATION_LIST_ITEM WHERE user_id = ?";
        PreparedStatement organsStatement = connection.prepareStatement(organsQuery);

        organsStatement.setInt(1, userId);
        ResultSet organsResultSet = organsStatement.executeQuery();

        while (organsResultSet.next()) {
            user.getOrgans().add(Organ.parse(organsResultSet.getString("name")));
        }

        //Get all the medications for the given user

        String medicationsQuery = "SELECT * FROM " + currentDatabase + ".MEDICATION WHERE user_id = ?";
        PreparedStatement medicationsStatement = connection.prepareStatement(medicationsQuery);

        medicationsStatement.setInt(1, userId);
        ResultSet medicationsResultSet = medicationsStatement.executeQuery();

        while (medicationsResultSet.next()) {
            String[] activeIngredients = medicationsResultSet.getString("active_ingredients").split(",");
            String[] historyStringList = medicationsResultSet.getString("history").split(",");

            ArrayList<String> historyList = new ArrayList<>();
            //Iterate through the history list to get associated values together
            int counter = 1;
            String historyItem = "";
            for (int i = 0; i < historyStringList.length; i++) {
                if (counter % 2 == 1) {
                    historyItem += historyStringList[i] + ",";
                    counter++;
                } else {
                    historyItem += historyStringList[i];
                    historyList.add(historyItem);
                    historyItem = "";
                    counter++;
                }

            }


            if (historyList.get(historyList.size() - 1).contains("Stopped taking")) { //Medication is historic
                user.getHistoricMedications().add(new Medication(
                        medicationsResultSet.getString("name"),
                        activeIngredients,
                        historyList,
                        medicationsResultSet.getInt("id")
                ));
            } else { //Medication is Current
                user.getCurrentMedications().add(new Medication(
                        medicationsResultSet.getString("name"),
                        activeIngredients,
                        historyList,
                        medicationsResultSet.getInt("id")
                ));
            }
        }


        //Get all the procedures for the given user

        String proceduresQuery = "SELECT * FROM " + currentDatabase + ".PROCEDURES WHERE user_id = ?";
        PreparedStatement proceduresStatement = connection.prepareStatement(proceduresQuery);

        proceduresStatement.setInt(1, userId);
        ResultSet proceduresResultSet = proceduresStatement.executeQuery();

        while (proceduresResultSet.next()) {

            if (proceduresResultSet.getDate("date").toLocalDate().isAfter(LocalDate.now())) {
                ArrayList<Organ> procedureOrgans = new ArrayList<>();
                for (String organ : proceduresResultSet.getString("organs_affected").split(",")) {
                    procedureOrgans.add(Organ.parse(organ));
                }
                user.getPendingProcedures().add(new Procedure(
                        proceduresResultSet.getString("summary"),
                        proceduresResultSet.getString("description"),
                        proceduresResultSet.getDate("date").toLocalDate(),
                        procedureOrgans,
                        proceduresResultSet.getInt("id")
                ));
            } else {
                ArrayList<Organ> procedureOrgans = new ArrayList<>();
                for (String organ : proceduresResultSet.getString("organs_affected").split(",")) {
                    procedureOrgans.add(Organ.parse(organ));
                }
                user.getPreviousProcedures().add(new Procedure(
                        proceduresResultSet.getString("summary"),
                        proceduresResultSet.getString("description"),
                        proceduresResultSet.getDate("date").toLocalDate(),
                        procedureOrgans,
                        proceduresResultSet.getInt("id")
                ));
            }

        }

        //Get all the diseases for the given user

        String diseasesQuery = "SELECT * FROM " + currentDatabase + ".DISEASE WHERE user_id = ?";
        PreparedStatement diseasesStatement = connection.prepareStatement(diseasesQuery);

        diseasesStatement.setInt(1, userId);
        ResultSet diseasesResultSet = diseasesStatement.executeQuery();

        while (diseasesResultSet.next()) {

            if (diseasesResultSet.getBoolean("is_cured")) {
                user.getCuredDiseases().add(new Disease(
                        diseasesResultSet.getString("name"),
                        diseasesResultSet.getDate("diagnosis_date").toLocalDate(),
                        diseasesResultSet.getBoolean("is_chronic"),
                        diseasesResultSet.getBoolean("is_cured"),
                        diseasesResultSet.getInt("id")
                ));
            } else {
                user.getCurrentDiseases().add(new Disease(
                        diseasesResultSet.getString("name"),
                        diseasesResultSet.getDate("diagnosis_date").toLocalDate(),
                        diseasesResultSet.getBoolean("is_chronic"),
                        diseasesResultSet.getBoolean("is_cured"),
                        diseasesResultSet.getInt("id")
                ));
            }

        }

        //Get all waiting list items from database
        String waitingListQuery = "SELECT * FROM " + currentDatabase + ".WAITING_LIST_ITEM WHERE user_id = ?";
        PreparedStatement waitingListStatement = connection.prepareStatement(waitingListQuery);
        waitingListStatement.setInt(1, userId);
        ResultSet waitingListResultSet = waitingListStatement.executeQuery();


        while (waitingListResultSet.next()) {
            Organ organ = Organ.parse(waitingListResultSet.getString("organ_type"));
            LocalDate registeredDate = waitingListResultSet.getDate("organ_registered_date").toLocalDate();
            LocalDate deregisteredDate = waitingListResultSet.getDate("organ_deregistered_date") != null ? waitingListResultSet.getDate("organ_deregistered_date").toLocalDate() : null;
            int waitinguserId = waitingListResultSet.getInt("user_id");
            int deregisteredCode = waitingListResultSet.getInt("deregistered_code");
            int waitingListId = waitingListResultSet.getInt("id");

            user.getWaitingListItems().add(new WaitingListItem(organ, registeredDate, waitingListId, waitinguserId, deregisteredDate, waitingListId));
        }
        return user;
    }

    public void updateUserAttributes(User user, int userId) throws SQLException {
        //Attributes update
        String update = "UPDATE " + currentDatabase + ".USER SET first_name = ?, middle_names = ?, last_name = ?, preferred_name = ?," +
                " preferred_middle_names = ?, preferred_last_name = ?, current_address = ?, " +
                "region = ?, date_of_birth = ?, date_of_death = ?, height = ?, weight = ?, blood_pressure = ?, " +
                "gender = ?, gender_identity = ?, blood_type = ?, smoker_status = ?, alcohol_consumption = ?, username = ?, email = ?, password = ? " +
                "WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getNameArray()[0]);
        statement.setString(2, user.getNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null);
        statement.setString(3, user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null);

        statement.setString(4, user.getPreferredNameArray()[0]);
        statement.setString(5, user.getPreferredNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getPreferredNameArray(), 1, user.getPreferredNameArray().length - 1)) : null);
        statement.setString(6, user.getPreferredNameArray().length > 1 ? user.getPreferredNameArray()[user.getPreferredNameArray().length - 1] : null);

        statement.setString(7, user.getCurrentAddress());
        statement.setString(8, user.getRegion());
        statement.setDate(9, java.sql.Date.valueOf(user.getDateOfBirth()));
        statement.setDate(10, user.getDateOfDeath() != null ? java.sql.Date.valueOf(user.getDateOfDeath()) : null);
        statement.setDouble(11, user.getHeight());
        statement.setDouble(12, user.getWeight());
        statement.setString(13, user.getBloodPressure());
        statement.setString(14, user.getGender() != null ? user.getGender().toString() : null);
        statement.setString(15, user.getGenderIdentity() != null ? user.getGenderIdentity().toString() : null);
        statement.setString(16, user.getBloodType() != null ? user.getBloodType().toString() : null);
        statement.setString(17, user.getSmokerStatus() != null ? user.getSmokerStatus().toString() : null);
        statement.setString(18, user.getAlcoholConsumption() != null ? user.getAlcoholConsumption().toString() : null);
        statement.setString(19, user.getUsername());
        statement.setString(20, user.getEmail());
        statement.setString(21, user.getPassword());
        statement.setInt(22, userId);
        System.out.println("Update User Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());


/*        int userId = getUserId(user.getUsername());

        //Organ Updates
        //First get rid of all the users organs in the table
        String deleteOrgansQuery = "DELETE FROM " + currentDatabase + ".DONATION_LIST_ITEM WHERE user_id = ?";
        PreparedStatement deleteOrgansStatement = connection.prepareStatement(deleteOrgansQuery);
        deleteOrgansStatement.setInt(1, userId);
        System.out.println("Organ rows deleted: " + deleteOrgansStatement.executeUpdate());

        int totalAdded = 0;
        //Then repopulate it with the new updated organs
        for (Organ organ: user.getOrgans()) {
            String insertOrgansQuery = "INSERT INTO " + currentDatabase + ".DONATION_LIST_ITEM (name, user_id) VALUES (?, ?)";
            PreparedStatement insertOrgansStatement = connection.prepareStatement(insertOrgansQuery);
            insertOrgansStatement.setString(1, organ.toString());
            insertOrgansStatement.setInt(2, userId);
            totalAdded += insertOrgansStatement.executeUpdate();
        }
        System.out.println("Update User Organ Donations -> Successful -> Rows Updated: " + totalAdded);*/
    }

    public void removeUser(User user) throws SQLException {
        String update = "DELETE FROM " + currentDatabase + ".USER WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getUsername());
        System.out.println("Deletion of User: " + user.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }
}