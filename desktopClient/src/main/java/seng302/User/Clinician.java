package seng302.User;

import seng302.User.Attribute.ProfileType;

/**
 * This class contains information about clinicians.
 */
public class Clinician {

    private String name, workAddress, region, username, password;


    private long staffID;

    /**
     * creates a new clinician object
     * @param username the clinician username
     * @param password the clinician password
     * @param name the name of the clinician
     */
    public Clinician(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = 1;
        this.region = null;
        this.workAddress = null;
    }

    /**
     * Used by admin to pick its own ID
     *
     * @param username    The username of the clinician
     * @param password    The clinician's password
     * @param name        The clinician's name
     * @param accountType The type of account
     * @param staffID     The clinician's ID
     */
    public Clinician(String username, String password, String name, ProfileType accountType, long staffID) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = staffID;
        this.region = null;
        this.workAddress = null;
    }

    /**
     * Used by admin to pick its own ID
     *
     * @param username    The username of the clinician
     * @param password    The clinician's password
     * @param name        The clinician's name
     * @param accountType The type of account
     */
    public Clinician(String username, String password, String name, ProfileType accountType) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = 1;
        this.region = null;
        this.workAddress = null;
    }

    /**
     * Constructor used when making a deep copy of a clinician.
     *
     * @param clinician the original clinician object being copied.
     */
    public Clinician(Clinician clinician) {
        this.username = clinician.username;
        this.password = clinician.password;
        this.name = clinician.name;
        this.staffID = clinician.staffID;
        this.region = clinician.region;
        this.workAddress = clinician.workAddress;
    }

    /**
     * method to set the staff id of the clinician
     * @param staffID long the staff id to set
     */
    public void setStaffID(long staffID) {
        this.staffID = staffID;
    }

    /**
     * method to get the clinician name
     * @return String the name of the clinician
     */
    public String getName() {
        return name;
    }

    /**
     * method to set the clinician name
     * @param name String the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * method to get the staff id
     * @return long the staff id
     */
    public long getStaffID() {
        return staffID;
    }

    /**
     * method to get the work address
     * @return String the work address
     */
    public String getWorkAddress() {
        return workAddress;
    }

    /**
     * method to set the work address
     * @param workAddress String the work address
     */
    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    /**
     * method to get the region
     * @return String the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * method to set the region
     * @param region String the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * method to get the username
     * @return String the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * method to set the username
     * @param username String the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * method to get the password
     * @return String the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * method to set the password
     * @param password String the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * overrides the toString function
     * @return returns the clinician info
     */
    @Override
    public String toString() {
        return "clinician{" +
                "name='" + name + '\'' +
                ", staffID='" + staffID + '\'' +
                ", workAddress='" + workAddress + '\'' +
                ", region='" + region + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
