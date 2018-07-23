package seng302;

import seng302.Logic.Database.GeneralAdmin;
import seng302.Logic.Database.GeneralClinician;
import seng302.Logic.Database.GeneralUser;
import seng302.Model.Admin;
import seng302.Model.Clinician;
import seng302.Model.Disease;
import seng302.Model.Medication.Medication;
import seng302.Model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
        String[] middle = {"Middle" + r.nextInt(1000)};
        User user = new User("First" + r.nextInt(1000), middle, "Last" + r.nextInt(1000), LocalDate.of(1900 + r.nextInt(1000), 8, 4), "username5" + r.nextInt(1000), "email5@domain.com" + r.nextInt(1000), "password");
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
        Admin admin = new Admin("username" + r.nextInt(1000), "password" + r.nextInt(1000), "Full Name" + r.nextInt(1000));
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
        Clinician Clinician = new Clinician("username" + r.nextInt(1000), "password" + r.nextInt(1000), "Full Name" + r.nextInt(1000));
        generalClinician.insertClinician(Clinician);
        Clinician.setStaffID(generalClinician.getClinicianIdFromUsername(Clinician.getUsername()));
        return Clinician;
    }

    /**
     * Create a list of medications with semi-random names and ingredients
     * @return A list of medication objects
     */
    public static List<Medication> makeMedications(){
        List<Medication> medications = new ArrayList<>();
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
}
