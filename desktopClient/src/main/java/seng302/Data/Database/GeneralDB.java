package seng302.Data.Database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralDB implements GeneralDAO {
    private final APIServer server;

    public GeneralDB(APIServer server) {
        this.server = server;
    }

    public Object loginUser(String usernameEmail, String password) {

        Debugger.log("Logging in with server.");
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("usernameEmail", usernameEmail);
        queryParameters.put("password", password);
        APIResponse response = server.postRequest(new JsonObject(), queryParameters, "login");
        if (response.isValidJson()) {
            JsonObject serverResponse = response.getAsJsonObject();
            if (serverResponse.get("accountType") == null) {
                return new Gson().fromJson(serverResponse, User.class);
            } else if (serverResponse.get("accountType").getAsString().equals("CLINICIAN")) {
                return new Gson().fromJson(serverResponse, Clinician.class);
            } else if (serverResponse.get("accountType").getAsString().equals("ADMIN")) {
                return new Gson().fromJson(serverResponse, Admin.class);
            } else {
                return "Username/email and password combination not recognized.";
            }
        } else {
            return "Username/email and password combination not recognized.";
        }
    }

    public void reset() throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(), new HashMap<String, String>(), "reset");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void resample() throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(), new HashMap<String, String>(), "resample");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public String sendCommand(String command) {
        JsonObject commandObject = new JsonObject();
        commandObject.addProperty("command", command);
        APIResponse response = server.postRequest(commandObject, new HashMap<String, String>(), "cli");
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
    public List<WaitingListItem> getAllWaitingListItems() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "waitingListItems");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());

        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<WaitingListItem>>() {
            }.getType());
        } else {
            return new ArrayList<WaitingListItem>();
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
}