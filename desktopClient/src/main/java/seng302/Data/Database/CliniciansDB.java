package seng302.Data.Database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.CliniciansDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.User.Clinician;
import seng302.Generic.Debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CliniciansDB implements CliniciansDAO {
    private final APIServer server;
    private String clinicians = "clinicians";

    public CliniciansDB(APIServer server) {
        this.server = server;
    }

    /**
     * Calls the server to add a new Clinician
     * @param clinician the Clinician to add
     * @param token the token of the User
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public void insertClinician(Clinician clinician, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject clinicianJson = jp.parse(new Gson().toJson(clinician)).getAsJsonObject();
        APIResponse response = server.postRequest(clinicianJson, new HashMap<>(), token, clinicians);
        if(response == null) return;
        Debugger.log(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * Calls the server to update a Clinician
     * @param clinician the Clinician to update
     * @param token the token of the User
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public void updateClinician(Clinician clinician, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject clinicianJson = jp.parse(new Gson().toJson(clinician)).getAsJsonObject();
        APIResponse response = server.patchRequest(clinicianJson, new HashMap<>(), token, clinicians, String.valueOf(clinician.getStaffID()));
        if(response == null) throw new HttpResponseException(0, "Could not access server");
        Debugger.log(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * gets all the clinicians in the Database
     * @param token the users token
     * @return returns all the clinicians from the server
     */
    public List<Clinician> getAllClinicians(String token) {
        APIResponse response = server.getRequest(new HashMap<>(), token, clinicians);
        if(response == null){
            return new ArrayList<>();
        }
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Clinician>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Removes a Clinician from the server
     * @param id the id of the Clinician to remove
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public void removeClinician(long id, String token) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), token, "Clinician", String.valueOf(id));
        if(response == null) throw new HttpResponseException(0, "Could not access server");
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * gets a specific Clinician
     * @param id the id of the Clinician
     * @param token the users token
     * @return returns the Clinician
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public Clinician getClinician(long id, String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "Clinician", String.valueOf(id));
        if(response == null){
            return null;
        }
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        if(response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonObject(), Clinician.class);
        } else {
            return null;
        }
    }
}