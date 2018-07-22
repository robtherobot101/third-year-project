package seng302.Data.Database;

import com.google.gson.JsonObject;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.Generic.Debugger;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class GeneralDB implements GeneralDAO {
    private final APIServer server;

    public GeneralDB(APIServer server) {
        this.server = server;
    }

    public APIResponse loginUser(String usernameEmail, String password) {
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("usernameEmail", usernameEmail);
        queryParameters.put("password", password);
        return server.postRequest(new JsonObject(), queryParameters, "login");
    }

    public void resetDatabase() throws HttpResponseException {
        APIResponse response = server.postRequest(new JsonObject(), new HashMap<String, String>(), "reset");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void loadSampleData() throws HttpResponseException {
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


}