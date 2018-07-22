package seng302.Data.Local;

import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.AdminsDAO;
import seng302.Generic.Debugger;
import seng302.User.Admin;
import seng302.User.Clinician;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AdminsM implements AdminsDAO {

    private List<Admin> admins;

    public AdminsM() {
        this.admins = new ArrayList<>();
    }

    @Override
    public void insertAdmin(Admin admin) {
        long nextAdminId = 0;
        for (Admin a : admins) {
            if (a.getStaffID() > nextAdminId) {
                nextAdminId = a.getStaffID();
            }
        }
        admin.setStaffID(nextAdminId + 1);
        admins.add(admin);
    }

    @Override
    public void updateAdminDetails(Admin admin) {
        removeAdmin(admin.getStaffID());
        admins.add(admin);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return admins;
    }

    @Override
    public Admin getAdmin(long id) {
        for(Admin a : admins) {
            if(a.getStaffID() == id) {
                return a;
            }
        }
        Debugger.log("Admin with id: " + id + " not found. Returning null.");
        return null;
    }

    @Override
    public void removeAdmin(long id) {
        for(Admin a : admins) {
            if(a.getStaffID() == id) {
                admins.remove(a);
                break;
            }
        }
    }
}
