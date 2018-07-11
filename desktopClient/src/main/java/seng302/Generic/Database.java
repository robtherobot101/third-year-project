package seng302.Generic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import seng302.User.Admin;
import seng302.User.Attribute.*;
import seng302.User.Clinician;
import seng302.User.Disease;
import seng302.User.Medication.Medication;
import seng302.User.Procedure;
import seng302.User.ReceiverWaitingListItem;
import seng302.User.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import seng302.User.WaitingListItem;


public class Database {

    private String currentDatabase = "`seng302-2018-team300-prod`";
    private String connectDatabase = "seng302-2018-team300-prod";
    private String username = "seng302-team300";
    private String password = "WeldonAside5766";
    private String url = "jdbc:mysql://mysql2.csse.canterbury.ac.nz/";
    private String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    private Connection connection;

    APIServer server = new APIServer("http://csse-s302g3.canterbury.ac.nz:80/api/v1");

    public int getUserId(String username) {
        for(User user:getAllUsers()){
            if(user.getUsername().equals(username)) {
                return (int)user.getId();
            }
        }
        return -1;
    }

    public int getClinicianId(String username) throws SQLException{
        String query = "SELECT staff_id FROM " + currentDatabase + ".CLINICIAN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("staff_id");
    }

    public int getAdminId(String username) throws SQLException{
        String query = "SELECT staff_id FROM " + currentDatabase + ".ADMIN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("staff_id");
    }

    public void insertUser(User user) throws SQLException {
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
        Debugger.log("Inserting new user -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void insertWaitingListItem(User currentUser, WaitingListItem waitingListItem) throws SQLException{
        String query = "SELECT COUNT(*) FROM " + currentDatabase + ".WAITING_LIST_ITEM WHERE id = user_id = ? AND organ_type = ?";
        PreparedStatement queryStatement  =connection.prepareStatement(query);
        queryStatement.setLong(1, waitingListItem.getUserId());
        queryStatement.setString(2, waitingListItem.getOrganType().toString().toLowerCase());
        ResultSet resultSet = queryStatement.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) != 0) {
            Debugger.log("deleting");
            String deleteQuery = "DELETE FROM " + currentDatabase + ".WAITING_LIST_ITEM WHERE user_id = ? AND organ_type = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setLong(1, waitingListItem.getUserId());
            deleteStatement.setString(2, waitingListItem.getOrganType().toString().toLowerCase());
            deleteStatement.executeQuery();
        }

        String insert = "INSERT INTO " + currentDatabase + ".WAITING_LIST_ITEM (organ_type, organ_registered_date, user_id ) VALUES  (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, waitingListItem.getOrganType().toString());
        statement.setDate(2, java.sql.Date.valueOf(waitingListItem.getOrganRegisteredDate()));
        statement.setLong(3, currentUser.getId());
        Debugger.log("Inserting new waiting list item -> Successful -> Rows Added: " + statement.executeUpdate());
    }

    public void updateUserAccountSettings(User user, int userId) throws SQLException {
        String update = "UPDATE " + currentDatabase + ".USER SET username = ?, email = ?, password = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setInt(4, userId);
        Debugger.log("Update User Account Settings -> Successful -> Rows Updated: " + statement.executeUpdate());

    }


    //Uses API server for updating attributes
    public void updateUserAttributesAndOrgans(User user) {
        updateUserAttributes(user).getAsString();
        clearUserDonations((int)user.getId());
        insertAllUserDonations(user);
        Debugger.log("Update User Attributes and Donations -> Successful");
    }


    private APIResponse updateUserAttributes(User user) {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        System.out.println("Patch: " + userJson);
        return server.patchRequest(userJson, new HashMap<>(), "users",String.valueOf(user.getId()));
    }

    private APIResponse clearUserDonations(int userID) {
        return server.deleteRequest(new HashMap<>(), "users",String.valueOf(userID),"donations");
    }

    private APIResponse insertAllUserDonations(User user) {
        for (Organ organ: user.getOrgans()) {
            APIResponse response = insertUserDonation(user.getId(), organ);
            if(response.getStatusCode()!=201) {
                return new APIResponse(response.getStatusCode());
            }

        }
        return new APIResponse(201);
    }

    private APIResponse insertUserDonation(long userID, Organ organ) {
        JsonObject organJson = new JsonObject();
        organJson.addProperty("name", organ.name());
        return server.postRequest(organJson, new HashMap<>(),"users",String.valueOf(userID),"donations");
    }

    public void updateUserProcedures(User user) {
        int userId = getUserId(user.getUsername());

        //Procedure Updates
        //First get rid of all the users procedures in the table
        clearUserProcedures(userId);

        for (Procedure procedure: user.getPendingProcedures()) {
            JsonParser jp = new JsonParser();
            JsonObject procedureJson = jp.parse(new Gson().toJson(procedure)).getAsJsonObject();
            server.postRequest(procedureJson, new HashMap<String, String>(), "users",String.valueOf(userId), "procedures");
        }

        for (Procedure procedure: user.getPreviousProcedures()) {
            JsonParser jp = new JsonParser();
            JsonObject procedureJson = jp.parse(new Gson().toJson(procedure)).getAsJsonObject();
            server.postRequest(procedureJson, new HashMap<String, String>(), "users",String.valueOf(userId), "procedures");
        }
    }

    private void clearUserProcedures(int userID) {
        APIResponse response = server.getRequest(new HashMap<>(), "users",String.valueOf(userID),"procedures");
        if(response.isValidJson()){
            for(JsonElement procedureJson: response.getAsJsonArray()) {
                int procedureId = ((JsonObject)procedureJson).get("id").getAsInt();
                server.deleteRequest(new HashMap<>(), "users",String.valueOf(userID),"procedures",String.valueOf(procedureId));
            }
        }
    }

    public void updateUserDiseases(User user) throws SQLException {
        int userId = getUserId(user.getUsername());

        //Disease Updates
        //First get rid of all the users diseases in the table
        String deleteDiseasesQuery = "DELETE FROM " + currentDatabase + ".DISEASE WHERE user_id = ?";
        PreparedStatement deleteDiseasesStatement = connection.prepareStatement(deleteDiseasesQuery);
        deleteDiseasesStatement.setInt(1, userId);
        Debugger.log("Disease rows deleted: " + deleteDiseasesStatement.executeUpdate());


        int totalAdded = 0;
        //Then repopulate it with the new updated diseases
        ArrayList<Disease> allDiseases = new ArrayList<>();
        allDiseases.addAll(user.getCurrentDiseases());
        allDiseases.addAll(user.getCuredDiseases());
        for (Disease disease: allDiseases) {
            String insertDiseasesQuery = "INSERT INTO " + currentDatabase + ".DISEASE (name, diagnosis_date, is_cured, is_chronic, user_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertDiseasesStatement = connection.prepareStatement(insertDiseasesQuery);

            insertDiseasesStatement.setString(1, disease.getName());
            insertDiseasesStatement.setDate(2, java.sql.Date.valueOf(disease.getDiagnosisDate()));
            insertDiseasesStatement.setBoolean(3, disease.isCured());
            insertDiseasesStatement.setBoolean(4, disease.isChronic());
            insertDiseasesStatement.setInt(5, userId);

            totalAdded += insertDiseasesStatement.executeUpdate();
        }

        Debugger.log("Update User Diseases -> Successful -> Rows Updated: " + totalAdded);

    }

    public void updateUserMedications(User user) throws SQLException {
        int userId = getUserId(user.getUsername());

        //Medication Updates
        //First get rid of all the users medications in the table
        String deleteMedicationsQuery = "DELETE FROM " + currentDatabase + ".MEDICATION WHERE user_id = ?";
        PreparedStatement deleteMedicationsStatement = connection.prepareStatement(deleteMedicationsQuery);

        deleteMedicationsStatement.setInt(1, userId);
        Debugger.log("Medication rows deleted: " + deleteMedicationsStatement.executeUpdate());


        int totalAdded = 0;
        //Then repopulate it with the new updated medications
        ArrayList<Medication> allMedications = new ArrayList<>();
        allMedications.addAll(user.getCurrentMedications());
        allMedications.addAll(user.getHistoricMedications());
        for (Medication medication: allMedications) {
            String insertMedicationsQuery = "INSERT INTO " + currentDatabase + ".MEDICATION (name, active_ingredients, history, user_id) " +
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

        Debugger.log("Update User Medications -> Successful -> Rows Updated: " + totalAdded);

    }

    public boolean isUniqueUser(String item) throws SQLException{
        String query = "SELECT * FROM " + currentDatabase + ".USER WHERE USER.username = ? OR USER.email = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, item);
        statement.setString(2, item);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        query = "SELECT * FROM " + currentDatabase + ".CLINICIAN WHERE CLINICIAN.username = ?";
        statement = connection.prepareStatement(query);

        statement.setString(1, item);
        resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        query = "SELECT * FROM " + currentDatabase + ".ADMIN WHERE ADMIN.username = ?";
        statement = connection.prepareStatement(query);

        statement.setString(1, item);
        resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        return true;
    }

    public void insertClinician(Clinician clinician) throws SQLException {
        String insert = "INSERT INTO " + currentDatabase + ".CLINICIAN(username, password, name, work_address, region) " +
                "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);

        statement.setString(1, clinician.getUsername());
        statement.setString(2, clinician.getPassword());
        statement.setString(3, clinician.getName());
        statement.setString(4, clinician.getWorkAddress());
        statement.setString(5, clinician.getRegion());
        Debugger.log("Inserting new Clinician -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void updateClinicianDetails(Clinician clinician) throws SQLException {
        String update = "UPDATE " + currentDatabase + ".CLINICIAN SET name = ?, work_address = ?, region = ? WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);

        statement.setString(1, clinician.getName());
        statement.setString(2, clinician.getWorkAddress());
        statement.setString(3, clinician.getRegion());
        statement.setString(4, clinician.getUsername());
        Debugger.log("Update Clinician Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());

    }

    public void updateClinicianAccountSettings(Clinician clinician, int clinicianId) throws SQLException {
        String update = "UPDATE " + currentDatabase + ".CLINICIAN SET username = ?, password = ? WHERE staff_id = ?";
        PreparedStatement statement = connection.prepareStatement(update);

        statement.setString(1, clinician.getUsername());
        statement.setString(2, clinician.getPassword());
        statement.setInt(3, clinicianId);
        Debugger.log("Update Clinician Account Settings -> Successful -> Rows Updated: " + statement.executeUpdate());
    }

    public void insertAdmin(Admin admin) throws SQLException {
        String insert = "INSERT INTO " + currentDatabase + ".ADMIN(username, password, name, work_address, region) " +
                "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);

        statement.setString(1, admin.getUsername());
        statement.setString(2, admin.getPassword());
        statement.setString(3, admin.getName());
        statement.setString(4, admin.getWorkAddress());
        statement.setString(5, admin.getRegion());
        Debugger.log("Inserting new Admin -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void updateAdminDetails(Admin admin) throws SQLException {
        String update = "UPDATE " + currentDatabase + ".ADMIN SET name = ?, work_address = ? WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);

        statement.setString(1, admin.getName());
        statement.setString(2, admin.getWorkAddress());
       // statement.setString(3, admin.getegion()); -- No Region for an Admin!
        statement.setString(3, admin.getUsername());
        Debugger.log("Update Admin Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());

    }

    // Now uses API server!
    public APIResponse loginUser(String usernameEmail, String password) {
        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("usernameEmail", usernameEmail);
        queryParameters.put("password", password);
        return server.postRequest(new JsonObject(), queryParameters, "login");
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

        String organsQuery = "SELECT * FROM " + currentDatabase + ".DONATION_LIST_ITEM WHERE user_id = ?";
        PreparedStatement organsStatement = connection.prepareStatement(organsQuery);

        organsStatement.setInt(1, userId);
        ResultSet organsResultSet = organsStatement.executeQuery();

        while(organsResultSet.next()) {
            user.getOrgans().add(Organ.parse(organsResultSet.getString("name")));
        }

        //Get all the medications for the given user

        String medicationsQuery = "SELECT * FROM " + currentDatabase + ".MEDICATION WHERE user_id = ?";
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

        String proceduresQuery = "SELECT * FROM " + currentDatabase + ".PROCEDURES WHERE user_id = ?";
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

        String diseasesQuery = "SELECT * FROM " + currentDatabase + ".DISEASE WHERE user_id = ?";
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
        String waitingListQuery = "SELECT * FROM " + currentDatabase + ".WAITING_LIST_ITEM WHERE user_id = ?";
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

    // Now uses API server!
    public void refreshUserWaitinglists() throws SQLException{
        APIResponse response = server.getRequest(new HashMap<>(),"waitingListItems");
        if(response.isValidJson()){

            //Remove all waiting list items from all users
            for (User user: DataManager.users) {
                user.getWaitingListItems().clear();
            }

            //Add updated waiting list items backto all users
            for(JsonElement item:response.getAsJsonArray()){
                Organ organ = Organ.parse(item.getAsJsonObject().get("organ_type").getAsString());
                LocalDate registeredDate = LocalDate.parse(item.getAsJsonObject().get("organ_registered_date").getAsString());
                LocalDate deregisteredDate = item.getAsJsonObject().get("organ_deregistered_date") != null ? LocalDate.parse(item.getAsJsonObject().get("organ_deregistered_date").getAsString()) : null;
                Long waitinguserId = item.getAsJsonObject().get("user_id").getAsLong();
                Integer deregisteredCode = item.getAsJsonObject().get("deregistered_code") != null ? item.getAsJsonObject().get("deregistered_code").getAsInt() : null;
                Integer waitingListId = item.getAsJsonObject().get("id").getAsInt();

                SearchUtils.getUserById(waitinguserId).getWaitingListItems().add(new ReceiverWaitingListItem(organ, registeredDate, deregisteredDate, waitinguserId, deregisteredCode, waitingListId));

            }
        }
    }

    public void transplantDeregister(ReceiverWaitingListItem waitingListItem) throws SQLException{
        String update = "UPDATE " + currentDatabase + ".WAITING_LIST_ITEM SET organ_deregistered_date = ?, deregistered_code = ? WHERE id = ?";
        PreparedStatement deregisterStatement = connection.prepareStatement(update);

        deregisterStatement.setDate(1, java.sql.Date.valueOf(waitingListItem.getOrganDeregisteredDate()));
        deregisterStatement.setInt(2, waitingListItem.getOrganDeregisteredCode());
        deregisterStatement.setInt(3, waitingListItem.getWaitingListItemId());
        Debugger.log("Update waitinglist Attributes -> Successful -> Rows Updated: " + deregisterStatement.executeUpdate());
        refreshUserWaitinglists();
    }

    public Clinician loginClinician(String username, String password) throws SQLException{
        //First needs to do a search to see if there is a unique clinician with the given inputs
        String query = "SELECT * FROM " + currentDatabase + ".CLINICIAN WHERE username = ? AND password = ?";
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

    public Clinician getClinicianFromId(int id) throws SQLException {
        // SELECT * FROM CLINICIAN id = id;
        String query = "SELECT * FROM " + currentDatabase + ".CLINICIAN WHERE staff_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new Clinician Object with the fields from the database
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
        String query = "SELECT * FROM " + currentDatabase + ".ADMIN WHERE username = ? AND password = ?";
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

    // Now uses API server!
    public List<User> getAllUsers() {
        APIResponse response = server.getRequest(new HashMap<>(),"users");
        if(response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<User>>(){}.getType());
        }else {
            return new ArrayList<User>();
        }
    }

    public ArrayList<Clinician> getAllClinicians() throws SQLException{
        APIResponse response = server.getRequest(new HashMap<>(),"clinicians");
        if(response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Clinician>>(){}.getType());
        }else {
            return new ArrayList<Clinician>();
        }
    }

    public ArrayList<Admin> getAllAdmins() throws SQLException{
        APIResponse response = server.getRequest(new HashMap<>(),"admins");
        if(response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Admin>>(){}.getType());
        }else {
            return new ArrayList<Admin>();
        }
    }

    public void removeUser(User user) throws SQLException {
        String update = "DELETE FROM " + currentDatabase + ".USER WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getUsername());
        Debugger.log("Deletion of User: " + user.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }

    public void removeClinician(Clinician clinician) throws SQLException {
        String update = "DELETE FROM " + currentDatabase + ".CLINICIAN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, clinician.getUsername());
        Debugger.log("Deletion of Clinician: " + clinician.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }

    public void removeAdmin(Admin admin) throws SQLException {
        String update = "DELETE FROM " + currentDatabase + ".ADMIN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, admin.getUsername());
        Debugger.log("Deletion of Admin: " + admin.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }

    public void resetDatabase() throws SQLException{
        String update = "DELETE FROM " + currentDatabase + ".WAITING_LIST_ITEM";
        PreparedStatement statement = connection.prepareStatement(update);
        Debugger.log("Reset of database (WAITING_LIST_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".PROCEDURES";
        statement = connection.prepareStatement(update);
        Debugger.log("Reset of database (PROCEDURE): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".MEDICATION";
        statement = connection.prepareStatement(update);
        Debugger.log("Reset of database (MEDICATION): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".DONATION_LIST_ITEM";
        statement = connection.prepareStatement(update);
        Debugger.log("Reset of database (DONATION_LIST_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".DISEASE";
        statement = connection.prepareStatement(update);
        Debugger.log("Reset of database (DISEASE): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".ADMIN";
        statement = connection.prepareStatement(update);
        Debugger.log("Reset of database (ADMIN): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".CLINICIAN";
        statement = connection.prepareStatement(update);
        Debugger.log("Reset of database (CLINICIAN): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".USER";
        statement = connection.prepareStatement(update);
        Debugger.log("Reset of database (USER): -> Successful -> Rows Removed: " + statement.executeUpdate());


        update = "ALTER TABLE " + currentDatabase + ".USER AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(USER): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".CLINICIAN AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(CLINICIAN): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".ADMIN AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(ADMIN): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".DISEASE AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(DISEASE): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".DONATION_LIST_ITEM AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(DONATION LIST ITEM): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".MEDICATION AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(MEDICATION): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".PROCEDURES AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(PROCEDURES): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".WAITING_LIST_ITEM AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(WAITING LIST ITEM): -> Successful -> " + statement.executeUpdate());

        String insert = "INSERT INTO " + currentDatabase + ".CLINICIAN(username, password, name, work_address, region, staff_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(insert);
        statement.setString(1, "default");
        statement.setString(2, "default");
        statement.setString(3, "default");
        statement.setString(4, "default");
        statement.setString(5, "default");
        statement.setInt(6, 1);
        Debugger.log("Inserting Default Clinician -> Successful -> Rows Added: " + statement.executeUpdate());

        insert = "INSERT INTO " + currentDatabase + ".ADMIN(username, password, name, work_address, region, staff_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(insert);
        statement.setString(1, "admin");
        statement.setString(2, "default");
        statement.setString(3, "default");
        statement.setString(4, "default");
        statement.setString(5, "default");
        statement.setInt(6, 1);
        Debugger.log("Inserting Default Admin -> Successful -> Rows Added: " + statement.executeUpdate());

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

    public ResultSet adminQuery(String query) throws SQLException{
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

    public void connectToDatabase() {
        try{
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(
                    url + connectDatabase, username, password);
            Debugger.log("Connected to " + connectDatabase + " database");
        } catch(Exception e){
            Debugger.log(e);
        }
    }
}