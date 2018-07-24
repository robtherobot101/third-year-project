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

    /**
     * Adds a new admin
     * @param admin The admin which will be added
     */
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

    /**
     * Updates the given admin. The old admin with a matching ID is replaced.
     * @param admin The admin which will replace the one with a matching ID
     */
    @Override
    public void updateAdminDetails(Admin admin) {
        removeAdmin(admin.getStaffID());
        admins.add(admin);
    }

    /**
     * Returns all admins
     * @return A List of all the admins
     */
    @Override
    public List<Admin> getAllAdmins() {
        return admins;
    }

    /**
     * Returns the admin with the given ID. If no such admin exists, null is returned.
     * If multiple admins have the given ID, the first one found is returned.
     *
     * @param id The id of the requested admin.
     * @return The admin which has the same ID
     */
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

    /**
     * Removes the admin with the given ID if it exists.
     * If multiple admins have the given ID, the first one found is removed.
     *
     * @param id The id of the admin which will be removed.
     */
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
