package seng302.Data.Database;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.Generic.Country;
import seng302.Generic.Debugger;
import seng302.User.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralDB implements GeneralDAO {
    private final APIServer server;

    public GeneralDB(APIServer server) {
        this.server = server;
    }

    /**
     * login the user into the program
     * @param usernameEmail the username/email of the user
     * @param password the users password
     * @return returns the response from the server
     */
    public Map<Object, String> loginUser(String usernameEmail, String password) {
        Map<Object, String> responseMap = new HashMap<>();

        Debugger.log("Logging in with server.");
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("usernameEmail", usernameEmail);
        queryParameters.put("password", password);
        APIResponse response = server.postRequest(new JsonObject(), queryParameters, "", "login");
        if(response == null) return responseMap;
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

    /**
     * logs out the user
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public void logoutUser(String token) throws HttpResponseException {
        server.postRequest(new JsonObject(), new HashMap<>(), token, "logout");
    }

    /**
     * calls the server to reset the database
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public void reset(String token) throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(), new HashMap<>(), token, "reset");
        if(response == null) throw new HttpResponseException(0, "Could not acccess database");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * calls the server to resample the database
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public void resample(String token) throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(), new HashMap<>(), token, "resample");
        if(response == null) throw new HttpResponseException(0, "Could not acccess database");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * pings the server to check if reachable
     * @return returns true if the server can be reached
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public boolean status() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), null,"status");
        return response != null && response.getAsString().equals("DATABASE ONLINE");
    }

    /**
     * sends a command to the sever
     * @param command the command to be sent
     * @param token the users token
     * @return return the response from the server
     */
    public String sendCommand(String command, String token) {
        JsonObject commandObject = new JsonObject();
        commandObject.addProperty("command", command);
        APIResponse response = server.postRequest(commandObject, new HashMap<>(), token, "cli");
        if(response == null) return null;
        else return response.getAsString();
    }

    /**
     * checks if the username or email is unique
     * @param identifier the string to check
     * @return returns true if unique, otherwise false
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public boolean isUniqueIdentifier(String identifier) throws HttpResponseException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("usernameEmail", identifier);
        APIResponse response = server.getRequest(queryParameters, null, "unique");
        if(response == null){
            return false;
        }
        return response.getAsString().equalsIgnoreCase("true");
    }

    /**
     * get all the waiting list items from the server
     * @param token the users token
     * @return all the waiting list items
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public List<WaitingListItem> getAllWaitingListItems(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "waitingListItems");
        if(response == null){
            return new ArrayList<>();
        }
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());

        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<WaitingListItem>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * gets all the countries from the server
     * @param token the users token
     * @return returns all the countries
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public List<Country> getAllCountries(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), token,"countries");
        if(response == null){
            return new ArrayList<Country>();
        }
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());

        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Country>>(){}.getType());
        } else {
            return new ArrayList<Country>();
        }
    }

    /**
     * updates all the countries in the server
     * @param countries the list of countries to update to
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public void updateCountries(List<Country> countries, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonArray userJson = jp.parse(new Gson().toJson(countries)).getAsJsonArray();
        APIResponse response = server.patchRequest(userJson, new HashMap<String, String>(),token,"countries");
        if(response == null) throw new HttpResponseException(0, "Could not access server");
    }

    /**
     * gets all of the organs that are available to donate from the server
     * @param token the users token
     * @return returns a list of donatableOrgans
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public List<DonatableOrgan> getAllDonatableOrgans(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "organs");
        if (response == null) {
            return new ArrayList<DonatableOrgan>();
        }
        if (response.getStatusCode() != 200) {
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<DonatableOrgan>>(){}.getType());
        } else {
            return new ArrayList<DonatableOrgan>();
        }
    }
}