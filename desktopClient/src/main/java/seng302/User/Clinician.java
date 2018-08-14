package seng302.User;

import seng302.User.Attribute.ProfileType;

/**
 * This class contains information about clinicians.
 */
public class Clinician {

    private String name, workAddress, region, username, password;


    private long staffID;
    private ProfileType accountType;

    public Clinician(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
        // TODO Add functionality to DAOs for getting next id.
        this.staffID = 1;
        this.region = null;
        this.workAddress = null;
        this.accountType = ProfileType.CLINICIAN;
    }

    /**
     * Used by Admin to pick its own ID
     *
     * @param username    The username of the Clinician
     * @param password    The Clinician's password
     * @param name        The Clinician's name
     * @param accountType The type of account
     * @param staffID     The Clinician's ID
     */
    public Clinician(String username, String password, String name, ProfileType accountType, long staffID) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = staffID;
        this.region = null;
        this.workAddress = null;
        this.accountType = accountType;
    }

    /**
     * Used by Admin to pick its own ID
     *
     * @param username    The username of the Clinician
     * @param password    The Clinician's password
     * @param name        The Clinician's name
     * @param accountType The type of account
     */
    public Clinician(String username, String password, String name, ProfileType accountType) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = 1;
        this.region = null;
        this.workAddress = null;
        this.accountType = accountType;
    }

    /**
     * Constructor used when making a deep copy of a Clinician.
     *
     * @param clinician the original Clinician object being copied.
     */
    public Clinician(Clinician clinician) {
        this.username = clinician.username;
        this.password = clinician.password;
        this.name = clinician.name;
        this.staffID = clinician.staffID;
        this.region = clinician.region;
        this.workAddress = clinician.workAddress;
    }

    public void setStaffID(long staffID) {
        this.staffID = staffID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStaffID() {
        return staffID;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * copies info from one Clinician to another
     * @param clinician the Clinician to copy from
     */
    public void copyFieldsFrom(Clinician clinician) {
        this.name = clinician.name;
        this.region = clinician.region;
        this.workAddress = clinician.workAddress;
    }

    /**
     * overrides the toString function
     * @return returns the Clinician info
     */
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
