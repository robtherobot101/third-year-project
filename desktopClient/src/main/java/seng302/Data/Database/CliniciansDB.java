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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CliniciansDB implements CliniciansDAO {
    private final APIServer server;

    public CliniciansDB(APIServer server) {
        this.server = server;
    }

    /**
     * Calls the server to add a new clinician
     * @param clinician the clinician to add
     * @param token the token of the user
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public void insertClinician(Clinician clinician, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject clinicianJson = jp.parse(new Gson().toJson(clinician)).getAsJsonObject();
        APIResponse response = server.postRequest(clinicianJson, new HashMap<>(), token, "clinicians");
        System.out.println(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * Calls the server to update a clinician
     * @param clinician the clinician to update
     * @param token the token of the user
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public void updateClinician(Clinician clinician, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject clinicianJson = jp.parse(new Gson().toJson(clinician)).getAsJsonObject();
        APIResponse response = server.patchRequest(clinicianJson, new HashMap<>(), token, "clinicians", String.valueOf(clinician.getStaffID()));
        System.out.println(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * gets all the clinicians in the database
     * @param token the users token
     * @return returns all the clinicians from the server
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public ArrayList<Clinician> getAllClinicians(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "clinicians");
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Clinician>>() {
            }.getType());
        } else {
            return new ArrayList<Clinician>();
        }
    }

    /**
     * Removes a clinician from the server
     * @param id the id of the clinician to remove
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public void removeClinician(long id, String token) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), token, "clinician", String.valueOf(id));
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * gets a specific clinician
     * @param id the id of the clinician
     * @param token the users token
     * @return returns the clinician
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public Clinician getClinician(long id, String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "clinician", String.valueOf(id));
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        if(response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonObject(), Clinician.class);
        } else {
            return null;
        }
    }
}