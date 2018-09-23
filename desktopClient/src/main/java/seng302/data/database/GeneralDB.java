package seng302.data.database;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.client.HttpResponseException;
import org.json.JSONObject;
import seng302.User.Attribute.Organ;
import seng302.data.interfaces.GeneralDAO;
import seng302.generic.APIResponse;
import seng302.generic.APIServer;
import seng302.generic.Country;
import seng302.generic.Debugger;
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

    public Boolean checkPassword(String password, long id) throws HttpResponseException {
        Debugger.log("Checking password with server.");
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("password", password);
        queryParameters.put("id", String.valueOf(id));
        APIResponse response = server.postRequest(new JsonObject(), queryParameters, "", "password");
        if(response == null) return false;
        if (response.getStatusCode() == 200) {
            return true;
        }
        return false;
    }

    /**
     * login the user into the program
     * @param identifier the username/email of the user
     * @param password the users password
     * @return returns the response from the server
     */
    public Map<Object, String> loginUser(String identifier, String password) {
        String accountType = "accountType";
        Map<Object, String> responseMap = new HashMap<>();

        Debugger.log("Logging in with server.");
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("usernameEmail", identifier);
        queryParameters.put("password", password);
        APIResponse response = server.postRequest(new JsonObject(), queryParameters, "", "login");
        if(response == null) return responseMap;
        if (response.isValidJson()) {
            JsonObject serverResponse = response.getAsJsonObject();
            if (serverResponse.get(accountType) == null) {
                responseMap.put(new Gson().fromJson(serverResponse, User.class), response.getToken());
                return responseMap;
            } else if (serverResponse.get(accountType).getAsString().equals("CLINICIAN")) {
                responseMap.put(new Gson().fromJson(serverResponse, Clinician.class), response.getToken());
                return responseMap;
            } else if (serverResponse.get(accountType).getAsString().equals("ADMIN")) {
                responseMap.put(new Gson().fromJson(serverResponse, Admin.class), response.getToken());
                return responseMap;
            } else {
                responseMap.put(null, "Username/email/NHI and password combination not recognized.");
                return responseMap;
            }
        } else {
            responseMap.put(null, "Username/email/NHI and password combination not recognized.");
            return responseMap;
        }
    }

    /**
     * logs out the user
     * @param token the users token
     */
    @Override
    public void logoutUser(String token) {
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
     */
    public boolean status() {
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
     */
    public boolean isUniqueIdentifier(String identifier) {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("usernameEmail", identifier);
        APIResponse response = server.getRequest(queryParameters, null, "unique");
        return response != null && response.getAsString().equalsIgnoreCase("true");
    }

    /**
     * get all the waiting list items from the server
     * @param token the users token
     * @return all the waiting list items
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public List<WaitingListItem> getAllWaitingListItems(Map<String, String> params, String token) throws HttpResponseException {
        APIResponse response = server.getRequest(params, token, "waitingListItems");
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
        APIResponse response = server.getRequest(new HashMap<>(), token,"countries");
        if(response == null){
            return new ArrayList<>();
        }
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());

        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Country>>(){}.getType());
        } else {
            return new ArrayList<>();
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
        APIResponse response = server.patchRequest(userJson, new HashMap<>(),token,"countries");
        if(response == null) throw new HttpResponseException(0, "Could not access server");
    }

    /**
     * gets all of the organs that are available to donate from the server
     * @param token the users token
     * @return returns a list of donatableOrgans
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public List<DonatableOrgan> getAllDonatableOrgans(Map filterParams, String token) throws HttpResponseException {
        APIResponse response = server.getRequest(filterParams, token, "organs");
        if (response == null) {
            return new ArrayList<>();
        }
        if (response.getStatusCode() != 200) {
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
        if (response.isValidJson()) {
            List<DonatableOrgan> organs = new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<DonatableOrgan>>(){}.getType());
            for(DonatableOrgan organ : organs) {
                Debugger.log("Top receivers: "+organ.getTopReceivers());
            }
            return organs;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public JSONObject getPosition(String address) throws UnirestException{
        address = address.replace(' ', '+');
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyD7DEH6Klk3ZyduVyqbaVEyTscj4sp48PQ", address);
        JSONObject response = new JSONObject(Unirest.get(url).asString().getBody());
        return response;
    }

    @Override
    public List<Hospital> getHospitals(String token) {
        APIResponse response = server.getRequest(new HashMap<>(), token, "hospitals");
        if(response == null){
            return new ArrayList<>();
        }
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Hospital>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void insertTransfer(OrganTransfer transfer, String token) throws HttpResponseException{
        JsonParser jp = new JsonParser();
        JsonObject jsonTransfer = jp.parse(new Gson().toJson(transfer)).getAsJsonObject();
        APIResponse response = server.postRequest(jsonTransfer, new HashMap<>(), token, "transfer");
        if (response == null) {
            Debugger.log(response.getStatusCode());
        }
        if (response.getStatusCode() != 201){
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
    }

    @Override
    public List<OrganTransfer> getAllOrganTransfers(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "transfer");
        if (response == null) {
            return new ArrayList<>();
        }
        if (response.getStatusCode() != 200){
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
        if (response.isValidJson()) {
            List<OrganTransfer> transfers = new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<OrganTransfer>>(){}.getType());
            return transfers;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void setTransferType(String token, int type, int organ) throws HttpResponseException {
        APIResponse response = server.patchRequest(new JsonObject(), new HashMap<>(), token, "organs/" + organ + "/" + type);
        if (response.getStatusCode() != 201){
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
    }

    @Override
    public void successfullyTransplantWaitingListItem(String token, int organ) throws HttpResponseException {
        APIResponse response = server.patchRequest(new JsonObject(), new HashMap<>(), token, "waitingListItems/" + organ);
        if (response.getStatusCode() != 201){
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
    }

    @Override
    public void deleteTransfer(String token, int organ) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), token, "transfer/" + organ);
        if (response.getStatusCode() != 201){
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
    }

    @Override
    public int getSingleWaitingListItem(String token, long userId, Organ organ) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "users/"+ userId +"/waitingListOrgan/" + organ);
        if (response == null) {
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
        if (response.getStatusCode() != 200){
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
        return Integer.parseInt(response.getAsString());
    }

    @Override
    public List<DonatableOrgan> getSingleUsersDonatableOrgans(String token, long userId) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "organs/" + userId);
        if (response == null) {
            return new ArrayList<>();
        }
        if (response.getStatusCode() != 200) {
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        }
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<DonatableOrgan>>(){}.getType());

        } else {
            return new ArrayList<>();
        }
    }
}