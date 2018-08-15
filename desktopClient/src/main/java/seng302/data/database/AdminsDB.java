package seng302.data.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.HttpResponseException;
import seng302.data.interfaces.AdminsDAO;
import seng302.generic.APIResponse;
import seng302.generic.APIServer;
import seng302.generic.Debugger;
import seng302.User.Admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AdminsDB implements AdminsDAO {
    private final APIServer server;
    private String admins = "admins";

    /**
     * constructor method to create a adminsDB object
     * @param server the api server object used to communicate with
     */
    public AdminsDB(APIServer server) {
        this.server = server;
    }

    public void insertAdmin(Admin admin, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject adminJson = jp.parse(new Gson().toJson(admin)).getAsJsonObject();
        APIResponse response = server.postRequest(adminJson, new HashMap<>(), token, admins);
        if(response == null) return;
        Debugger.log(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public void updateAdminDetails(Admin admin, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject adminJson = jp.parse(new Gson().toJson(admin)).getAsJsonObject();
        APIResponse response = server.patchRequest(adminJson, new HashMap<>(), token, admins, String.valueOf(admin.getStaffID()));
        if(response == null) throw new HttpResponseException(0, "Could not access server");
        Debugger.log(response.getStatusCode());
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    public Collection<Admin> getAllAdmins(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, admins);
        if(response == null){
            return new ArrayList<>();
        }
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<Admin>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Admin getAdmin(long id, String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, admins, String.valueOf(id));
        if(response == null){
            return null;
        }
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
        if(response == null) throw new HttpResponseException(0, "Could not access server");
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }
}