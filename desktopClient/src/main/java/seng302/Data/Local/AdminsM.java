package seng302.Data.Local;

import seng302.Data.Interfaces.AdminsDAO;
import seng302.Generic.Debugger;
import seng302.User.Admin;

import java.util.ArrayList;
import java.util.List;

public class AdminsM implements AdminsDAO {

    private List<Admin> admins;

    /**
     * constructor method to create a new adminsM object used for Local storage
     */
    public AdminsM() {
        this.admins = new ArrayList<>();
    }

    /**
     * Adds a new Admin
     * @param admin The Admin which will be added
     */
    @Override
    public void insertAdmin(Admin admin, String token) {
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
     * Updates the given Admin. The old Admin with a matching ID is replaced.
     * @param admin The Admin which will replace the one with a matching ID
     */
    @Override
    public void updateAdminDetails(Admin admin, String token) {
        removeAdmin(admin.getStaffID(), null);
        admins.add(admin);
    }

    /**
     * Returns all admins
     * @return A List of all the admins
     */
    @Override
    public List<Admin> getAllAdmins(String token) {
        return admins;
    }

    /**
     * Returns the Admin with the given ID. If no such Admin exists, null is returned.
     * If multiple admins have the given ID, the first one found is returned.
     *
     * @param id The id of the requested Admin.
     * @return The Admin which has the same ID
     */
    @Override
    public Admin getAdmin(long id, String token) {
        for(Admin a : admins) {
            if(a.getStaffID() == id) {
                return a;
            }
        }
        Debugger.log("Admin with id: " + id + " not found. Returning null.");
        return null;
    }

    /**
     * Removes the Admin with the given ID if it exists.
     * If multiple admins have the given ID, the first one found is removed.
     *
     * @param id The id of the Admin which will be removed.
     */
    @Override
    public void removeAdmin(long id, String token) {
        for(Admin a : admins) {
            if(a.getStaffID() == id) {
                admins.remove(a);
                break;
            }
        }
    }
}
