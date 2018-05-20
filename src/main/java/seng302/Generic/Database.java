package seng302.Generic;

import com.google.api.client.util.DateTime;
import seng302.User.Admin;
import seng302.User.Attribute.*;
import seng302.User.Clinician;
import seng302.User.Medication.Medication;
import seng302.User.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


public class Database {

    private String testDatabase = "seng302-2018-team300-test";
    private String username = "seng302-team300";
    private String password = "WeldonAside5766";
    private String url = "jdbc:mysql://mysql2.csse.canterbury.ac.nz/";
    private String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    private Connection connection;


    public int getUserId(String username) throws SQLException{
        String query = "SELECT id FROM USER WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");
    }

    public int getClinicianId(String username) throws SQLException{
        String query = "SELECT staff_id FROM CLINICIAN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("staff_id");
    }

    public int getAdminId(String username) throws SQLException{
        String query = "SELECT staff_id FROM ADMIN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("staff_id");
    }

    public void insertUser(User user) throws SQLException {
        String insert = "INSERT INTO USER(first_name, middle_names, last_name, creation_time, last_modified, username," +
                " email, password, date_of_birth) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, user.getNameArray()[0]);
        statement.setString(2, user.getNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null);
        statement.setString(3, user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null);
        statement.setTimestamp(4, java.sql.Timestamp.valueOf(user.getCreationTime()));
        statement.setTimestamp(5, java.sql.Timestamp.valueOf(user.getCreationTime()));
        statement.setString(6, user.getUsername());
        statement.setString(7, user.getEmail());
        //TODO Hash the password
        statement.setString(8, user.getPassword());
        statement.setDate(9, java.sql.Date.valueOf(user.getDateOfBirth()));
        System.out.println("Inserting new user -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void insertWaitingListItem(User currentUser, WaitingListItem waitingListItem) throws SQLException{
        String insert = "INSERT INTO WAITING_LIST_ITEM (organ_type, organ_registered_date, user_id) VALUES  (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, waitingListItem.organType.toString());
        statement.setDate(2, java.sql.Date.valueOf(waitingListItem.getOrganRegisteredDate()));
        statement.setLong(3, currentUser.getId());
        System.out.println("Inserting new waiting list item -> Successful -> Rows Added: " + statement.executeUpdate());
    }

    public void updateUserAccountSettings(User user, int userId) throws SQLException {
        String update = "UPDATE USER SET username = ?, email = ?, password = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setInt(4, userId);
        System.out.println("Update User Account Settings -> Successful -> Rows Updated: " + statement.executeUpdate());

    }

    public void updateUserAttributesAndOrgans(User user) throws SQLException {
        //Attributes update
        String update = "UPDATE USER SET first_name = ?, middle_names = ?, last_name = ?, preferred_name = ?," +
                " preferred_middle_names = ?, preferred_last_name = ?, current_address = ?, " +
                "region = ?, date_of_birth = ?, date_of_death = ?, height = ?, weight = ?, blood_pressure = ?, " +
                "gender = ?, gender_identity = ?, blood_type = ?, smoker_status = ?, alcohol_consumption = ?  WHERE username = ?";
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
        System.out.println("Update User Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());


        int userId = getUserId(user.getUsername());

        //Organ Updates
        //First get rid of all the users organs in the table
        String deleteOrgansQuery = "DELETE FROM DONATION_LIST_ITEM WHERE user_id = ?";
        PreparedStatement deleteOrgansStatement = connection.prepareStatement(deleteOrgansQuery);
        deleteOrgansStatement.setInt(1, userId);
        System.out.println("Organ rows deleted: " + deleteOrgansStatement.executeUpdate());

        int totalAdded = 0;
        //Then repopulate it with the new updated organs
        for (Organ organ: user.getOrgans()) {
            String insertOrgansQuery = "INSERT INTO DONATION_LIST_ITEM (name, user_id) VALUES (?, ?)";
            PreparedStatement insertOrgansStatement = connection.prepareStatement(insertOrgansQuery);
            insertOrgansStatement.setString(1, organ.toString());
            insertOrgansStatement.setInt(2, userId);
            totalAdded += insertOrgansStatement.executeUpdate();
        }
        System.out.println("Update User Organ Donations -> Successful -> Rows Updated: " + totalAdded);
    }

    public void updateUserProcedures(User user) throws SQLException {
        int userId = getUserId(user.getUsername());

        //Procedure Updates
        //First get rid of all the users procedures in the table
        String deleteProceduresQuery = "DELETE FROM PROCEDURES WHERE user_id = ?";
        PreparedStatement deleteProceduresStatement = connection.prepareStatement(deleteProceduresQuery);
        deleteProceduresStatement.setInt(1, userId);
        System.out.println("Procedure rows deleted: " + deleteProceduresStatement.executeUpdate());


        int totalAdded = 0;
        //Then repopulate it with the new updated procedures
        ArrayList<Procedure> allProcedures = new ArrayList<>();
        allProcedures.addAll(user.getPendingProcedures());
        allProcedures.addAll(user.getPreviousProcedures());
        for (Procedure procedure: allProcedures) {
            String insertProceduresQuery = "INSERT INTO PROCEDURES (summary, description, date, organs_affected, user_id) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement insertProceduresStatement = connection.prepareStatement(insertProceduresQuery);

            insertProceduresStatement.setString(1, procedure.getSummary());
            insertProceduresStatement.setString(2, procedure.getDescription());
            insertProceduresStatement.setDate(3, java.sql.Date.valueOf(procedure.getDate()));

            String organsAffected = "";
            for (Organ organ: procedure.getOrgansAffected()) {
                organsAffected += organ.toString() + ",";
            }
            organsAffected = organsAffected.substring(0, organsAffected.length() - 1);

            insertProceduresStatement.setString(4, organsAffected);
            insertProceduresStatement.setInt(5, userId);

            totalAdded += insertProceduresStatement.executeUpdate();
        }

        System.out.println("Update User Procedures -> Successful -> Rows Updated: " + totalAdded);

    }

    public void updateUserDiseases(User user) throws SQLException {
        int userId = getUserId(user.getUsername());

        //Disease Updates
        //First get rid of all the user's diseases in the table
        String deleteDiseasesQuery = "DELETE FROM DISEASE WHERE user_id = ?";
        PreparedStatement deleteDiseasesStatement = connection.prepareStatement(deleteDiseasesQuery);
        deleteDiseasesStatement.setInt(1, userId);
        System.out.println("Disease rows deleted: " + deleteDiseasesStatement.executeUpdate());


        int totalAdded = 0;
        //Then repopulate it with the new updated diseases
        ArrayList<Disease> allDiseases = new ArrayList<>();
        allDiseases.addAll(user.getCurrentDiseases());
        allDiseases.addAll(user.getCuredDiseases());
        for (Disease disease: allDiseases) {
            String insertDiseasesQuery = "INSERT INTO DISEASE (name, diagnosis_date, is_cured, is_chronic, user_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertDiseasesStatement = connection.prepareStatement(insertDiseasesQuery);

            insertDiseasesStatement.setString(1, disease.getName());
            insertDiseasesStatement.setDate(2, java.sql.Date.valueOf(disease.getDiagnosisDate()));
            insertDiseasesStatement.setBoolean(3, disease.isCured());
            insertDiseasesStatement.setBoolean(4, disease.isChronic());
            insertDiseasesStatement.setInt(5, userId);

            totalAdded += insertDiseasesStatement.executeUpdate();
        }

        System.out.println("Update User Diseases -> Successful -> Rows Updated: " + totalAdded);

    }

    public void updateUserMedications(User user) throws SQLException {
        int userId = getUserId(user.getUsername());

        //Medication Updates
        //First get rid of all the users medications in the table
        String deleteMedicationsQuery = "DELETE FROM MEDICATION WHERE user_id = ?";
        PreparedStatement deleteMedicationsStatement = connection.prepareStatement(deleteMedicationsQuery);
        deleteMedicationsStatement.setInt(1, userId);
        System.out.println("Medication rows deleted: " + deleteMedicationsStatement.executeUpdate());


        int totalAdded = 0;
        //Then repopulate it with the new updated medications
        ArrayList<Medication> allMedications = new ArrayList<>();
        allMedications.addAll(user.getCurrentMedications());
        allMedications.addAll(user.getHistoricMedications());
        for (Medication medication: allMedications) {
            String insertMedicationsQuery = "INSERT INTO MEDICATION (name, active_ingredients, history, user_id) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement insertMedicationsStatement = connection.prepareStatement(insertMedicationsQuery);

            String activeIngredientsString = String.join(",", medication.getActiveIngredients());
            String historyString = String.join(",", medication.getHistory());

            insertMedicationsStatement.setString(1, medication.getName());
            insertMedicationsStatement.setString(2, activeIngredientsString);
            insertMedicationsStatement.setString(3, historyString);
            insertMedicationsStatement.setInt(4, userId);

            totalAdded += insertMedicationsStatement.executeUpdate();
        }

        System.out.println("Update User Medications -> Successful -> Rows Updated: " + totalAdded);

    }

    public boolean isUniqueUser(String item) throws SQLException{
        String query = "SELECT * FROM USER WHERE USER.username = ? OR USER.email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, item);
        statement.setString(2, item);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        query = "SELECT * FROM CLINICIAN WHERE CLINICIAN.username = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, item);
        resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        query = "SELECT * FROM ADMIN WHERE ADMIN.username = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, item);
        resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        return true;
    }

    public void insertClinician(Clinician clinician) throws SQLException {
        String insert = "INSERT INTO CLINICIAN(username, password, name, work_address, region) " +
                "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, clinician.getUsername());
        statement.setString(2, clinician.getPassword());
        statement.setString(3, clinician.getName());
        statement.setString(4, clinician.getWorkAddress());
        statement.setString(5, clinician.getRegion());
        System.out.println("Inserting new Clinician -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void updateClinicianDetails(Clinician clinician) throws SQLException {
        String update = "UPDATE CLINICIAN SET name = ?, work_address = ?, region = ? WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, clinician.getName());
        statement.setString(2, clinician.getWorkAddress());
        statement.setString(3, clinician.getRegion());
        statement.setString(4, clinician.getUsername());
        System.out.println("Update Clinician Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());

    }

    public void updateClinicianAccountSettings(Clinician clinician, int clinicianId) throws SQLException {
        String update = "UPDATE CLINICIAN SET username = ?, password = ? WHERE staff_id = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, clinician.getUsername());
        statement.setString(2, clinician.getPassword());
        statement.setInt(3, clinicianId);
        System.out.println("Update Clinician Account Settings -> Successful -> Rows Updated: " + statement.executeUpdate());
    }

    public void insertAdmin(Admin admin) throws SQLException {
        String insert = "INSERT INTO ADMIN(username, password, name, work_address, region) " +
                "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, admin.getUsername());
        statement.setString(2, admin.getPassword());
        statement.setString(3, admin.getName());
        statement.setString(4, admin.getWorkAddress());
        statement.setString(5, admin.getRegion());
        System.out.println("Inserting new Admin -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void updateAdminDetails(Admin admin) throws SQLException {
        String update = "UPDATE ADMIN SET name = ?, work_address = ? WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, admin.getName());
        statement.setString(2, admin.getWorkAddress());
       // statement.setString(3, admin.getRegion()); -- No Region for an Admin!
        statement.setString(3, admin.getUsername());
        System.out.println("Update Admin Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());

    }


    public User loginUser(String usernameEmail, String password) throws SQLException {
        //First needs to do a search to see if there is a unique user with the given inputs
        // SELECT * FROM USER WHERE username = usernameEmail OR email = usernameEmail AND password = password
        String query = "SELECT * FROM USER WHERE (username = ? OR email = ?) AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, usernameEmail);
        statement.setString(2, usernameEmail);
        statement.setString(3, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new User Object with the fields from the database
            return getUserFromResultSet(resultSet);
        }

    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {

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
        if(resultSet.getString("preferred_middle_names") != null) {
            for (String middleName: resultSet.getString("preferred_middle_names").split(",")) {
                preferredNameString += middleName + " ";
            }
        }
        preferredNameString += resultSet.getString("preferred_last_name");

        user.setPreferredNameArray(preferredNameString.split(" "));


        user.setGenderIdentity(resultSet.getString("gender_identity") != null ? Gender.parse(resultSet.getString("gender_identity")) : null);

        if(resultSet.getString("blood_pressure") != null) {
            user.setBloodPressure(resultSet.getString("blood_pressure"));
        } else {
            user.setBloodPressure("");
        }

        if(resultSet.getString("smoker_status") != null) {
            user.setSmokerStatus(SmokerStatus.parse(resultSet.getString("smoker_status")));
        } else {
            user.setSmokerStatus(null);
        }

        if(resultSet.getString("alcohol_consumption") != null) {
            user.setAlcoholConsumption(AlcoholConsumption.parse(resultSet.getString("alcohol_consumption")));
        } else {
            user.setAlcoholConsumption(null);
        }

        //Get all the organs for the given user

        int userId = getUserId(resultSet.getString("username"));
        user.setId(userId);

        String organsQuery = "SELECT * FROM DONATION_LIST_ITEM WHERE user_id = ?";
        PreparedStatement organsStatement = connection.prepareStatement(organsQuery);
        organsStatement.setInt(1, userId);
        ResultSet organsResultSet = organsStatement.executeQuery();

        while(organsResultSet.next()) {
            user.getOrgans().add(Organ.parse(organsResultSet.getString("name")));
        }

        //Get all the medications for the given user

        String medicationsQuery = "SELECT * FROM MEDICATION WHERE user_id = ?";
        PreparedStatement medicationsStatement = connection.prepareStatement(medicationsQuery);
        medicationsStatement.setInt(1, userId);
        ResultSet medicationsResultSet = medicationsStatement.executeQuery();

        while(medicationsResultSet.next()) {
            String[] activeIngredients = medicationsResultSet.getString("active_ingredients").split(",");
            String[] historyStringList = medicationsResultSet.getString("history").split(",");

            ArrayList<String> historyList = new ArrayList<>();
            //Iterate through the history list to get associated values together
            int counter = 1;
            String historyItem = "";
            for(int i = 0; i < historyStringList.length; i++) {
                if(counter % 2 == 1) {
                    historyItem += historyStringList[i] + ",";
                    counter++;
                } else {
                    historyItem += historyStringList[i];
                    historyList.add(historyItem);
                    historyItem = "";
                    counter++;
                }

            }


            if(historyList.get(historyList.size() - 1).contains("Stopped taking")) { //Medication is historic
                user.getHistoricMedications().add(new Medication(
                        medicationsResultSet.getString("name"),
                        activeIngredients,
                        historyList
                ));
            } else { //Medication is Current
                user.getCurrentMedications().add(new Medication(
                        medicationsResultSet.getString("name"),
                        activeIngredients,
                        historyList
                ));
            }
        }


        //Get all the procedures for the given user

        String proceduresQuery = "SELECT * FROM PROCEDURES WHERE user_id = ?";
        PreparedStatement proceduresStatement = connection.prepareStatement(proceduresQuery);
        proceduresStatement.setInt(1, userId);
        ResultSet proceduresResultSet = proceduresStatement.executeQuery();

        while(proceduresResultSet.next()) {

            if(proceduresResultSet.getDate("date").toLocalDate().isAfter(LocalDate.now())) {
                ArrayList<Organ> procedureOrgans = new ArrayList<>();
                for (String organ: proceduresResultSet.getString("organs_affected").split(",")) {
                    procedureOrgans.add(Organ.parse(organ));
                }
                user.getPendingProcedures().add(new Procedure(
                        proceduresResultSet.getString("summary"),
                        proceduresResultSet.getString("description"),
                        proceduresResultSet.getDate("date").toLocalDate(),
                        procedureOrgans
                ));
            } else {
                ArrayList<Organ> procedureOrgans = new ArrayList<>();
                for (String organ: proceduresResultSet.getString("organs_affected").split(",")) {
                    procedureOrgans.add(Organ.parse(organ));
                }
                user.getPreviousProcedures().add(new Procedure(
                        proceduresResultSet.getString("summary"),
                        proceduresResultSet.getString("description"),
                        proceduresResultSet.getDate("date").toLocalDate(),
                        procedureOrgans
                ));
            }

        }

        //Get all the diseases for the given user

        String diseasesQuery = "SELECT * FROM DISEASE WHERE user_id = ?";
        PreparedStatement diseasesStatement = connection.prepareStatement(diseasesQuery);
        diseasesStatement.setInt(1, userId);
        ResultSet diseasesResultSet = diseasesStatement.executeQuery();

        while(diseasesResultSet.next()) {

            if(diseasesResultSet.getBoolean("is_cured")) {
                user.getCuredDiseases().add(new Disease(
                        diseasesResultSet.getString("name"),
                        diseasesResultSet.getDate("diagnosis_date").toLocalDate(),
                        diseasesResultSet.getBoolean("is_chronic"),
                        diseasesResultSet.getBoolean("is_cured")
                ));
            } else {
                user.getCurrentDiseases().add(new Disease(
                        diseasesResultSet.getString("name"),
                        diseasesResultSet.getDate("diagnosis_date").toLocalDate(),
                        diseasesResultSet.getBoolean("is_chronic"),
                        diseasesResultSet.getBoolean("is_cured")
                ));
            }

        }

        //Get all waiting list items from database
        String waitingListQuery = "SELECT * FROM WAITING_LIST_ITEM WHERE user_id = ?";
        PreparedStatement waitingListStatement = connection.prepareStatement(waitingListQuery);
        waitingListStatement.setInt(1, userId);
        ResultSet waitingListResultSet = waitingListStatement.executeQuery();

        //user.setGenderIdentity(resultSet.getString("gender_identity") != null ? Gender.parse(resultSet.getString("gender_identity")) : null);

        while(waitingListResultSet.next()) {
            Organ organ = Organ.parse(waitingListResultSet.getString("organ_type"));
            LocalDate registeredDate = waitingListResultSet.getDate("organ_registered_date").toLocalDate();
            LocalDate deregisteredDate = waitingListResultSet.getDate("organ_deregistered_date") != null ? waitingListResultSet.getDate("organ_deregistered_date").toLocalDate() : null;
            Long waitinguserId = Long.valueOf(String.valueOf(waitingListResultSet.getInt("user_id")));
            Integer deregisteredCode = String.valueOf(waitingListResultSet.getInt("deregistered_code")) != null ? waitingListResultSet.getInt("deregistered_code") : null;
            Integer waitingListId = waitingListResultSet.getInt("id");

            user.getWaitingListItems().add(new ReceiverWaitingListItem(organ, registeredDate, deregisteredDate, waitinguserId, deregisteredCode, waitingListId));
        }
        return user;
    }


    private void refreshUserWaitinglists() throws SQLException{
        String waitingListQuery = "SELECT * FROM WAITING_LIST_ITEM";
        PreparedStatement waitingListStatement = connection.prepareStatement(waitingListQuery);
        ResultSet waitingListResultSet = waitingListStatement.executeQuery();

        for (User user: DataManager.users) {
            user.getWaitingListItems().clear();
        }

        while(waitingListResultSet.next()) {
            Organ organ = Organ.parse(waitingListResultSet.getString("organ_type"));
            LocalDate registeredDate = waitingListResultSet.getDate("organ_registered_date").toLocalDate();
            LocalDate deregisteredDate = waitingListResultSet.getDate("organ_deregistered_date") != null ? waitingListResultSet.getDate("organ_deregistered_date").toLocalDate() : null;
            Long waitinguserId = Long.valueOf(String.valueOf(waitingListResultSet.getInt("user_id")));
            Integer deregisteredCode = String.valueOf(waitingListResultSet.getInt("deregistered_code")) != null ? waitingListResultSet.getInt("deregistered_code") : null;
            Integer waitingListId = waitingListResultSet.getInt("id");


            SearchUtils.getUserById(waitinguserId).getWaitingListItems().add(new ReceiverWaitingListItem(organ, registeredDate, deregisteredDate, waitinguserId, deregisteredCode, waitingListId));
        }
    }

    public void transplantDeregister(Integer waitingListItemId, Long userId) throws SQLException{
        User user = SearchUtils.getUserById(userId);
        ReceiverWaitingListItem waitingListItem = null;
        for (ReceiverWaitingListItem item: user.getWaitingListItems()) {
            if (item.getWaitingListItemId().equals(waitingListItemId)) {
                waitingListItem = item;
            }
        }
        String update = "UPDATE WAITING_LIST_ITEM SET organ_deregistered_date = ?, deregistered_code = ? WHERE id = ?";
        PreparedStatement deregisterStatement = connection.prepareStatement(update);
        deregisterStatement.setDate(1, java.sql.Date.valueOf(waitingListItem.getOrganDeregisteredDate()));
        deregisterStatement.setInt(2, waitingListItem.getOrganDeregisteredCode());
        deregisterStatement.setInt(3, waitingListItem.getWaitingListItemId());
        System.out.println("Update waitinglist Attributes -> Successful -> Rows Updated: " + deregisterStatement.executeUpdate());
        refreshUserWaitinglists();
    }

    public Clinician loginClinician(String username, String password) throws SQLException{
        //First needs to do a search to see if there is a unique clinician with the given inputs
        String query = "SELECT * FROM CLINICIAN WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new Clinican Object with the fields from the database
            return getClinicianFromResultSet(resultSet);
        }

    }

    private Clinician getClinicianFromResultSet(ResultSet resultSet) throws SQLException{
        Clinician clinician = new Clinician(
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("name")
        );
        clinician.setWorkAddress(resultSet.getString("work_address"));
        clinician.setRegion(resultSet.getString("region"));
        clinician.setStaffID(resultSet.getInt("staff_id"));



        return clinician;
    }

    public Admin loginAdmin(String usernameEmail, String password) throws SQLException {
        //First needs to do a search to see if there is a unique admin with the given inputs
        String query = "SELECT * FROM ADMIN WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, usernameEmail);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new Admin Object with the fields from the database
            return getAdminFromResultSet(resultSet);
        }

    }

    private Admin getAdminFromResultSet(ResultSet resultSet) throws SQLException{
        Admin admin = new Admin(
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("name")
        );
        admin.setWorkAddress(resultSet.getString("work_address"));
        admin.setRegion(resultSet.getString("region"));
        admin.setStaffID(resultSet.getInt("staff_id"));

        return admin;
    }

    public ArrayList<User> getAllUsers() throws SQLException{
        ArrayList<User> allUsers = new ArrayList<>();
        String query = "SELECT * FROM USER";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            allUsers.add(getUserFromResultSet(resultSet));
        }

        return allUsers;
    }

    public ArrayList<Clinician> getAllClinicians() throws SQLException{
        ArrayList<Clinician> allClinicans = new ArrayList<>();
        String query = "SELECT * FROM CLINICIAN";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            allClinicans.add(getClinicianFromResultSet(resultSet));
        }

        return allClinicans;
    }

    public ArrayList<Admin> getAllAdmins() throws SQLException{
        ArrayList<Admin> allAdmins = new ArrayList<>();
        String query = "SELECT * FROM ADMIN";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            allAdmins.add(getAdminFromResultSet(resultSet));
        }

        return allAdmins;
    }

    public void removeUser(User user) throws SQLException {
        String update = "DELETE FROM USER WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getUsername());
        System.out.println("Deletion of User: " + user.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }

    public void removeClinician(Clinician clinician) throws SQLException {
        String update = "DELETE FROM CLINICIAN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, clinician.getUsername());
        System.out.println("Deletion of Clinician: " + clinician.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }

    public void removeAdmin(Admin admin) throws SQLException {
        String update = "DELETE FROM ADMIN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, admin.getUsername());
        System.out.println("Deletion of Admin: " + admin.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }

    public void resetDatabase() throws SQLException{
        String update = "DELETE FROM WAITING_LIST_ITEM";
        PreparedStatement statement = connection.prepareStatement(update);
        System.out.println("Reset of database (WAITING_LIST_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM PROCEDURES";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (PROCEDURE): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM MEDICATION";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (MEDICATION): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM DONATION_LIST_ITEM";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (DONATION_LIST_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM DISEASE";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (DISEASE): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM ADMIN";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (ADMIN): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM CLINICIAN";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (CLINICIAN): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM USER";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (USER): -> Successful -> Rows Removed: " + statement.executeUpdate());

        String insert = "INSERT INTO CLINICIAN(username, password, name, work_address, region, staff_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(insert);
        statement.setString(1, "default");
        statement.setString(2, "default");
        statement.setString(3, "default");
        statement.setString(4, "default");
        statement.setString(5, "default");
        statement.setInt(6, 1);
        System.out.println("Inserting Default Clinician -> Successful -> Rows Added: " + statement.executeUpdate());

        insert = "INSERT INTO ADMIN(username, password, name, work_address, region, staff_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(insert);
        statement.setString(1, "admin");
        statement.setString(2, "default");
        statement.setString(3, "default");
        statement.setString(4, "default");
        statement.setString(5, "default");
        statement.setInt(6, 1);
        System.out.println("Inserting Default Admin -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void loadSampleData() throws SQLException {

        ArrayList<User> allUsers = new ArrayList<>();
        User user1 = new User("Andy", new String[]{"Robert"}, "French", LocalDate.now(), "andy", "andy@andy.com", "andrew");
        allUsers.add(user1);
        User user2 = new User("Buzz", new String[]{"Buzzy"}, "Knight", LocalDate.now(), "buzz", "buzz@buzz.com", "drowssap");
        allUsers.add(user2);
        User user3 = new User("James", new String[]{"Mozza"}, "Morritt", LocalDate.now(), "mozza", "mozza@mozza.com", "mozza");
        allUsers.add(user3);
        User user4 = new User("Jono", new String[]{"Zilla"}, "Hills", LocalDate.now(), "jonozilla", "zilla@zilla.com", "zilla");
        allUsers.add(user4);
        User user5 = new User("James", new String[]{"Mackas"}, "Mackay", LocalDate.now(), "mackas", "mackas@mackas.com", "mackas");
        allUsers.add(user5);
        User user6 = new User("Nicky", new String[]{"The Dark Horse"}, "Zohrab-Henricks", LocalDate.now(), "nicky", "nicky@nicky.com", "nicky");
        allUsers.add(user6);
        User user7 = new User("Kyran", new String[]{"Playing Fortnite"}, "Stagg", LocalDate.now(), "kyran", "kyran@kyran.com", "fortnite");
        allUsers.add(user7);
        User user8 = new User("Andrew", new String[]{"Daveo"}, "Davidson", LocalDate.now(), "andrew", "andrew@andrew.com", "andrew");
        allUsers.add(user8);

        for (User user: allUsers) {
            insertUser(user);
        }


    }

    private void connectToDatabase() {
        try{
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(
                    url + testDatabase, username, password);
            System.out.println("Connected to test database");
            System.out.println(LocalDateTime.now());


            //Statement stmt=con.createStatement();
            //ResultSet rs=stmt.executeQuery("SELECT * FROM USER");
            //System.out.println("Users:");

//            while(rs.next())
//                System.out.println(rs.getString(1));
//            con.close();
        } catch(Exception e){
            System.out.println(e);
        }
    }
}