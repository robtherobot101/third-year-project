package seng302.Model;

import seng302.Model.Attribute.ProfileType;

import java.util.Objects;

/**
 * This class contains information about clinicians.
 */
public class Clinician {

    private String name, workAddress, region, username, password;
    public static final String tableHeader = "Model ID  | Name    | Work address           | region        | Username   ";


    private long staffID;
    private ProfileType accountType;

    /**
     * constructor method to create a new clinician object with minimal objects
     * @param username String the username of the clinician
     * @param password String the password for the clinician
     * @param name String the name of the clinician
     */
    public Clinician(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.region = null;
        this.workAddress = null;
        this.accountType = ProfileType.CLINICIAN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clinician clinician = (Clinician) o;
        return Objects.equals(name, clinician.name) &&
                Objects.equals(workAddress, clinician.workAddress) &&
                Objects.equals(region, clinician.region) &&
                Objects.equals(username, clinician.username) &&
                Objects.equals(password, clinician.password) &&
                accountType == clinician.accountType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, workAddress, region, username, password, accountType);
    }

    /**
     * Used by Admin to pick its own ID
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
        this.accountType = accountType;
    }

    /**
     * Used by Admin to pick its own ID
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
//        this.staffID = DataManager.getNextId(true, ProfileType.CLINICIAN);
        this.region = null;
        this.workAddress = null;
        this.accountType = accountType;
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
     * method to set the clinicians staffid
     * @param staffID long the staff id of the clinician
     */
    public void setStaffID(long staffID) { this.staffID = staffID; }

    /**
     * method to get the name of the clinician
     * @return String the name of the clinician
     */
    public String getName() {
        return name;
    }

    /**
     * method to set the name of the clinician
     * @param name String the name of the clinician
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * method to get the staff id of a clinician
     * @return long the staff id
     */
    public long getStaffID() {
        return staffID;
    }

    /**
     * method to get the work address of a clinician
     * @return String the work address
     */
    public String getWorkAddress() {
        return workAddress;
    }

    /**
     * method to set a clinicians work address
     * @param workAddress String the work address
     */
    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    /**
     * method to get the clinicians region
     * @return String the region of the clinician
     */
    public String getRegion() {
        return region;
    }

    /**
     * method to set the region of the clinician
     * @param region String the region of the clinician
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * method to get the clinicians username
     * @return String the username of the clinician
     */
    public String getUsername() {
        return username;
    }

    /**
     * method to set the username of the clinician
     * @param username String the user name of the clinician
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * method to get the password of the clinician
     * @return String the password of the clinician
     */
    public String getPassword() {
        return password;
    }

    /**
     * method to set the password of the clinician
     * @param password String the password of the clinician
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get a string containing key information about the user. Can be formatted as a table row.
     *
     * @param table Whether to format the information as a table row
     * @return The information string
     */
    public String getString(boolean table) {

        if (table) {
            return String.format("%-8d | %s | %-22s | %10s    | %-10s    ", staffID,
                    name, workAddress,region, username);
        } else {
                return String.format("Clinician (ID %d) Name: %s, Work address: %s, Region: %s, Username: %s.", staffID
                        , name, workAddress, region, username);
            }
    }



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
