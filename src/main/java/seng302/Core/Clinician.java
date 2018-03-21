package seng302.Core;

public class Clinician {
    private String name, workAddress, region, username, password;
    private long staffID;

    public Clinician(String username, String password, String name){
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = Main.getNextId(true, false);
        this.region = null;
        this.workAddress = null;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public long getStaffID() { return staffID; }

    public String getWorkAddress() { return workAddress; }

    public void setWorkAddress(String workAddress) { this.workAddress = workAddress; }

    public String getRegion() { return region; }

    public void setRegion(String region) { this.region = region; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Clinician{" +
                "name='" + name + '\'' +
                ", staffID='" + staffID + '\'' +
                ", workAddress='" + workAddress + '\'' +
                ", region='" + region + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
