package seng302;

import seng302.Logic.Database.GeneralAdmin;
import seng302.Logic.Database.GeneralClinician;
import seng302.Logic.Database.GeneralUser;
import seng302.Logic.SaltHash;
import seng302.Model.*;
import seng302.Model.Attribute.Organ;
import seng302.Model.Medication.Medication;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public abstract class HelperMethods {
    
    private static Random r = new Random();

    /**
     * Create a test user with semi-random attributes
     * @param generalUser Server access object
     * @param password The password to give the new user
     * @return The created user
     * @throws SQLException catch sql execution exceptions
     */
    public static User insertUser(GeneralUser generalUser, String password) throws SQLException {
        String[] middle = {"Middle" + r.nextInt(1000000)};
        User user = new User("First" + r.nextInt(1000000), middle, "Last" + r.nextInt(1000000), LocalDate.of(1900 + r.nextInt(100), 8, 4), "username5" + r.nextInt(1000000), "email5@domain.com" + r.nextInt(1000000), "chj" + r.nextInt(9999));
        generalUser.insertUser(user, SaltHash.createHash(password));
        user.setId(generalUser.getIdFromUser(user.getUsername()));
        return user;
    }

    /**
     * Create a test user with semi-random attributes
     * @param generalUser Server access object
     * @return The created user
     * @throws SQLException catch sql execution exceptions
     */
    public static User insertUser(GeneralUser generalUser) throws SQLException {
        return insertUser(generalUser, "pasword");
    }

    /**
     * Create a test admin with semi-random attributes
     * @param generalAdmin Server access object
     * @return The created admin
     * @throws SQLException catch sql execution exceptions
     */
    public static Admin insertAdmin(GeneralAdmin generalAdmin) throws SQLException {
        return insertAdmin(generalAdmin, "password" + r.nextInt(1000000));
    }

    /**
     * Create a test admin with semi-random attributes
     * @param generalAdmin Server access object
     * @param password The admin's password
     * @return The created admin
     * @throws SQLException catch sql execution exceptions
     */
    public static Admin insertAdmin(GeneralAdmin generalAdmin, String password) throws SQLException {
        Admin admin = new Admin("username" + r.nextInt(1000000), password, "Full Name" + r.nextInt(1000000));
        admin.setPassword(SaltHash.createHash(admin.getPassword()));
        generalAdmin.insertAdmin(admin);
        admin.setStaffID(generalAdmin.getAdminIdFromUsername(admin.getUsername()));
        return admin;
    }

    /**
     * Create a test clinician with semi-random attributes
     * @param generalClinician Server access object
     * @param password The clinician's password
     * @return The created clinician
     * @throws SQLException catch sql execution exceptions
     */
    public static Clinician insertClinician(GeneralClinician generalClinician, String password) throws SQLException {
        Clinician Clinician = new Clinician("username" + r.nextInt(1000000), password, "Full Name" + r.nextInt(1000000));
        Clinician.setPassword(SaltHash.createHash(Clinician.getPassword()));
        generalClinician.insertClinician(Clinician);
        Clinician.setStaffID(generalClinician.getClinicianIdFromUsername(Clinician.getUsername()));
        return Clinician;
    }

    /**
     * Create a test clinician with semi-random attributes
     * @param generalClinician Server access object
     * @return The created clinician
     * @throws SQLException catch sql execution exceptions
     */
    public static Clinician insertClinician(GeneralClinician generalClinician) throws SQLException {
        return insertClinician(generalClinician, "password" + r.nextInt(1000000));
    }

    /**
     * Create a list of medications with semi-random names and ingredients
     * @return A list of medication objects
     */
    public static ArrayList<Medication> makeMedications(){
        ArrayList<Medication> medications = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            ArrayList<String> history = new ArrayList<>();
            String[] ingredients = {"Ingredient1", "Ingredient2", "Ingredient3", "Ingredient4"};
            Medication m = new Medication("medication" + i, ingredients, history, i);
            m.startedTaking();
            medications.add(m);
        }
        return medications;
    }

    /**
     * Create a list of diseases with semi-random names and ingredients
     * @param cured if the new disease is cured or not
     * @return A list of disease objects
     */
    public static ArrayList<Disease> makeDiseases(boolean cured) {
        ArrayList<Disease> diseases = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            Disease d = new Disease("Disease" + i, LocalDate.of(1985 + i, 3, 14), (i % 2) == 1, cured && (i % 4) == 0, i);
            diseases.add(d);
        }
        return diseases;
    }

    /**
     * Create a list of procedures with semi-random info
     * @return A list of procedure objects
     */
    public static ArrayList<Procedure> makeProcedures() {
        ArrayList<Procedure> procedures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ArrayList<Organ> organs = new ArrayList<>(Arrays.asList(Organ.BONE, Organ.CORNEA, Organ.PANCREAS));
            Procedure p = new Procedure("Summary" + r.nextInt(1000000), "description" + r.nextInt(1000000), LocalDate.of(2013 + r.nextInt(10), 3, 14), organs, i);
            procedures.add(p);
        }
        return procedures;
    }

    /**
     * Create a list of waiting list items
     * @param userid the userid for the waiting list item
     * @return A list of waiting list items objects
     */
    public static ArrayList<WaitingListItem> makeWaitingListItems(int userid) {
        ArrayList<WaitingListItem> waitingListItems = new ArrayList<>();
        waitingListItems.add(new WaitingListItem(Organ.HEART, LocalDate.of(2013 + r.nextInt(10), 3, 14), 0, userid, null, 0));
        waitingListItems.add(new WaitingListItem(Organ.BONE, LocalDate.of(2013 + r.nextInt(10), 3, 14), 0, userid, null, 0));
        waitingListItems.add(new WaitingListItem(Organ.SKIN, LocalDate.of(2013 + r.nextInt(10), 3, 14), 0, userid, null, 0));
        return waitingListItems;
    }

    /**
     * Create a list of random country names
     * @return A list of procedure objects
     */
    public static ArrayList<Country> makeCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Country c = new Country("country" + r.nextInt(1000000), i & 4);
            countries.add(c);
        }
        return countries;
    }

    /**
     * Create a list of HistoryItems with semi-random info
     * @return A list of HistoryItem objects
     */
    public static ArrayList<HistoryItem> makeHistory() {
        ArrayList<HistoryItem> history = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HistoryItem h = new HistoryItem(LocalDateTime.of(2018, 6, 6, i, (i * 37) % 24, (i * 41) % 60), "Did a thing", "This is what happened", i);
            history.add(h);
        }
        return history;
    }

    public static boolean containsWaitingListItems(Collection<WaitingListItem> superset, Collection<WaitingListItem> contained) {
        boolean found;
        for (WaitingListItem w1: contained) {
            found = false;
            for (WaitingListItem w2: superset) {
                if (waitingListItemsEqual(w1, w2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private static boolean waitingListItemsEqual(WaitingListItem w1, WaitingListItem w2) {
        if (w1.getUserId() != w2.getUserId()) {
            return false;
        }
        if (w1.getOrganDeregisteredCode() != w2.getOrganDeregisteredCode()) {
            return false;
        }
        if (w1.getOrganType() != w2.getOrganType()) {
            return false;
        }
        if (w1.getOrganRegisteredDate() == null) {
            if (w2.getOrganRegisteredDate() != null) {
                return false;
            }
        } else {
            if (w2.getOrganRegisteredDate() == null) {
                return false;
            } else {
                if (!w1.getOrganRegisteredDate().equals(w2.getOrganRegisteredDate())) {
                    return false;
                }
            }
        }
        if (w1.getOrganDeregisteredDate() == null) {
            if (w2.getOrganDeregisteredDate() != null) {
                return false;
            }
        } else {
            if (w2.getOrganDeregisteredDate() == null) {
                return false;
            } else {
                if (!w1.getOrganDeregisteredDate().equals(w2.getOrganDeregisteredDate())) {
                    return false;
                }
            }
        }
        return true;
    }
}
