package seng302.Generic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.HttpResponseException;
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

    public int getUserId(String username) throws HttpResponseException {
        for(User user:getAllUsers()){
            if(user.getUsername().equals(username)) {
                return (int)user.getId();
            }
        }
        return -1;
    }

    public void insertUser(User user) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        userJson.remove("id");

        System.out.println(userJson);
        APIResponse response = server.postRequest(userJson, new HashMap<>(), "users");
        System.out.println(response.getAsString());
    }

    public void updateWaitingListItems(User user) throws HttpResponseException{
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
    public void updateUserOrgans(User user) throws HttpResponseException {
        clearUserDonations((int)user.getId());
        insertAllUserDonations(user);
    }

    public void updateUser(User user) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        APIResponse response = server.patchRequest(userJson, new HashMap<>(), "users",String.valueOf(user.getId()));
        System.out.println(response.getStatusCode());
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    private void clearUserDonations(int userID) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), "users",String.valueOf(userID),"donations");
        System.out.println(response.getStatusCode());
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());


    }

    private void insertAllUserDonations(User user) throws HttpResponseException {
        for (Organ organ: user.getOrgans()) {
            APIResponse response = insertUserDonation(user.getId(), organ);
            if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
    }

    private APIResponse insertUserDonation(long userID, Organ organ) {
        JsonObject organJson = new JsonObject();
        organJson.addProperty("name", organ.name());
        return server.postRequest(organJson, new HashMap<>(),"users",String.valueOf(userID),"donations");
    }

    public void updateUserProcedures(User user) throws HttpResponseException {
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

    public void updateUserDiseases(User user) throws HttpResponseException {
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

    public void insertClinician(Clinician clinician) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject clinicianJson = jp.parse(new Gson().toJson(clinician)).getAsJsonObject();
        APIResponse response = server.postRequest(clinicianJson, new HashMap<>(), "clinicians");
        System.out.println(response.getStatusCode());
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void updateClinician(Clinician clinician) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject clinicianJson = jp.parse(new Gson().toJson(clinician)).getAsJsonObject();
        APIResponse response = server.patchRequest(clinicianJson, new HashMap<>(), "clinicians",String.valueOf(clinician.getStaffID()));
        System.out.println(response.getStatusCode());
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void insertAdmin(Admin admin) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject adminJson = jp.parse(new Gson().toJson(admin)).getAsJsonObject();
        APIResponse response = server.postRequest(adminJson, new HashMap<>(), "admins");
        System.out.println(response.getStatusCode());
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void updateAdminDetails(Admin admin) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject adminJson = jp.parse(new Gson().toJson(admin)).getAsJsonObject();
        APIResponse response = server.patchRequest(adminJson, new HashMap<>(), "admins", String.valueOf(admin.getStaffID()));
        System.out.println(response.getStatusCode());
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    // Now uses API server!
    public APIResponse loginUser(String usernameEmail, String password) {
        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("usernameEmail", usernameEmail);
        queryParameters.put("password", password);
        return server.postRequest(new JsonObject(), queryParameters, "login");
    }

    public boolean isUniqueUser(String username) throws HttpResponseException {
        // TODO Add functionality to GET api/v1/users which allows searching for users by username, then implement this method
        return true;
    }

    /**
     * Used for searching, takes a hashmap of keyvalue pairs and searches the DB for them.
     * eg. "age", "10" returns all users aged 10.
     * @param searchMap The hashmap with associated key value pairs
     * @return a JSON array of users.
     */
    public APIResponse getUsers(Map<String,String> searchMap) throws HttpResponseException {
        return server.getRequest(searchMap, "users");
    }

    public User getUserFromId(int id) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), "users",String.valueOf(id));
        if(response.isValidJson()){
            return new Gson().fromJson(response.getAsJsonObject(), User.class);
        }
        return null;
    }

    // Now uses API server!
    public void refreshUserWaitinglists() throws HttpResponseException {
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

    // Now uses API server!
    public List<User> getAllUsers() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(),"users");
        if(response.isValidJson()) {
            List<User> responses = new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<User>>(){}.getType());
            return responses;
        }else {
            return new ArrayList<User>();
        }
    }

    public ArrayList<Clinician> getAllClinicians() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(),"clinicians");
        if(response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Clinician>>(){}.getType());
        }else {
            return new ArrayList<Clinician>();
        }
    }

    public ArrayList<Admin> getAllAdmins() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(),"admins");
        if(response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Admin>>(){}.getType());
        }else {
            return new ArrayList<Admin>();
        }
    }

    public void removeUser(User user) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), "users",String.valueOf(user.getId()));
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void removeClinician(Clinician clinician) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), "clinician",String.valueOf(clinician.getStaffID()));
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void removeAdmin(Admin admin) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), "admin",String.valueOf(admin.getStaffID()));
        if(response.getStatusCode() != 201) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void resetDatabase() throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(),new HashMap<>(), "reset");
        if(response.getStatusCode() != 200) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void loadSampleData() throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(),new HashMap<>(), "resample");
        if(response.getStatusCode() != 200) throw new HttpResponseException(response.getStatusCode(), response.getAsString());
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