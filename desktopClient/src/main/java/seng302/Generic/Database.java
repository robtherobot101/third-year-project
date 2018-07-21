package seng302.Generic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import seng302.User.*;
import seng302.User.Attribute.Organ;
import seng302.User.Medication.Medication;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        List<Clinician> clinicians = getAllClinicians();
        for(Clinician clinician:clinicians){
            if(clinician.getUsername().equals(username)){
                return (int)clinician.getStaffID();
            }
        }
        return -1;
    }

    public void insertUser(User user) throws SQLException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        userJson.remove("id");

        System.out.println(userJson);
        APIResponse response = server.postRequest(userJson, new HashMap<>(), "users");
        System.out.println(response.getAsString());
    }

    public void updateWaitingListItems(User user){
        int userId = getUserId(user.getUsername());

        //First get rid of all the users waiting list items in the table
        clearUserWaitingListItems(userId);

        for (WaitingListItem item: user.getWaitingListItems()) {

            JsonObject waitingListItemJson = new JsonObject();
            waitingListItemJson.addProperty("organType", item.getOrganType().name());
            waitingListItemJson.add("organRegisteredDate", new Gson().toJsonTree(item.getOrganRegisteredDate(),LocalDate.class));
            waitingListItemJson.add("organDeregisteredDate", new Gson().toJsonTree(item.getOrganDeregisteredDate(),LocalDate.class));
            waitingListItemJson.addProperty("organDeregisteredCode", item.getOrganDeregisteredCode());
            waitingListItemJson.addProperty("userId", item.getUserId());
            System.out.println("Item: " + waitingListItemJson);
            APIResponse res = server.postRequest(waitingListItemJson, new HashMap<String, String>(), "users",String.valueOf(userId), "waitingListItems");
            System.out.println("Response: " + res.getAsString());
        }
    }

    private void clearUserWaitingListItems(int userId){
        APIResponse response = server.getRequest(new HashMap<>(), "users",String.valueOf(userId),"waitingListItems");
        if(response.isValidJson()){
            for(JsonElement itemJson: response.getAsJsonArray()) {
                int itemId = ((JsonObject)itemJson).get("id").getAsInt();
                server.deleteRequest(new HashMap<>(), "users",String.valueOf(userId),"waitingListItems",String.valueOf(itemId));
            }
        }
    }

    //Uses API server for updating attributes
    public void updateUserOrgans(User user) {
        clearUserDonations((int)user.getId());
        insertAllUserDonations(user);
    }

    public void updateUser(User user) {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        APIResponse response = server.patchRequest(userJson, new HashMap<>(), "users",String.valueOf(user.getId()));
        System.out.println(response.getStatusCode());
        assert response.getStatusCode() == 201;
    }

    private void clearUserDonations(int userID) {
        APIResponse response = server.deleteRequest(new HashMap<>(), "users",String.valueOf(userID),"donations");
        System.out.println(response.getStatusCode());
        assert response.getStatusCode() == 201;
    }

    private void insertAllUserDonations(User user) {
        for (Organ organ: user.getOrgans()) {
            APIResponse response = insertUserDonation(user.getId(), organ);
            assert response.getStatusCode() == 201;
        }
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

    public void updateUserDiseases(User user) {
        int userId = getUserId(user.getUsername());

        //Disease Updates
        //First get rid of all the users diseases in the table
        clearUserDiseases(userId);

        for (Disease disease: user.getCuredDiseases()) {
            JsonParser jp = new JsonParser();
            JsonObject diseaseJson = jp.parse(new Gson().toJson(disease)).getAsJsonObject();
            server.postRequest(diseaseJson, new HashMap<String, String>(), "users",String.valueOf(userId), "diseases");
        }

        for (Disease disease: user.getCurrentDiseases()) {
            JsonParser jp = new JsonParser();
            JsonObject diseaseJson = jp.parse(new Gson().toJson(disease)).getAsJsonObject();
            server.postRequest(diseaseJson, new HashMap<String, String>(), "users",String.valueOf(userId), "diseases");
        }
    }

    private void clearUserDiseases(int userID) {
        APIResponse response = server.getRequest(new HashMap<>(), "users",String.valueOf(userID),"diseases");
        if(response.isValidJson()){
            for(JsonElement diseaseJson: response.getAsJsonArray()) {
                int procedureId = ((JsonObject)diseaseJson).get("id").getAsInt();
                server.deleteRequest(new HashMap<>(), "users",String.valueOf(userID),"diseases",String.valueOf(procedureId));
            }
        }
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
        JsonParser jp = new JsonParser();
        JsonObject clinicianJson = jp.parse(new Gson().toJson(clinician)).getAsJsonObject();
        APIResponse response = server.postRequest(clinicianJson, new HashMap<>(), "clinicians");
        System.out.println(response.getStatusCode());
        assert response.getStatusCode() == 201;
    }

    public void updateClinician(Clinician clinician) {
        JsonParser jp = new JsonParser();
        JsonObject clinicianJson = jp.parse(new Gson().toJson(clinician)).getAsJsonObject();
        APIResponse response = server.patchRequest(clinicianJson, new HashMap<>(), "clinicians",String.valueOf(clinician.getStaffID()));
        System.out.println(response.getStatusCode());
        assert response.getStatusCode() == 201;
    }

    public void insertAdmin(Admin admin) throws SQLException {
        JsonParser jp = new JsonParser();
        JsonObject adminJson = jp.parse(new Gson().toJson(admin)).getAsJsonObject();
        APIResponse response = server.postRequest(adminJson, new HashMap<>(), "admins");
        System.out.println(response.getStatusCode());
        assert response.getStatusCode() == 201;
    }

    public void updateAdminDetails(Admin admin) throws SQLException {
        JsonParser jp = new JsonParser();
        JsonObject adminJson = jp.parse(new Gson().toJson(admin)).getAsJsonObject();
        APIResponse response = server.patchRequest(adminJson, new HashMap<>(), "admins", String.valueOf(admin.getStaffID()));
        System.out.println(response.getStatusCode());
        assert response.getStatusCode() == 201;
    }

    // Now uses API server!
    public APIResponse loginUser(String usernameEmail, String password) {
        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("usernameEmail", usernameEmail);
        queryParameters.put("password", password);
        return server.postRequest(new JsonObject(), queryParameters, "login");
    }

    /**
     * Used for searching, takes a hashmap of keyvalue pairs and searches the DB for them.
     * eg. "age", "10" returns all users aged 10.
     * @param searchMap The hashmap with associated key value pairs
     * @return a JSON array of users.
     */
    public APIResponse getUsers(Map<String,String> searchMap) {
        return server.getRequest(searchMap, "users");
    }

    public User getUserFromId(int id) throws SQLException {
        APIResponse response = server.getRequest(new HashMap<>(), "users",String.valueOf(id));
        if(response.isValidJson()){
            return new Gson().fromJson(response.getAsJsonObject(), User.class);
        }
        return null;
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
                Organ organ = Organ.valueOf(item.getAsJsonObject().get("organType").getAsString());
                LocalDate registeredDate = new Gson().fromJson(item.getAsJsonObject().get("organRegisteredDate"), LocalDate.class);
                LocalDate deregisteredDate = new Gson().fromJson(item.getAsJsonObject().get("organDeregisteredDate"), LocalDate.class);
                Long waitinguserId = item.getAsJsonObject().get("userId").getAsLong();
                Integer deregisteredCode = item.getAsJsonObject().get("organDeregisteredCode") != null ? item.getAsJsonObject().get("organDeregisteredCode").getAsInt() : null;
                Integer waitingListId = item.getAsJsonObject().get("id").getAsInt();

                User user = getUserFromId(waitinguserId.intValue());
                SearchUtils.getUserById(waitinguserId).getWaitingListItems().add(new WaitingListItem(user.getName(),user.getRegion(),user.getId(),registeredDate,deregisteredDate,deregisteredCode,organ));
            }
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
            List<User> responses = new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<User>>(){}.getType());

            // Debugging
            for(User u:responses){
                if(u.getId() == 6){
                    System.out.println("Getting all users from database");
                    System.out.println("CurrentState: ");
                    for(WaitingListItem i:u.getWaitingListItems()){
                        System.out.println(i.getOrganType() + "," + i.getStillWaitingOn());
                    }
                }
            }
            //

            return responses;
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
        APIResponse response = server.deleteRequest(new HashMap<>(), "users",String.valueOf(user.getId()));
        assert response.getStatusCode() == 201;
    }

    public void removeClinician(Clinician clinician) throws SQLException {
        APIResponse response = server.deleteRequest(new HashMap<>(), "clinician",String.valueOf(clinician.getStaffID()));
        assert response.getStatusCode() == 201;
    }

    public void removeAdmin(Admin admin) throws SQLException {
        APIResponse response = server.deleteRequest(new HashMap<>(), "admin",String.valueOf(admin.getStaffID()));
        assert response.getStatusCode() == 201;
    }

    public void resetDatabase() throws SQLException{
        APIResponse response = server.postRequest(new JsonObject(),new HashMap<>(), "reset");
        assert response.getStatusCode() == 201;
    }

    public void loadSampleData() throws SQLException {
        APIResponse response = server.postRequest(new JsonObject(),new HashMap<>(), "resample");
        assert response.getStatusCode() == 201;
    }

    public String sendCommand(String command) {
        JsonObject commandObject = new JsonObject();
        commandObject.addProperty("command", command);
        APIResponse response = server.postRequest(commandObject, new HashMap<>(), "cli");
        return response.getAsString();
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