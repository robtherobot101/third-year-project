package seng302.data.local;

import seng302.data.interfaces.AdminsDAO;
import seng302.generic.Debugger;
import seng302.User.Admin;

import java.util.ArrayList;
import java.util.List;

public class AdminsM implements AdminsDAO {

    private List<Admin> admins;

    /**
     * constructor method to create a new adminsM object used for local storage
     */
    public AdminsM() {
        this.admins = new ArrayList<>();
    }

    /**
     * Adds a new ADMIN
     * @param admin The ADMIN which will be added
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
     * Updates the given ADMIN. The old ADMIN with a matching ID is replaced.
     * @param admin The ADMIN which will replace the one with a matching ID
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
     * Returns the ADMIN with the given ID. If no such ADMIN exists, null is returned.
     * If multiple admins have the given ID, the first one found is returned.
     *
     * @param id The id of the requested ADMIN.
     * @return The ADMIN which has the same ID
     */
    @Override
    public Admin getAdmin(long id, String token) {
        for(Admin a : admins) {
            if(a.getStaffID() == id) {
                return a;
            }
        }
        Debugger.log("ADMIN with id: " + id + " not found. Returning null.");
        return null;
    }

    /**
     * Removes the ADMIN with the given ID if it exists.
     * If multiple admins have the given ID, the first one found is removed.
     *
     * @param id The id of the ADMIN which will be removed.
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
