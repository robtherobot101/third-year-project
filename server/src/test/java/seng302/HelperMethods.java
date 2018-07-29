package seng302;

import seng302.Logic.Database.GeneralAdmin;
import seng302.Logic.Database.GeneralClinician;
import seng302.Logic.Database.GeneralUser;
import seng302.Model.*;
import seng302.Model.Attribute.Organ;
import seng302.Model.Medication.Medication;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class HelperMethods {
    
    private static Random r = new Random();

    /**
     * Create a test user with semi-random attributes
     * @param generalUser Server access object
     * @return The created user
     * @throws SQLException
     */
    public static User insertUser(GeneralUser generalUser) throws SQLException {
        String[] middle = {"Middle" + r.nextInt(1000000)};
        User user = new User("First" + r.nextInt(1000000), middle, "Last" + r.nextInt(1000000), LocalDate.of(1900 + r.nextInt(100), 8, 4), "username5" + r.nextInt(1000000), "email5@domain.com" + r.nextInt(1000000), "password");
        generalUser.insertUser(user);
        user.setId(generalUser.getIdFromUser(user.getUsername()));
        return user;
    }

    /**
     * Create a test admin with semi-random attributes
     * @param generalAdmin Server access object
     * @return The created admin
     * @throws SQLException
     */
    public static Admin insertAdmin(GeneralAdmin generalAdmin)  throws SQLException {
        Admin admin = new Admin("username" + r.nextInt(1000000), "password" + r.nextInt(1000000), "Full Name" + r.nextInt(1000000));
        generalAdmin.insertAdmin(admin);
        admin.setStaffID(generalAdmin.getAdminIdFromUsername(admin.getUsername()));
        return admin;
    }

    /**
     * Create a test clinician with semi-random attributes
     * @param generalClinician Server access object
     * @return The created clinician
     * @throws SQLException
     */
    public static Clinician insertClinician(GeneralClinician generalClinician) throws SQLException {
        Clinician Clinician = new Clinician("username" + r.nextInt(1000000), "password" + r.nextInt(1000000), "Full Name" + r.nextInt(1000000));
        generalClinician.insertClinician(Clinician);
        Clinician.setStaffID(generalClinician.getClinicianIdFromUsername(Clinician.getUsername()));
        return Clinician;
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

    public static ArrayList<Procedure> makeProcedures() {
        ArrayList<Procedure> procedures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ArrayList<Organ> organs = new ArrayList<>(Arrays.asList(Organ.BONE, Organ.CORNEA, Organ.PANCREAS));
            Procedure p = new Procedure("Summary" + r.nextInt(1000000), "description" + r.nextInt(1000000), LocalDate.of(2013 + r.nextInt(10), 3, 14), organs, i);
            procedures.add(p);
        }
        return procedures;
    }
}
