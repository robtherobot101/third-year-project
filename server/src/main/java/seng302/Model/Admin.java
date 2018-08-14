package seng302.Model;

import seng302.Model.Attribute.ProfileType;

import java.util.Objects;
import java.util.Random;

/**
 * This class contains information about Admin.
 */
public class Admin extends Clinician {

    private String accessLevel;

    /**
     * Method to create a new Admin instance
     *
     * @param username the usernam of the Admin
     * @param password the password of the Admin
     * @param name     the name of the Admin

     */
    public Admin(String username, String password, String name) {
        super(username, password, name, ProfileType.ADMIN, 0);
        String[] levels = {"CONFIDENTIAL", "SECRET", "TOP SECRET"};
        Random r = new Random();
        accessLevel = levels[r.nextInt(levels.length)];
    }

    /**
     * Constructor used when making a deep copy of a Clinician.
     *
     * @param originalAdmin the original Clinician object being copied.
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
