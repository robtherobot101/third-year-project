package seng302.User;

import java.util.Random;

/**
 * This class contains information about admin.
 */
public class Admin extends Clinician {
    private String accessLevel;

    public Admin(String username, String password, String name){
        super(username, password, name);
        String[] levels = {"CONFIDENTIAL", "SECRET", "TOP SECRET"};
        Random r = new Random();
        accessLevel = levels[r.nextInt(levels.length)];
    }

    /**
     * Constructor used when making a deep copy of a clinician.
     * @param originalAdmin the original clinician object being copied.
     */
    public Admin(Admin originalAdmin){
        super(originalAdmin);
    }

    public String getName() { return super.getName(); }

    public void setName(String name) { super.setName(name); }

    public String getUsername() { return super.getUsername(); }

    public void setUsername(String username) { super.setUsername(username); }

    public String getPassword() { return super.getPassword(); }

    public void setPassword(String password) { super.setPassword(password); }

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
