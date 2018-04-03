package seng302.Core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * This class contains information about organ donors.
 */
public class Donor {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
    public static final String tableHeader = "Donor ID | Creation Time        | Name                   | Date of Birth | Date of Death | Gender | " +
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
//    private ObservableList<Medication> currentMedications;
//    private ObservableList<Medication> historicMedications;

    public Donor(String name, LocalDate dateOfBirth) {
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
//        this.currentMedications = FXCollections.observableArrayList();
//        this.historicMedications = FXCollections.observableArrayList();
    }

    public Donor(String name, String dateOfBirth, String dateOfDeath, String gender, double height, double weight, String bloodType, String region,
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
//        this.currentMedications = FXCollections.observableArrayList();
//        this.historicMedications = FXCollections.observableArrayList();
    }

    public Donor(String firstName, String[] middleNames, String lastName, LocalDate dateOfBirth, String username, String email, String password) {
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
//        this.currentMedications = FXCollections.observableArrayList();
//        this.historicMedications = FXCollections.observableArrayList();
    }

    public Donor(String firstName, String[] middleNames, String lastName, LocalDate dateOfBirth, LocalDate dateOfDeath, Gender gender, double height,
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
//        this.currentMedications = FXCollections.observableArrayList();
//        this.historicMedications = FXCollections.observableArrayList();
    }

    /**
     * Used to create a deep copy of the object.
     * @param donor The donor to make a copy of
     */
    public Donor(Donor donor) {
        this.name = donor.name;
        this.dateOfBirth = donor.dateOfBirth;
        this.dateOfDeath = donor.dateOfDeath;
        this.gender = donor.gender;
        this.height = donor.height;
        this.weight = donor.weight;
        this.bloodType = donor.bloodType;
        this.region = donor.region;
        this.currentAddress = donor.currentAddress;
        this.creationTime = donor.creationTime;
        this.id = donor.id;
        this.smokerStatus = donor.smokerStatus;
        this.bloodPressure = donor.bloodPressure;
        this.alcoholConsumption = donor.alcoholConsumption;
        this.organs.addAll(donor.organs);
//        this.currentMedications = donor.currentMedications;
//        this.historicMedications = donor.historicMedications;
    }

    public void copyFieldsFrom(Donor donor) {
        name = donor.getNameArray();
        dateOfBirth = donor.getDateOfBirth();
        dateOfDeath = donor.getDateOfDeath();
        gender = donor.getGender();
        bloodType = donor.getBloodType();
        height = donor.getHeight();
        weight = donor.getWeight();
        region = donor.getRegion();
        currentAddress = donor.getCurrentAddress();
        smokerStatus = donor.getSmokerStatus();
        bloodPressure = donor.getBloodPressure();
        alcoholConsumption = donor.getAlcoholConsumption();
        organs.clear();
        organs.addAll(donor.getOrgans());
//        currentMedications.clear();
//        currentMedications.addAll(donor.getCurrentMedications());
//        historicMedications.clear();
//        historicMedications.addAll(donor.getHistoricMedications());
    }

    public boolean fieldsEqual(Donor donor) {
        return (Arrays.equals(name, donor.getNameArray()) &&
                dateOfBirth == donor.getDateOfBirth() &&
                dateOfDeath == donor.getDateOfDeath() &&
                gender == donor.getGender() &&
                bloodType == donor.getBloodType() &&
                height == donor.getHeight() &&
                weight == donor.getWeight() &&
                stringEqual(region, donor.getRegion()) &&
                stringEqual(currentAddress, donor.getCurrentAddress()) &&
                smokerStatus == donor.getSmokerStatus() &&
                stringEqual(bloodPressure, donor.getBloodPressure()) &&
                alcoholConsumption == donor.getAlcoholConsumption() &&
                organs.equals(donor.getOrgans()) // &&
//                currentMedications.equals(donor.getCurrentMedications()) &&
//                historicMedications.equals(donor.getHistoricMedications())
        );
    }

    private boolean stringEqual(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        } else {
            return s2 != null && s1.equals(s2);
        }
    }

    public String getName() {
        return String.join(" ", name);
    }

    public void setName(String name) {
        this.name = name.split(",");
        setLastModified();
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

    public long getAge() {
        LocalDate today = LocalDate.now();
        return ChronoUnit.YEARS.between(dateOfBirth, today);
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

//    public ObservableList<Medication> getCurrentMedications() { return currentMedications; }
//
//    public void setCurrentMedications(ObservableList<Medication> currentMedications) { this.currentMedications = currentMedications; }
//
//    public ObservableList<Medication> getHistoricMedications() { return historicMedications; }
//
//    public void setHistoricMedications(ObservableList<Medication> historicMedications) { this.historicMedications = historicMedications; }

    /**
     * Get a string containing key information about the donor. Can be formatted as a table row.
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
            return String.format("Donor (ID %d) created at %s Name: %s, Date of Birth: %s, Date of death: %s, " + "Gender: %s, Height: %s, Width: " +
                    "%s, Blood type: %s, Region: %s, Current address: %s, Last Modified: %s, Organs to donate: %s.", id, dateTimeFormat.format
                    (creationTime), getName(), dateFormat.format(dateOfBirth), dateOfDeathString, gender, heightString, weightString, bloodType,
                    region, currentAddress, dateModifiedString, organs);
        }
    }

    public String toString() {
        return getString(false);
    }
}
