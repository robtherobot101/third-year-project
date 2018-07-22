package seng302.Data.Database;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.Generic.DataManager;
import seng302.User.Attribute.Organ;
import seng302.User.Disease;
import seng302.User.Procedure;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersDB implements UsersDAO {
    private final APIServer server;

    public UsersDB(APIServer server) {
        this.server = server;
    }

    @Override
    public int getUserId(String username) throws HttpResponseException {
        for (User user : getAllUsers()) {
            if (user.getUsername().equals(username)) {
                return (int) user.getId();
            }
        }
        return -1;
    }

    @Override
    public void insertUser(User user) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        userJson.remove("id");
        APIResponse response = server.postRequest(userJson, new HashMap<String, String>(), "users");
    }

    @Override
    public void updateWaitingListItems(User user) throws HttpResponseException {
        int userId = getUserId(user.getUsername());

        //First get rid of all the users waiting list items in the table
        clearUserWaitingListItems(userId);

        for (WaitingListItem item : user.getWaitingListItems()) {

            JsonObject waitingListItemJson = new JsonObject();
            waitingListItemJson.addProperty("organType", item.getOrganType().name());
            waitingListItemJson.add("organRegisteredDate", new Gson().toJsonTree(item.getOrganRegisteredDate(), LocalDate.class));
            waitingListItemJson.add("organDeregisteredDate", new Gson().toJsonTree(item.getOrganDeregisteredDate(), LocalDate.class));
            waitingListItemJson.addProperty("organDeregisteredCode", item.getOrganDeregisteredCode());
            waitingListItemJson.addProperty("userId", item.getUserId());
            System.out.println("Item: " + waitingListItemJson);
            APIResponse res = server.postRequest(waitingListItemJson, new HashMap<String, String>(), "users", String.valueOf(userId), "waitingListItems");
            System.out.println("Response: " + res.getAsString());
        }
    }

    void clearUserWaitingListItems(int userId) {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "users", String.valueOf(userId), "waitingListItems");
        if (response.isValidJson()) {
            for (JsonElement itemJson : response.getAsJsonArray()) {
                int itemId = ((JsonObject) itemJson).get("id").getAsInt();
                server.deleteRequest(new HashMap<String, String>(), "users", String.valueOf(userId), "waitingListItems", String.valueOf(itemId));
            }
        }
    }//Uses API server for updating attributes

    @Override
    public void updateUserOrgans(User user) throws HttpResponseException {
        clearUserDonations((int) user.getId());
        insertAllUserDonations(user);
    }

    @Override
    public void updateUser(User user) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        APIResponse response = server.patchRequest(userJson, new HashMap<String, String>(), "users", String.valueOf(user.getId()));
        System.out.println(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    void clearUserDonations(int userID) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<String, String>(), "users", String.valueOf(userID), "donations");
        System.out.println(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());


    }

    void insertAllUserDonations(User user) throws HttpResponseException {
        for (Organ organ : user.getOrgans()) {
            APIResponse response = insertUserDonation(user.getId(), organ);
            if (response.getStatusCode() != 201)
                throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
    }

    APIResponse insertUserDonation(long userID, Organ organ) {
        JsonObject organJson = new JsonObject();
        organJson.addProperty("name", organ.name());
        return server.postRequest(organJson, new HashMap<String, String>(), "users", String.valueOf(userID), "donations");
    }

    @Override
    public void updateUserProcedures(User user) throws HttpResponseException {
        int userId = getUserId(user.getUsername());

        //Procedure Updates
        //First get rid of all the users procedures in the table
        clearUserProcedures(userId);

        for (Procedure procedure : user.getPendingProcedures()) {
            JsonParser jp = new JsonParser();
            JsonObject procedureJson = jp.parse(new Gson().toJson(procedure)).getAsJsonObject();
            server.postRequest(procedureJson, new HashMap<String, String>(), "users", String.valueOf(userId), "procedures");
        }

        for (Procedure procedure : user.getPreviousProcedures()) {
            JsonParser jp = new JsonParser();
            JsonObject procedureJson = jp.parse(new Gson().toJson(procedure)).getAsJsonObject();
            server.postRequest(procedureJson, new HashMap<String, String>(), "users", String.valueOf(userId), "procedures");
        }
    }

    void clearUserProcedures(int userID) {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "users", String.valueOf(userID), "procedures");
        if (response.isValidJson()) {
            for (JsonElement procedureJson : response.getAsJsonArray()) {
                int procedureId = ((JsonObject) procedureJson).get("id").getAsInt();
                server.deleteRequest(new HashMap<String, String>(), "users", String.valueOf(userID), "procedures", String.valueOf(procedureId));
            }
        }
    }

    @Override
    public void updateUserDiseases(User user) throws HttpResponseException {
        int userId = getUserId(user.getUsername());

        //Disease Updates
        //First get rid of all the users diseases in the table
        clearUserDiseases(userId);

        for (Disease disease : user.getCuredDiseases()) {
            JsonParser jp = new JsonParser();
            JsonObject diseaseJson = jp.parse(new Gson().toJson(disease)).getAsJsonObject();
            server.postRequest(diseaseJson, new HashMap<String, String>(), "users", String.valueOf(userId), "diseases");
        }

        for (Disease disease : user.getCurrentDiseases()) {
            JsonParser jp = new JsonParser();
            JsonObject diseaseJson = jp.parse(new Gson().toJson(disease)).getAsJsonObject();
            server.postRequest(diseaseJson, new HashMap<String, String>(), "users", String.valueOf(userId), "diseases");
        }
    }

    void clearUserDiseases(int userID) {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "users", String.valueOf(userID), "diseases");
        if (response.isValidJson()) {
            for (JsonElement diseaseJson : response.getAsJsonArray()) {
                int procedureId = ((JsonObject) diseaseJson).get("id").getAsInt();
                server.deleteRequest(new HashMap<String, String>(), "users", String.valueOf(userID), "diseases", String.valueOf(procedureId));
            }
        }
    }

    /**
     * Used for searching, takes a hashmap of keyvalue pairs and searches the DB for them.
     * eg. "age", "10" returns all users aged 10.
     *
     * @param searchMap The hashmap with associated key value pairs
     * @return a JSON array of users.
     */
    @Override
    public APIResponse getUsers(Map<String, String> searchMap) throws HttpResponseException {
        return server.getRequest(searchMap, "users");
    }

    @Override
    public User getUserFromId(int id) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "users", String.valueOf(id));
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonObject(), User.class);
        }
        return null;
    }// Now uses API server!

    @Override
    public List<User> getAllUsers() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "users");
        if (response.isValidJson()) {
            List<User> responses = new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<User>>() {
            }.getType());
            return responses;
        } else {
            return new ArrayList<User>();
        }
    }

    @Override
    public void removeUser(User user) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<String, String>(), "users", String.valueOf(user.getId()));
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }
}