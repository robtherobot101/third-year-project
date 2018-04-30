package seng302.User;

import seng302.Generic.Main;
import seng302.User.Attribute.LoginType;

/**
 * This class contains information about admin.
 */
public class Admin extends Clinician {
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
        super(originalAdmin);
        this.adminID = originalAdmin.adminID;
    }

    public String getName() { return super.getName(); }

    public void setName(String name) { super.setName(name); }

    public long getAdminID() { return adminID; }

    public String getUsername() { return super.getUsername(); }

    public void setUsername(String username) { super.setUsername(username); }

    public String getPassword() { return super.getPassword(); }

    public void setPassword(String password) { super.setPassword(password); }

    @Override
    public String toString() {
        return "Admin{" +
                "name='" + getName() + '\'' +
                ", staffID='" + adminID + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                '}';
    }
}
