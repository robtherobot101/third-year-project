package seng302.Data.Local;

import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.AdminsDAO;
import seng302.Data.Interfaces.CliniciansDAO;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Data.ResampleData;
import seng302.Generic.APIResponse;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.util.ArrayList;
import java.util.List;

public class GeneralM implements GeneralDAO {

    private UsersDAO users;
    private CliniciansDAO clinicians;
    private AdminsDAO admins;
    private GeneralDAO general;

    public GeneralM(UsersDAO users, CliniciansDAO clinicians, AdminsDAO admins) {
        this.users = users;
        this.clinicians = clinicians;
        this.admins = admins;
        this.reset();
        this.resample();
    }

    @Override
    public Object loginUser(String usernameEmail, String password) throws HttpResponseException {
        for(User u : users.getAllUsers()) {
            if(u.getEmail().equals(usernameEmail) && u.getPassword().equals(password) || u.getUsername().equals(usernameEmail) && u.getPassword().equals(password)) {
                return u;
            }
        }

        for(Clinician c : clinicians.getAllClinicians()) {
            if(c.getUsername().equals(usernameEmail) && c.getPassword().equals(password)) {
                return c;
            }
        }

        for(Admin a : admins.getAllAdmins()) {
            if(a.getUsername().equals(usernameEmail) && a.getPassword().equals(password)) {
                return a;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        try {
            for(User u : new ArrayList<>(users.getAllUsers())) {
                users.removeUser(u.getId());
            }
            for(Clinician c : new ArrayList<>(clinicians.getAllClinicians())) {
                clinicians.removeClinician(c.getStaffID());
            }
            for(Admin a : new ArrayList<>(admins.getAllAdmins())) {
                admins.removeAdmin(a.getStaffID());
            }

        } catch (HttpResponseException e) {

        }
    }

    @Override
    public void resample() {
        try {
            for(User u : ResampleData.users()) {
                users.insertUser(u);
            }
            for(Clinician c : ResampleData.clinicians()) {
                clinicians.insertClinician(c);
            }
            for(Admin a : ResampleData.admins()) {
                admins.insertAdmin(a);
            }

        } catch (HttpResponseException e) {

        }
    }

    @Override
    public String sendCommand(String command) {
        return null;
    }

    @Override
    public boolean isUniqueIdentifier(String usernameEmail) throws HttpResponseException {
        for(User u : users.getAllUsers()) {
            if(u.getEmail().equals(usernameEmail) || u.getUsername().equals(usernameEmail)) {
                return false;
            }
        }

        for(Clinician c : clinicians.getAllClinicians()) {
            if(c.getUsername().equals(usernameEmail)) {
                return false;
            }
        }

        for(Admin a : admins.getAllAdmins()) {
            if(a.getUsername().equals(usernameEmail)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isUniqueIdentifier(String usernameEmail, long userId) throws HttpResponseException {
        for(User u : users.getAllUsers()) {
            if(u.getId() != userId && (u.getEmail().equals(usernameEmail) || u.getUsername().equals(usernameEmail))) {
                return false;
            }
        }

        for(Clinician c : clinicians.getAllClinicians()) {
            if(c.getUsername().equals(usernameEmail)) {
                return false;
            }
        }

        for(Admin a : admins.getAllAdmins()) {
            if(a.getUsername().equals(usernameEmail)) {
                return false;
            }
        }
        return true;
    }

    public List<WaitingListItem> getAllWaitingListItems() throws HttpResponseException {
        return null;
    }
}
