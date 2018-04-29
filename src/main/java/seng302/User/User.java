package seng302.User;

import java.lang.reflect.Array;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import seng302.Generic.Main;
import seng302.User.Medication.Medication;
import seng302.User.Attribute.Organ;
import seng302.User.Attribute.AlcoholConsumption;
import seng302.User.Attribute.BloodType;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.SmokerStatus;
import seng302.Generic.WaitingListItem;

/**
 * This class contains information about organ users.
 */
public class User {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
    public static final String tableHeader = "User ID | Creation Time        | Name                   | Date of Birth | Date of Death | Gender | " +
            "Height | Weight | Blood Type | Region          | Current Address                | Last Modified | Organs to donate";
    private String[] name;
    private LocalDate dateOfBirth, dateOfDeath;
    private LocalDateTime creationTime;
    private LocalDateTime lastModified = null;
    private Gender gender;
    private double height, weight;
    private BloodType bloodType;
    private long id;
    private String currentAddress, region;
    private EnumSet<Organ> organs = EnumSet.noneOf(Organ.class);
    private String username, email, password;
    private String bloodPressure = "";
    private SmokerStatus smokerStatus;
    private AlcoholConsumption alcoholConsumption;
    private ArrayList<Medication> currentMedications;
    private ArrayList<Medication> historicMedications;

    private ArrayList<WaitingListItem> waitingListItems;

    public User(String name, LocalDate dateOfBirth) {
        this.name = name.split(",");
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = null;
        this.gender = null;
        this.height = -1;
        this.weight = -1;
        this.bloodType = null;
        this.region = null;
        this.currentAddress = null;
        this.creationTime = LocalDateTime.now();
        this.id = Main.getNextId(true, true);
        this.currentMedications = new ArrayList<>();
        this.historicMedications = new ArrayList<>();
        this.waitingListItems = new ArrayList<>();
    }

    public User(String name, String dateOfBirth, String dateOfDeath, String gender, double height, double weight, String bloodType, String region,
        String currentAddress) throws DateTimeException, IllegalArgumentException {
        this.name = name.split(",");
        this.dateOfBirth = LocalDate.parse(dateOfBirth, dateFormat);
        this.dateOfDeath = LocalDate.parse(dateOfDeath, dateFormat);
        this.gender = Gender.parse(gender);
        this.height = height;
        this.weight = weight;
        this.bloodType = BloodType.parse(bloodType);
        this.region = region;
        this.currentAddress = currentAddress;
        this.creationTime = LocalDateTime.now();
        this.id = Main.getNextId(true, true);
        this.currentMedications = new ArrayList<>();
        this.historicMedications = new ArrayList<>();
        this.waitingListItems = new ArrayList<>();
    }

    public User(String firstName, String[] middleNames, String lastName, LocalDate dateOfBirth, String username, String email, String password) {
        int isLastName = lastName == null || lastName.isEmpty() ? 0 : 1;
        this.name = new String[1 + middleNames.length + isLastName];
        this.name[0] = firstName;
        System.arraycopy(middleNames, 0, this.name, 1, middleNames.length);
        if (isLastName == 1) {
            this.name[this.name.length-1] = lastName;
        }
        System.out.println(getName());
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = null;
        this.gender = null;
        this.height = -1;
        this.weight = -1;
        this.bloodType = null;
        this.region = null;
        this.currentAddress = null;
        this.creationTime = LocalDateTime.now();
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = Main.getNextId(true, true);
        this.currentMedications = new ArrayList<>();
        this.historicMedications = new ArrayList<>();
        this.waitingListItems = new ArrayList<>();
    }

    public User(String firstName, String[] middleNames, String lastName, LocalDate dateOfBirth, LocalDate dateOfDeath, Gender gender, double height,
        double weight, BloodType bloodType, String region, String currentAddress, String username, String email, String password) {
        int isLastName = lastName == null || lastName.isEmpty() ? 0 : 1;
        this.name = new String[1 + middleNames.length + isLastName];
        this.name[0] = firstName;
        System.arraycopy(middleNames, 0, this.name, 1, middleNames.length);
        if (isLastName == 1) {
            this.name[this.name.length-1] = lastName;
        }
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.region = region;
        this.currentAddress = currentAddress;
        this.creationTime = LocalDateTime.now();
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = Main.getNextId(true, true);
        this.currentMedications = new ArrayList<>();
        this.historicMedications = new ArrayList<>();
        this.waitingListItems = new ArrayList<>();
    }

    /**
     * Used to create a deep copy of the object.
     * @param user The user to make a copy of
     */
    public User(User user) {
        this.name = user.name;
        this.dateOfBirth = user.dateOfBirth;
        this.dateOfDeath = user.dateOfDeath;
        this.gender = user.gender;
        this.height = user.height;
        this.weight = user.weight;
        this.bloodType = user.bloodType;
        this.region = user.region;
        this.currentAddress = user.currentAddress;
        this.creationTime = user.creationTime;
        this.id = user.id;
        this.smokerStatus = user.smokerStatus;
        this.bloodPressure = user.bloodPressure;
        this.alcoholConsumption = user.alcoholConsumption;
        this.organs.addAll(user.organs);
        this.currentMedications = new ArrayList<>();
        this.historicMedications = new ArrayList<>();
        this.currentMedications.addAll(user.currentMedications);
        this.historicMedications.addAll(user.historicMedications);
        this.waitingListItems = new ArrayList<>();
    }

    public void copyFieldsFrom(User user) {
        name = user.getNameArray();
        dateOfBirth = user.getDateOfBirth();
        dateOfDeath = user.getDateOfDeath();
        gender = user.getGender();
        bloodType = user.getBloodType();
        height = user.getHeight();
        weight = user.getWeight();
        region = user.getRegion();
        currentAddress = user.getCurrentAddress();
        smokerStatus = user.getSmokerStatus();
        bloodPressure = user.getBloodPressure();
        alcoholConsumption = user.getAlcoholConsumption();
        organs.clear();
        organs.addAll(user.getOrgans());
    }

    public void copyMedicationListsFrom(User user) {
        currentMedications.clear();
        currentMedications.addAll(user.getCurrentMedications());
        historicMedications.clear();
        historicMedications.addAll(user.getHistoricMedications());
        waitingListItems.addAll(waitingListItems);
    }

    public boolean fieldsEqual(User user) {
        return (Arrays.equals(name, user.getNameArray()) &&
                dateOfBirth == user.getDateOfBirth() &&
                dateOfDeath == user.getDateOfDeath() &&
                gender == user.getGender() &&
                bloodType == user.getBloodType() &&
                height == user.getHeight() &&
                weight == user.getWeight() &&
                stringEqual(region, user.getRegion()) &&
                stringEqual(currentAddress, user.getCurrentAddress()) &&
                smokerStatus == user.getSmokerStatus() &&
                stringEqual(bloodPressure, user.getBloodPressure()) &&
                alcoholConsumption == user.getAlcoholConsumption() &&
                organs.equals(user.getOrgans()) &&
                currentMedications.equals(user.getCurrentMedications()) &&
                historicMedications.equals(user.getHistoricMedications())
        );
    }

    private boolean stringEqual(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        } else {
            return s1.equals(s2);
        }
    }

    public String getName() {
        return String.join(" ", name);
    }

    public void setName(String name) {
        this.name = name.split(",");
        setLastModified();
    }

    public void setNameArray(String[] name) {
        this.name = name;
    }

    public void setUsername(String username) { this.username = username; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public String[] getNameArray() {
        return name;
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

    public String getCurrentAddress() { return currentAddress; }

    public String getRegion() { return region; }

    public Gender getGender() { return gender; }

    public double getHeight() { return height; }

    public double getWeight() { return weight; }

    public BloodType getBloodType() { return bloodType; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }

    public LocalDate getDateOfDeath() { return dateOfDeath; }

    public ArrayList<WaitingListItem> getWaitingListItems(){
        return waitingListItems;
    }


    public String getAgeString() {
        String age = String.format("%.1f", getAgeDouble());
        return age + " years";
    }

    public double getAgeDouble() {
        long days = Duration.between(dateOfBirth.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
        double years = days/365.00;
        return years;

    }


    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        setLastModified();
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
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

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
        setLastModified();
    }

    public void setRegion(String region) {
        this.region = region;
        setLastModified();
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

    public void removeOrgan(Organ organ) {
        if (organs.contains(organ)) {
            this.organs.remove(organ);
            System.out.println("Organ removed.");
        } else {
            System.out.println("Organ not in list.");
        }
        setLastModified();
    }

    private void setLastModified() {
        lastModified = LocalDateTime.now();
    }

    public String getBloodPressure() { return bloodPressure; }

    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }

    public SmokerStatus getSmokerStatus() { return smokerStatus; }

    public void setSmokerStatus(SmokerStatus smokerStatus) { this.smokerStatus = smokerStatus; }

    public AlcoholConsumption getAlcoholConsumption() { return alcoholConsumption; }

    public void setAlcoholConsumption(AlcoholConsumption alcoholConsumption) { this.alcoholConsumption = alcoholConsumption; }

    public ArrayList<Medication> getCurrentMedications() { return currentMedications; }

    public void setCurrentMedications(ArrayList<Medication> currentMedications) { this.currentMedications = currentMedications; }

    public ArrayList<Medication> getHistoricMedications() { return historicMedications; }

    public void setHistoricMedications(ArrayList<Medication> historicMedications) { this.historicMedications = historicMedications; }

    /**
     * Get a string containing key information about the user. Can be formatted as a table row.
     *
     * @param table Whether to format the information as a table row
     * @return The information string
     */
    public String getString(boolean table) {
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

        if (table) {
            return String.format("%-8d | %s | %-22s | %10s    | %-10s    | %-6s | %-5s  | %-6s | %-4s       | %-15s | %-30s | %-20s | %s", id,
                    dateTimeFormat.format(creationTime), getName(), dateFormat.format(dateOfBirth), dateOfDeathString, gender, heightString,
                    weightString, bloodType, region, currentAddress, dateModifiedString, organs);
        } else {
            return String.format("User (ID %d) created at %s Name: %s, Date of Birth: %s, Date of death: %s, " + "Gender: %s, Height: %s, Width: " +
                    "%s, Blood type: %s, Region: %s, Current address: %s, Last Modified: %s, Organs to donate: %s.", id, dateTimeFormat.format
                    (creationTime), getName(), dateFormat.format(dateOfBirth), dateOfDeathString, gender, heightString, weightString, bloodType,
                    region, currentAddress, dateModifiedString, organs);
        }
    }

    public String toString() {
        return getString(false);
    }

    public Boolean isDonor() {
        return !organs.isEmpty();
    }

    public boolean isReceiver() {
        boolean receiver = false;
        for (WaitingListItem item : waitingListItems){
            if (item.getOrganDeregisteredDate() == null){
                receiver = true;
            }
        }
        return receiver;
    }

    public String getType(){
        if(isDonor() && isReceiver()){
            return "donor/receiver";
        }else if(isDonor() && !isReceiver()){
            return "donor";
        }else if(!isDonor() && isReceiver()){
            return "receiver";
        }else{
            return "neither";
        }
    }
}
