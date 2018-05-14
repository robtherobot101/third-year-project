package seng302.Generic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng302.User.Admin;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.User;

public class DataManager {

    public static ObservableList<User> users = FXCollections.observableArrayList();
    public static ObservableList<Clinician> clinicians = FXCollections.observableArrayList();
    public static ObservableList<Admin> admins = FXCollections.observableArrayList();
    private static long nextUserId = -1, nextClinicianId = -1, nextAdminId = -1;

    /**
     * Get the unique id number for the next user or the last id number issued.
     *
     * @param increment Whether to increment the unique id counter before returning the unique id value.
     * @param type      Whether to increment and return clinician, user or admin.
     * @return returns either the next unique id number or the last issued id number depending on whether increment
     * was true or false
     */
    public static long getNextId(boolean increment, ProfileType type) {
        recalculateNextId(ProfileType.ADMIN);
        recalculateNextId(ProfileType.USER);
        recalculateNextId(ProfileType.CLINICIAN);
        if (increment) {
            switch (type) {
                case USER:
                    nextUserId++;
                    break;
                case CLINICIAN:
                    nextClinicianId++;
                    break;
                case ADMIN:
                    nextAdminId++;
                    break;
            }
        }
        switch (type) {
            case USER:
                return nextUserId;
            case CLINICIAN:
                return nextClinicianId;
            case ADMIN:
                return nextAdminId;
            default:
                // Unreachable
                return -69;
        }
    }

    /**
     * Changes the next id to be issued to a new user to be correct for the current users list.
     *
     * @param type Whether to recalculate user, clinician or admin ID
     */
    public static void recalculateNextId(ProfileType type) {
        switch (type) {
            case USER:
                nextUserId = -1;
                for (User nextUser : users) {
                    if (nextUser.getId() > nextUserId) {
                        nextUserId = nextUser.getId();
                    }
                }
                break;
            case CLINICIAN:
                nextClinicianId = -1;
                for (Clinician clinician : clinicians) {
                    if (clinician.getStaffID() > nextClinicianId) {
                        nextClinicianId = clinician.getStaffID();
                    }
                }
                break;
            case ADMIN:
                nextAdminId = -1;
                for (Admin admin : admins) {
                    if (admin.getStaffID() > nextAdminId) {
                        nextAdminId = admin.getStaffID();
                    }
                }
                break;
        }
    }
}
