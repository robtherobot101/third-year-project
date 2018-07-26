package seng302.User;

import seng302.Generic.DataManager;
import seng302.User.Attribute.ProfileType;

import java.util.Random;

/**
 * This class contains information about admin.
 */
public class Admin extends Clinician {

    private String accessLevel;

    /**
     * Method to create a new admin instance
     *
     * @param username the username of the admin
     * @param password the password of the admin
     * @param name     the name of the admin
     */
    public Admin(String username, String password, String name) {
        //TODO Implement functionality for getting actual id for all profiles
        super(username, password, name, ProfileType.ADMIN, 1);
        String[] levels = {"CONFIDENTIAL", "SECRET", "TOP SECRET"};
        Random r = new Random();
        accessLevel = levels[r.nextInt(levels.length)];
    }

    /**
     * Constructor used when making a deep copy of a clinician.
     *
     * @param originalAdmin the original clinician object being copied.
     */
    public Admin(Admin originalAdmin) {
        super(originalAdmin);
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public String getUsername() {
        return super.getUsername();
    }

    public void setUsername(String username) {
        super.setUsername(username);
    }

    public String getPassword() {
        return super.getPassword();
    }

    public void setPassword(String password) {
        super.setPassword(password);
    }

    /**
     * overrides the toString function
     * @return returns the admins info
     */
    @Override
    public String toString() {
        return "Admin{" +
                "name='" + getName() + '\'' +
                ", staffID='" + getStaffID() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                '}';
    }
}
