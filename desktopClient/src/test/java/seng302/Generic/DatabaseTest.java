package seng302.Generic;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.User.Admin;
import seng302.User.Attribute.AlcoholConsumption;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.Disease;
import seng302.User.Medication.Medication;
import seng302.User.Procedure;
import seng302.User.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import seng302.User.WaitingListItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatabaseTest {


    private static Connection connection;
    private static Database database = new Database();


    @Before
    public void setUp() {
        database.connectToDatabase();
        try {
            database.resetDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            database.resetDatabase();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

    /**
     * Insert a user then query the users, asserting that it is the previously inserted user
     */
    @Test
    public void insertUser() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser.getName(), queriedUser.getName());
            assertEquals(testUser.getUsername(), queriedUser.getUsername());
            assertEquals(testUser.getEmail(), queriedUser.getEmail());
            assertEquals(testUser.getPassword(), queriedUser.getPassword());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user then a WaitingListItem, then query the DB
     */
    @Test
    public void insertWaitingListItem() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");
        WaitingListItem testWaitingListItem = new WaitingListItem(
                testUser.getName(), testUser.getRegion(), testUser.getId(),Organ.KIDNEY);

        try {
            database.insertUser(testUser);
            database.insertWaitingListItem(testUser, testWaitingListItem);

            User queriedUser = database.getAllUsers().get(0);
            assertEquals(testWaitingListItem, queriedUser.getWaitingListItems().get(0));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user, modify account settings, check that is IS NOT reflected in DB, then check after
     * running updateUserAccountSettings()
     */
    @Test
    public void updateUserAccountSettings() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);

            testUser.setUsername("dr.bdong");
            testUser.setEmail("flameman21@gmail.com");
            testUser.setPassword("password123");

            database.updateUserAccountSettings(testUser, database.getUserId(testUser.getUsername()));
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser.getUsername(), queriedUser.getUsername());
            assertEquals(testUser.getEmail(), queriedUser.getEmail());
            assertEquals(testUser.getPassword(), queriedUser.getPassword());

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user, modify profile attributes, check that is IS NOT reflected in DB, then check after
     * running updateUserAttributesAndOrgans()
     */
    @Test
    public void updateUserAttributesAndOrgans() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);

            testUser.setAlcoholConsumption(AlcoholConsumption.VERYHIGH);
            testUser.setGender(Gender.FEMALE);

            database.updateUserAttributesAndOrgans(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser.getAlcoholConsumption(), queriedUser.getAlcoholConsumption());
            assertEquals(testUser.getGender(), queriedUser.getGender());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user with a test procedure, check that is IS NOT reflected in DB, then check after
     * running updateUserProcedures()
     */
    @Test
    public void updateUserProcedures() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");
        ArrayList<Organ> testOrgansAffected = new ArrayList<>();
        testOrgansAffected.add(Organ.LIVER);
        Procedure testUpcomingProcedure = new Procedure("Liver transplant","Bobby being the loose unit " +
                "he is, has burned out his current liver.", LocalDate.of(2029, 01, 23), testOrgansAffected);
        Procedure testPreviousProcedure = new Procedure("Toe swap","The ol' 1-2", LocalDate.now(),
                testOrgansAffected);

        ArrayList<Procedure> testPendingProcedures = new ArrayList<>();
        ArrayList<Procedure> testPreviousProcedures = new ArrayList<>();


        testPendingProcedures.add(testUpcomingProcedure);
        testPreviousProcedures.add(testPreviousProcedure);

        testUser.getPendingProcedures().addAll(testPendingProcedures);
        testUser.getPreviousProcedures().addAll(testPreviousProcedures);

        try {
            database.insertUser(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(new ArrayList<Procedure>(), queriedUser.getPendingProcedures());
            assertEquals(new ArrayList<Procedure>(), queriedUser.getPreviousProcedures());

            database.updateUserProcedures(testUser);

            queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser.getPendingProcedures().get(0).getDescription(), queriedUser.getPendingProcedures().get(0).getDescription());
            assertEquals(testUser.getPreviousProcedures().get(0).getDescription(), queriedUser.getPreviousProcedures().get(0).getDescription());

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user with test diseases, check that is IS NOT reflected in DB, then check after
     * running updateUserDiseases()
     */
    @Test
    public void updateUserDiseases() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        ArrayList<Disease> testCuredDiseases = new ArrayList<>();
        ArrayList<Disease> testCurrentDiseases = new ArrayList<>();

        testCurrentDiseases.add(new Disease("Liver disease", LocalDate.now(),
                true, false));
        testCuredDiseases.add(new Disease("Asthma", LocalDate.now(),
                false, true));

        testUser.setCuredDiseases(testCuredDiseases);
        testUser.setCurrentDiseases(testCurrentDiseases);

        try {
            database.insertUser(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(new ArrayList<Disease>(), queriedUser.getCurrentDiseases());
            assertEquals(new ArrayList<Disease>(), queriedUser.getCuredDiseases());

            database.updateUserDiseases(testUser);

            queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser.getCurrentDiseases().get(0).getName(),
                    queriedUser.getCurrentDiseases().get(0).getName());
            assertEquals(testUser.getCurrentDiseases().get(0).getDiagnosisDate(),
                    queriedUser.getCurrentDiseases().get(0).getDiagnosisDate());

            assertEquals(testUser.getCuredDiseases().get(0).getName(),
                    queriedUser.getCuredDiseases().get(0).getName());
            assertEquals(testUser.getCuredDiseases().get(0).getDiagnosisDate(),
                    queriedUser.getCuredDiseases().get(0).getDiagnosisDate());

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user with test medications, check that is IS NOT reflected in DB, then check after
     * running updateUserMedications()
     */
    @Test
    public void updateUserMedications() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");
        ArrayList<Medication> testCurrentMedications = new ArrayList<>();
        ArrayList<Medication> testHistoricMedications = new ArrayList<>();

        Medication historicMedication = new Medication("Magic beans", new String[]{"Test"}, new ArrayList<String>());
        historicMedication.startedTaking();
        historicMedication.stoppedTaking();
        testHistoricMedications.add(historicMedication);

        Medication currentMedication = new Medication("Super Male Vitality", new String[]{"Test"}, new ArrayList<String>());
        currentMedication.startedTaking();
        testCurrentMedications.add(currentMedication);


        testUser.setHistoricMedications(testHistoricMedications);
        testUser.setCurrentMedications(testCurrentMedications);

        try {
            database.insertUser(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(new ArrayList<Medication>(), queriedUser.getCurrentMedications());
            assertEquals(new ArrayList<Medication>(), queriedUser.getHistoricMedications());

            database.updateUserMedications(testUser);

            queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser.getCurrentMedications().get(0).getName(), queriedUser.getCurrentMedications().get(0).getName());
            assertEquals(testUser.getHistoricMedications().get(0).getName(), queriedUser.getHistoricMedications().get(0).getName());

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void checkUniqueUser() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);
            assertFalse(database.isUniqueUser("bdong"));
            assertFalse(database.isUniqueUser("flameman@hotmail.com"));
            assertTrue(database.isUniqueUser("jsmith"));
            assertTrue(database.isUniqueUser("johnsmith@msn.com"));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void checkUniqueClinician() {
        Clinician testClinician = new Clinician("drflame", "password", "Dr. Dong",
                ProfileType.CLINICIAN);

        try {
            database.insertClinician(testClinician);
            assertFalse(database.isUniqueUser("drflame"));
            assertTrue(database.isUniqueUser("drjohnsmith"));
            assertTrue(database.isUniqueUser("johnsmith@msn.ac.com"));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void checkUniqueAdmin() {
        Admin testAdmin = new Admin("Xx_bobbythetechsupport007_xX", "password", "Flame, Bobby");

        try {
            database.insertAdmin(testAdmin);
            assertFalse(database.isUniqueUser("Xx_bobbythetechsupport007_xX"));
            assertTrue(database.isUniqueUser("drjohnsmith"));
            assertTrue(database.isUniqueUser("johnsmith@msn.ac.com"));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void insertClinician() {
        Clinician testClinician = new Clinician("drflame", "password", "Dr. Dong",
                ProfileType.CLINICIAN);

        try {
            database.insertClinician(testClinician);
            Clinician queriedClinician = database.getAllClinicians().get(1);
            assertEquals(testClinician.getName(), queriedClinician.getName());
            assertEquals(testClinician.getUsername(), queriedClinician.getUsername());
            assertEquals(testClinician.getPassword(), queriedClinician.getPassword());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void updateClinicianDetails() {
        Clinician testClinician = new Clinician("drflame", "password", "Dr. Dong",
                ProfileType.CLINICIAN);

        try {
            database.insertClinician(testClinician);

            testClinician.setName("Actually it's Professor Dong");
            testClinician.setWorkAddress("321 Ekaf Avenue");
            testClinician.setRegion("Roundtown");

            database.updateClinician(testClinician);
            Clinician queriedClinician = database.getAllClinicians().get(1);
            assertEquals(testClinician.getName(), queriedClinician.getName());
            assertEquals(testClinician.getWorkAddress(), queriedClinician.getWorkAddress());
            assertEquals(testClinician.getRegion(), queriedClinician.getRegion());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void updateClinicianAccountSettings() {
        Clinician testClinician = new Clinician("drflame", "password", "Dr. Dong",
                ProfileType.CLINICIAN);

        try {
            database.insertClinician(testClinician);

            testClinician.setUsername("profflame");
            testClinician.setPassword("Password123");

            database.updateClinician(testClinician);
            Clinician queriedClinician = database.getAllClinicians().get(1);
            assertEquals(testClinician.getUsername(), queriedClinician.getUsername());
            assertEquals(testClinician.getPassword(), queriedClinician.getPassword());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void insertAdmin() {
        Admin testAdmin = new Admin("Xx_bobbythetechsupport007_xX", "password", "Flame, Bobby");

        try {
            database.insertAdmin(testAdmin);
            Admin queriedAdmin = database.getAllAdmins().get(1);
            assertEquals(testAdmin.getUsername(), queriedAdmin.getUsername());
            assertEquals(testAdmin.getPassword(), queriedAdmin.getPassword());
            assertEquals(testAdmin.getName(), queriedAdmin.getName());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void updateAdminDetails() {
        Admin testAdmin = new Admin("Xx_bobbythetechsupport007_xX", "password", "Flame, Bobby");

        try {
            database.insertAdmin(testAdmin);

            testAdmin.setName("B. D. Flame");
            testAdmin.setWorkAddress("NSA HQ");

            database.updateAdminDetails(testAdmin);
            Admin queriedAdmin = database.getAllAdmins().get(1);
            assertEquals(testAdmin.getName(), queriedAdmin.getName());
            assertEquals(testAdmin.getWorkAddress(), queriedAdmin.getWorkAddress());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void loginUser() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);

            User loggedInA = new Gson().fromJson(database.loginUser("bdong", "password").getAsJsonObject(), User.class);
            assertEquals(testUser.getEmail(), loggedInA.getEmail());

            User loggedInB = new Gson().fromJson(database.loginUser("flameman@hotmail.com", "password").getAsJsonObject(), User.class);
            assertEquals(testUser.getUsername(), loggedInB.getUsername());

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void refreshUserWaitinglists() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);
            assertEquals(new ArrayList<>(), database.getAllUsers().get(0).getWaitingListItems());

            database.insertWaitingListItem(testUser,
                    new WaitingListItem(database.getAllUsers().get(0).getName(), database.getAllUsers().get(0).getRegion(),
                            database.getAllUsers().get(0).getId(), Organ.BONE)
            );

            User queriedUser = database.getAllUsers().get(0);
            assertTrue(queriedUser.getWaitingListItems().get(0).getStillWaitingOn());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void transplantDeregister() {
        //TODO
    }

    @Test
    public void loginClinician() {
        Clinician testClinician = new Clinician("drflame", "password", "Dr. Dong",
                ProfileType.CLINICIAN);
        try {
            database.insertClinician(testClinician);
            assertEquals(testClinician.getName(),
                    database.loginClinician("drflame", "password").getName());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void loginAdmin() {
        Admin testAdmin = new Admin("Xx_bobbythetechsupport007_xX", "password", "Flame, Bobby");
        try {
            database.insertAdmin(testAdmin);
            assertEquals(testAdmin.getName(),
                    database.loginAdmin("Xx_bobbythetechsupport007_xX", "password").getName());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    

    @Test
    public void removeUser() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);
            database.removeUser(testUser);
            assertEquals(new ArrayList<User>(), database.getAllUsers());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void removeClinician() {
        Clinician testClinician = new Clinician("drflame", "password", "Dr. Dong",
                ProfileType.CLINICIAN);

        try {
            database.insertClinician(testClinician);
            database.removeClinician(testClinician);
            // Only the default clinician remains
            assertEquals(1, database.getAllClinicians().size());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void removeAdmin() {
        Admin testAdmin = new Admin("Xx_bobbythetechsupport007_xX", "password", "Flame, Bobby");

        try {
            database.insertAdmin(testAdmin);
            database.removeAdmin(testAdmin);
            // Only the default admin remains
            assertEquals(1, database.getAllAdmins().size());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void resetDatabase() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");
        Admin testAdmin = new Admin("Xx_bobbythetechsupport007_xX", "password", "Flame, Bobby");
        Clinician testClinician = new Clinician("drflame", "password", "Dr. Dong",
                ProfileType.CLINICIAN);

        try {
            database.insertUser(testUser);
            database.insertClinician(testClinician);
            database.insertAdmin(testAdmin);

            database.resetDatabase();

            assertEquals(0, database.getAllUsers().size());
            assertEquals(1, database.getAllClinicians().size());
            assertEquals(1, database.getAllAdmins().size());

            assertEquals("default", database.getAllClinicians().get(0).getUsername());
            assertEquals("admin", database.getAllAdmins().get(0).getUsername());
            assertEquals(1, database.getAllClinicians().get(0).getStaffID());
            assertEquals(1, database.getAllAdmins().get(0).getStaffID());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    public void loadSampleData() {
        try {
            database.loadSampleData();
            assertEquals(8, database.getAllUsers().size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}