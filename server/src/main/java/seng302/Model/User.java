package seng302.Model;

import seng302.Model.Attribute.*;
import seng302.Model.Medication.Medication;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

/**
 * This class contains information about organ users.
 */
public class User {

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy"), dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
    private String[] name, preferredName;
    private LocalDate dateOfBirth = null;
    private LocalDateTime dateOfDeath, creationTime, lastModified = null;
    private Gender gender = null, genderIdentity = null;
    private double height = -1, weight = -1;
    private BloodType bloodType = null;
    private long id;
    private String nhi;
    private EnumSet<Organ> organs = EnumSet.noneOf(Organ.class);
    private int zipCode=0;
    private String currentAddress = "", region = "", city="", country="", homePhone="", mobilePhone="", username, email, password, bloodPressure = "", profileImageType="";
    private SmokerStatus smokerStatus;
    private AlcoholConsumption alcoholConsumption;
    private ArrayList<Medication> currentMedications = new ArrayList<>(), historicMedications = new ArrayList<>();
    private ArrayList<Disease> currentDiseases = new ArrayList<>(), curedDiseases = new ArrayList<>();
    private ArrayList<Procedure> pendingProcedures = new ArrayList<>(), previousProcedures = new ArrayList<>();
    private ArrayList<WaitingListItem> waitingListItems = new ArrayList<>();
    private ArrayList<HistoryItem> userHistory = new ArrayList<>();
    private String cityOfDeath = "";
    private String regionOfDeath = "";
    private String countryOfDeath;

    /**
     * constructor method to create a new user object
     * @param firstName String the user first name
     * @param middleNames String[] the middle names of the user
     * @param lastName String the last name of the user
     * @param dateOfBirth LocalDate the date of birth of the user
     * @param username String the username of the user
     * @param email String the email of the user
     * @param nhi String the NHI of the user
     * @param password String the password of the user
     */
    public User(String firstName, String[] middleNames, String lastName, LocalDate dateOfBirth, String username, String email, String nhi, String password) {
        int isLastName = lastName == null || lastName.isEmpty() ? 0 : 1;
        this.name = new String[1 + middleNames.length + isLastName];
        this.name[0] = firstName;
        System.arraycopy(middleNames, 0, this.name, 1, middleNames.length);
        if (isLastName == 1) {
            this.name[this.name.length - 1] = lastName;
        }
        this.preferredName = this.name;
        this.dateOfBirth = dateOfBirth;
        this.creationTime = LocalDateTime.now();
        this.username = username;
        this.email = email;
        this.nhi = nhi;
        this.password = password;
        this.profileImageType = null;
        /*this.id = DataManager.getNextId(true, ProfileType.USER);*/
    }

    /**
     * constructor method to create a new user object with the bare minimum attributes
     * used with the cli
     * @param name the full name of the user
     * @param dateOfBirth the date of birth of the user
     */
    public User(String name, LocalDate dateOfBirth) {
        this.name = name.split(",");
        this.preferredName = this.name;
        this.dateOfBirth = dateOfBirth;
        this.creationTime = LocalDateTime.now();
    }

    public void copyFieldsFrom(User user) {
        name = user.getNameArray();
        preferredName = user.getPreferredNameArray();
        dateOfBirth = user.getDateOfBirth();
        dateOfDeath = user.getDateOfDeath();
        gender = user.getGender();
        genderIdentity = user.getGenderIdentity();
        bloodType = user.getBloodType();
        height = user.getHeight();
        weight = user.getWeight();
        region = user.getRegion();
        country = user.getCountry();
        currentAddress = user.getCurrentAddress();
        smokerStatus = user.getSmokerStatus();
        bloodPressure = user.getBloodPressure();
        alcoholConsumption = user.getAlcoholConsumption();
        organs.clear();
        organs.addAll(user.getOrgans());
        countryOfDeath = user.getCountryOfDeath();
        regionOfDeath = user.getRegionOfDeath();
        cityOfDeath = user.getCityOfDeath();
    }


    /**
     * constructor method to create a user object from the database
     * @param id int the id of the user
     * @param firstName String the user first name
     * @param middleNames String[] the middle names of the user
     * @param lastName String the last name of the user
     * @param dateOfBirth LocalDate the date of birth of the userh
     * @param dateOfDeath LocalDate the date of death of the user
     * @param gender String the gender of the user
     * @param height double the height of the user
     * @param weight double the weight of the user
     * @param bloodType String the blood type of the user
     * @param region String the region of the user
     * @param currentAddress String the current address of the user
     * @param username String the username of the user
     * @param email String the email of the user
     * @param nhi String the NHI of the user
     * @param password String the password of the user
     * @param cityOfDeath String of the city of death
     * @param country String of the country of residence
     * @param countryOfDeath String of the country of death
     * @param regionOfDeath String of the region of death
     * @param profileImageType String of the type of profile image
     */
    public User(int id, String firstName, String[] middleNames, String lastName, LocalDate dateOfBirth, LocalDateTime dateOfDeath, Gender gender, double height,
                double weight, BloodType bloodType, String region, String currentAddress, String username, String email, String nhi, String password, String country, String cityOfDeath,
                String regionOfDeath, String countryOfDeath, String profileImageType) {
        int isLastName = lastName == null || lastName.isEmpty() ? 0 : 1;
        int lenMiddleNames = middleNames == null ? 0 : middleNames.length;
        this.name = new String[1 + lenMiddleNames + isLastName];
        this.name[0] = firstName;
        if (middleNames != null) {
            System.arraycopy(middleNames, 0, this.name, 1, lenMiddleNames);
        }
        if (isLastName == 1) {
            this.name[this.name.length - 1] = lastName;
        }
        this.preferredName = this.name;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.gender = gender;
        this.genderIdentity = gender;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.region = region;
        this.currentAddress = currentAddress;
        this.creationTime = LocalDateTime.now();
        this.username = username;
        this.email = email;
        this.nhi = nhi;
        this.password = password;
        this.country = country;
        this.id = id;
        this.cityOfDeath = cityOfDeath;
        this.regionOfDeath = regionOfDeath;
        this.countryOfDeath = countryOfDeath;
        this.currentMedications = new ArrayList<>();
        this.historicMedications = new ArrayList<>();
        this.waitingListItems = new ArrayList<>();
        this.currentDiseases = new ArrayList<>();
        this.curedDiseases = new ArrayList<>();
        this.pendingProcedures = new ArrayList<>();
        this.previousProcedures = new ArrayList<>();
        this.profileImageType = profileImageType;
    }

    public double getAgeDouble() {
        long days = Duration.between(dateOfBirth.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
        return days / 365.00;

    }

    public String getName() {
        return String.join(" ", name);
    }

    private String getPreferredName() {
        String val;
        if (preferredName == null || preferredName.length == 0) {
            val = "";
        } else {
            val = String.join(" ", preferredName);
        }
        return val;
    }

    public void setName(String name) {
        this.name = name.split(",");
        setLastModified();
    }

    public void setPreferredName(String name) {
        this.preferredName = name.split(",");
        setLastModified();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPreferredNameArray(String[] name) {
        this.preferredName = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public String[] getNameArray() {
        return name;
    }

    public String[] getPreferredNameArray() {
        return preferredName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public EnumSet<Organ> getOrgans() {
        return organs;
    }

    public long getId() {
        return id;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getRegion() {
        return region;
    }

    public Gender getGender() {
        return gender;
    }

    public Gender getGenderIdentity() {
        return genderIdentity;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDateTime getDateOfDeath() {
        return dateOfDeath;
    }

    public ArrayList<WaitingListItem> getWaitingListItems() {
        return waitingListItems;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        setLastModified();
    }

    public void setDateOfDeath(LocalDateTime dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
        setLastModified();
    }

    public void setHeight(double height) {
        this.height = height;
        setLastModified();
    }

    public void setWeight(double weight) {
        this.weight = weight;
        setLastModified();
    }

    public void setGender(Gender gender) {
        this.gender = gender;
        setLastModified();
    }

    public void setGenderIdentity(Gender gender) {
        this.genderIdentity = gender;
        setLastModified();
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
        setLastModified();
    }

    public void setRegion(String region) {
        this.region = region;
        setLastModified();
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
        setLastModified();
    }

    public void setOrgan(Organ organ) {
        if (!organs.contains(organ)) {
            this.organs.add(organ);
            System.out.println("Organ added.");
        } else {
            System.out.println("Organ already being donated.");
        }
        setLastModified();
    }

    private void setLastModified() {
        lastModified = LocalDateTime.now();
    }

    public void setLastModifiedForDatabase(LocalDateTime time) {
        lastModified = time;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public SmokerStatus getSmokerStatus() {
        return smokerStatus;
    }

    public void setSmokerStatus(SmokerStatus smokerStatus) {
        this.smokerStatus = smokerStatus;
    }

    public AlcoholConsumption getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(AlcoholConsumption alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public ArrayList<Medication> getCurrentMedications() {
        return currentMedications;
    }

    public ArrayList<Medication> getHistoricMedications() {
        return historicMedications;
    }

    public ArrayList<Procedure> getPendingProcedures() {
        return pendingProcedures;
    }

    public ArrayList<Procedure> getPreviousProcedures() {
        return previousProcedures;
    }

    public ArrayList<HistoryItem> getUserHistory() {
        return userHistory;
    }

    /**
     * Get a string containing key information about the user.
     *
     * @return The information string
     */
    public String toString() {
        String dateOfDeathString, dateModifiedString, heightString, weightString;
        if (dateOfDeath != null) {
            dateOfDeathString = dateFormat.format(dateOfDeath);
        } else {
            dateOfDeathString = null;
        }
        if (lastModified == null) {
            dateModifiedString = null;
        } else {
            dateModifiedString = dateTimeFormat.format(lastModified);
        }
        if (height == -1) {
            heightString = null;
        } else {
            heightString = String.format("%.2f", height);
        }
        if (weight == -1) {
            weightString = null;
        } else {
            weightString = String.format("%.2f", weight);
        }
        return String.format("user (ID %d) created at %s "
                + "\n-Name: %s"
                + "\n-Preferred Name: %s"
                + "\n-Date of Birth: %s"
                + "\n-Date of death: %s"
                + "\n-Gender: %s"
                + "\n-Height: %s"
                + "\n-Weight: %s"
                + "\n-Blood type: %s"
                + "\n-Region: %s"
                + "\n-Current address: %s"
                + "\n-Last Modified: %s"
                + "\n-Organs to donate: %s.",
            id, dateTimeFormat.format(creationTime), getName(), getPreferredName(), dateFormat.format(dateOfBirth), dateOfDeathString,
            genderIdentity, heightString, weightString, bloodType, region, currentAddress, dateModifiedString, organs);
    }

    public String getSummaryString() {
        return String.format("%s (preferred name %s), ID %d", getName(), getPreferredName(), id);
    }

    public ArrayList<Disease> getCurrentDiseases() {
        return currentDiseases;
    }


    public ArrayList<Disease> getCuredDiseases() {
        return curedDiseases;
    }

    public void setCuredDiseases(ArrayList<Disease> curedDiseases) {
        this.curedDiseases = curedDiseases;
    }

    public Boolean isDonor() {
        return !organs.isEmpty();
    }

    public void setPendingProcedures(ArrayList<Procedure> item) { this.pendingProcedures = item; }

    public void setPreviousProcedures(ArrayList<Procedure> item) { this.previousProcedures = item; }

    public void setHistoricMedications(ArrayList<Medication> item) { this.historicMedications = item; }

    public void setCurrentMedications(ArrayList<Medication> item) { this.currentMedications = item; }


    public void addHistoryItem(HistoryItem historyItem) {
        userHistory.add(historyItem);
    }

    public String getProfileImageType() {
        return this.profileImageType;
    }

    public void setProfileImageType(String profileImageType) {
        if (profileImageType.length() != 3) {
            // Not a photo extension
            throw new IllegalArgumentException();
        }
        this.profileImageType = profileImageType;
    }


    public String getCityOfDeath() {
        return cityOfDeath;
    }

    public String getRegionOfDeath() {
        return regionOfDeath;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryOfDeath() {
        return countryOfDeath;
    }

    public void setCountryOfDeath(String countryOfDeath) {
        this.countryOfDeath = countryOfDeath;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Arrays.equals(name, user.name) &&
                Arrays.equals(preferredName, user.preferredName) &&
                Objects.equals(dateOfBirth, user.dateOfBirth) &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(dateOfBirth, id, username, email, password);
        result = 31 * result + Arrays.hashCode(name);
        result = 31 * result + Arrays.hashCode(preferredName);
        return result;
    }

    public String getNhi() {
        return nhi;
    }

    public void setNhi(String nhi) {
        this.nhi = nhi;
    }
}
