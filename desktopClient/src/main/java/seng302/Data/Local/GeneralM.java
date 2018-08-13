package seng302.Data.Local;

import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.AdminsDAO;
import seng302.Data.Interfaces.CliniciansDAO;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Data.ResampleData;
import seng302.Generic.Debugger;
import seng302.Generic.Country;
import seng302.User.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralM implements GeneralDAO {

    private UsersDAO users;
    private CliniciansDAO clinicians;
    private AdminsDAO admins;

    public GeneralM(UsersDAO users, CliniciansDAO clinicians, AdminsDAO admins) {
        this.users = users;
        this.clinicians = clinicians;
        this.admins = admins;
        this.reset(null);
        this.resample(null);
    }

    /**
     * logs a user in
     * @param usernameEmail the username or email of the user
     * @param password the users password
     * @return the user profile
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public Map<Object, String> loginUser(String usernameEmail, String password) throws HttpResponseException {
        Map<Object, String> response = new HashMap<>();
        for(User u : users.getAllUsers(null)) {
            try {
                if(u.getEmail().equals(usernameEmail) && u.getPassword().equals(password) || u.getUsername().equals(usernameEmail) && u.getPassword().equals(password)) {
                    response.put(u, null);
                    return response;
                }
            } catch (NullPointerException e) {
                Debugger.error("NullPointerException while trying to login with user: " + u);
            }
        }

        for(Clinician c : clinicians.getAllClinicians(null)) {
            if(c.getUsername().equals(usernameEmail) && c.getPassword().equals(password)) {
                response.put(c, null);
                return response;
            }
        }

        for(Admin a : admins.getAllAdmins(null)) {
            if(a.getUsername().equals(usernameEmail) && a.getPassword().equals(password)) {
                response.put(a, null);
                return response;
            }
        }
        return null;
    }

    @Override
    public void logoutUser(String token) {
        //No need to log out from local test version
    }

    /**
     * resets the local data
     * @param token the users token
     */
    @Override
    public void reset(String token) {
        try {
            for(User u : new ArrayList<>(users.getAllUsers(null))) {
                users.removeUser(u.getId(), null);
            }
            for(Clinician c : new ArrayList<>(clinicians.getAllClinicians(null))) {
                clinicians.removeClinician(c.getStaffID(), null);
            }
            for(Admin a : new ArrayList<>(admins.getAllAdmins(null))) {
                admins.removeAdmin(a.getStaffID(), null);
            }

        } catch (HttpResponseException e) {
            Debugger.error(e.getStackTrace());
        }
    }

    /**
     * resamples the local data
     * @param token authentication token for the database
     */
    @Override
    public void resample(String token) {
        try {
            for(User u : ResampleData.users()) {
                users.insertUser(u);
            }
            for(Clinician c : ResampleData.clinicians()) {
                clinicians.insertClinician(c, null);
            }
            for(Admin a : ResampleData.admins()) {
                admins.insertAdmin(a, null);
            }

        } catch (HttpResponseException e) {
            Debugger.error(e.getStackTrace());
        }
    }

    @Override
    public String sendCommand(String command, String token) {
        return null;
    }

    /**
     * checks if the username or email is unique
     * @param usernameEmail the username or email
     * @return returns true if unique, otherwise false
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public boolean isUniqueIdentifier(String usernameEmail) throws HttpResponseException {
        for(User u : users.getAllUsers(null)) {
            if(u.getEmail().equals(usernameEmail) || u.getUsername().equals(usernameEmail)) {
                return false;
            }
        }

        for(Clinician c : clinicians.getAllClinicians(null)) {
            if(c.getUsername().equals(usernameEmail)) {
                return false;
            }
        }

        for(Admin a : admins.getAllAdmins(null)) {
            if(a.getUsername().equals(usernameEmail)) {
                return false;
            }
        }
        return true;
    }


    /**
     * gets all the waiting list items
     * @param token the users token
     * @return returns all waiting list items
     * @throws HttpResponseException throws if cannot connect to the server
     */
    public List<WaitingListItem> getAllWaitingListItems(Map<String, String> params, String token) throws HttpResponseException {
        List<WaitingListItem> items = new ArrayList<>();
        for(User u : users.getAllUsers(null)) {
            items.addAll(u.getWaitingListItems());
        }
        return items;
    }

    @Override
    public boolean status() {
        return true;
    }

    public List<Country> getAllCountries(String token) {
        return null;
    }

    public void updateCountries(List<Country> countries, String token) {}

    @Override
    public List<DonatableOrgan> getAllDonatableOrgans(HashMap filterParams, String Token) {
        return null;
    }
}
