package seng302.Data.Database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.Generic.Country;
import seng302.Generic.Debugger;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralDB implements GeneralDAO {
    private final APIServer server;

    public GeneralDB(APIServer server) {
        this.server = server;
    }

    public Map<Object, String> loginUser(String usernameEmail, String password) {
        Map<Object, String> responseMap = new HashMap<>();

        Debugger.log("Logging in with server.");
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("usernameEmail", usernameEmail);
        queryParameters.put("password", password);
        APIResponse response = server.postRequest(new JsonObject(), queryParameters, "", "login");
        if (response.isValidJson()) {
            JsonObject serverResponse = response.getAsJsonObject();
            if (serverResponse.get("accountType") == null) {
                responseMap.put(new Gson().fromJson(serverResponse, User.class), response.getToken());
                return responseMap;
            } else if (serverResponse.get("accountType").getAsString().equals("CLINICIAN")) {
                responseMap.put(new Gson().fromJson(serverResponse, Clinician.class), response.getToken());
                return responseMap;
            } else if (serverResponse.get("accountType").getAsString().equals("ADMIN")) {
                responseMap.put(new Gson().fromJson(serverResponse, Admin.class), response.getToken());
                return responseMap;
            } else {
                responseMap.put(null, "Username/email and password combination not recognized.");
                return responseMap;
            }
        } else {
            responseMap.put(null, "Username/email and password combination not recognized.");
            return responseMap;
        }
    }

    @Override
    public void logoutUser(String token) throws HttpResponseException {
        server.postRequest(new JsonObject(), new HashMap<>(), token, "logout");
    }

    public void reset(String token) throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(), new HashMap<>(), token, "reset");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void resample(String token) throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(), new HashMap<>(), token, "resample");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public boolean status() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "status");
        if (response.getAsString().equals("DATABASE ONLINE")) {
            return true;
        } else {
            return false;
        }
    }

    public String sendCommand(String command, String token) {
        JsonObject commandObject = new JsonObject();
        commandObject.addProperty("command", command);
        APIResponse response = server.postRequest(commandObject, new HashMap<>(), token, "cli");
        return response.getAsString();
    }

    public boolean isUniqueIdentifier(String username) throws HttpResponseException {
        // TODO - Implement this to check for the same username/email over all profile types
        return true;
    }

    public boolean isUniqueIdentifier(String username, long userId) throws HttpResponseException {
        // TODO - Implement this to check for the same username/email over all profile types
        return true;
    }

    @Override
    public List<WaitingListItem> getAllWaitingListItems(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "waitingListItems");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());

        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<WaitingListItem>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Country> getAllCountries() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "countries");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());

        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Country>>(){}.getType());
        } else {
            return new ArrayList<Country>();
        }
    }

    @Override
    public void updateCountries(List<Country> countries) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(countries)).getAsJsonObject();
        APIResponse response = server.patchRequest(userJson, new HashMap<String, String>(),"countries");
    }
}