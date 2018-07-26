package seng302.Data.Database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.AdminsDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.User.Admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AdminsDB implements AdminsDAO {
    private final APIServer server;

    public AdminsDB(APIServer server) {
        this.server = server;
    }

    public void insertAdmin(Admin admin, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject adminJson = jp.parse(new Gson().toJson(admin)).getAsJsonObject();
        APIResponse response = server.postRequest(adminJson, new HashMap<>(), token, "admins");
        System.out.println(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void updateAdminDetails(Admin admin, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject adminJson = jp.parse(new Gson().toJson(admin)).getAsJsonObject();
        APIResponse response = server.patchRequest(adminJson, new HashMap<>(), token, "admins", String.valueOf(admin.getStaffID()));
        System.out.println(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public Collection<Admin> getAllAdmins(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "admins");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Admin>>() {
            }.getType());
        } else {
            return new ArrayList<Admin>();
        }
    }

    public Admin getAdmin(long id, String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "admins", String.valueOf(id));
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        if(response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonObject(), Admin.class);
        } else {
            return null;
        }
    }


    public void removeAdmin(long id, String token) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), token,"admin", String.valueOf(id));
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }
}