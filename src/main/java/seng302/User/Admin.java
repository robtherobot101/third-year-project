package seng302.User;

import seng302.Generic.Main;
import seng302.User.Attribute.LoginType;

/**
 * This class contains information about admin.
 */
public class Admin extends Clinician {
    private String name, username, password;
    private long adminID;

    public Admin(String username, String password, String name){
        super(username, password, name);
        this.adminID = Main.getNextId(true, LoginType.ADMIN);
    }

    /**
     * Constructor used when making a deep copy of a clinician.
     * @param originalAdmin the original clinician object being copied.
     */
    public Admin(Admin originalAdmin){
        super(originalAdmin.username, originalAdmin.password, originalAdmin.name);
        this.adminID = originalAdmin.adminID;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public long getAdminID() { return adminID; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Admin{" +
                "name='" + name + '\'' +
                ", staffID='" + adminID + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
