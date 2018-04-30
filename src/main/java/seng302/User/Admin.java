package seng302.User;

import seng302.Generic.Main;

/**
 * This class contains information about admin.
 */
public class Admin {
    private String name, username, password;
    private long staffID;

    public Admin(String username, String password, String name){
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = Main.getNextId(true, false);
    }

    /**
     * Constructor used when making a deep copy of a clinician.
     * @param originalAdmin the original clinician object being copied.
     */
    public Admin(Admin originalAdmin){
        this.username = originalAdmin.username;
        this.password = originalAdmin.password;
        this.name = originalAdmin.name;
        this.staffID = originalAdmin.staffID;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public long getStaffID() { return staffID; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Admin{" +
                "name='" + name + '\'' +
                ", staffID='" + staffID + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
