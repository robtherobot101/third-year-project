package seng302.Data.Local;

import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.AdminsDAO;
import seng302.Data.Interfaces.CliniciansDAO;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Data.ResampleData;
import seng302.Generic.Debugger;
import seng302.Generic.Country;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralM implements GeneralDAO {

    private UsersDAO users;
    private CliniciansDAO clinicians;
    private AdminsDAO admins;
    private GeneralDAO general;

    public GeneralM(UsersDAO users, CliniciansDAO clinicians, AdminsDAO admins) {
        this.users = users;
        this.clinicians = clinicians;
        this.admins = admins;
        this.reset(null);
        this.resample(null);
    }

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
    public void logoutUser(String token) throws HttpResponseException {
        //No need to log out from local test version
    }

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

        }
    }

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

        }
    }

    @Override
    public String sendCommand(String command, String token) {
        return null;
    }

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

    @Override
    public boolean isUniqueIdentifier(String usernameEmail, long userId) throws HttpResponseException {
        for(User u : users.getAllUsers(null)) {
            if(u.getId() != userId && (u.getEmail().equals(usernameEmail) || u.getUsername().equals(usernameEmail))) {
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

    public List<WaitingListItem> getAllWaitingListItems(String token) throws HttpResponseException {
        List<WaitingListItem> items = new ArrayList<>();
        for(User u : users.getAllUsers(null)) {
            for(WaitingListItem i : u.getWaitingListItems()) {
                items.add(i);
            }
        }
        return items;
    }

    @Override
    public boolean status() throws HttpResponseException {
        return true;
    }

    public List<Country> getAllCountries(String token) throws HttpResponseException {
        return null;
    }

    public void updateCountries(List<Country> countries, String token) throws HttpResponseException{}
}
