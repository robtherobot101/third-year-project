package seng302.Core;

public class Clinician {
    private String name;
    private String staffID;
    private String workAddress;
    private String region;

    private String username;
    private String password;

    public Clinician(String username, String password, String name){
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getStaffID() { return staffID; }

    public void setStaffID(String staffID) { this.staffID = staffID; }

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
