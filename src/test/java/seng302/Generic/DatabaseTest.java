package seng302.Generic;

import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng302.User.Attribute.AlcoholConsumption;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.lang.Math.toIntExact;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DatabaseTest {


    private Connection connection;
    private Database database;

    @BeforeClass
    void getConnection() {
        database = new Database();

        try {
            database.resetDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String testDatabase = "seng302-2018-team300-test";
        String username = "seng302-team300";
        String password = "WeldonAside5766";
        String url = "jdbc:mysql://mysql2.csse.canterbury.ac.nz/";
        String jdbcDriver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(
                    url + testDatabase, username, password);
            System.out.println("Connected to test database");
            System.out.println(LocalDateTime.now());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        try {
            database.resetDatabase();
            database.loadSampleData();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

    /**
     * Insert a user then query the users, asserting that it is the previously inserted user
     */
    @Test
    void insertUser() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser, queriedUser);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user then a WaitingListItem, then query the DB
     */
    @Test
    void insertWaitingListItem() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");
        WaitingListItem testWaitingListItem = new WaitingListItem(Organ.KIDNEY, LocalDate.now(), testUser.getId(), 100);


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
    void updateUserAccountSettings() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser, queriedUser);

            testUser.setUsername("dr.bdong");

            database.updateUserAccountSettings(testUser, toIntExact(testUser.getId()));
            queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser, queriedUser);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user, modify profile attributes, check that is IS NOT reflected in DB, then check after
     * running updateUserAttributesAndOrgans()
     */
    @Test
    void updateUserAttributesAndOrgans() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");

        try {
            database.insertUser(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser, queriedUser);

            testUser.setAlcoholConsumption(AlcoholConsumption.VERYHIGH);
            testUser.setGender(Gender.FEMALE);

            database.updateUserAccountSettings(testUser, toIntExact(testUser.getId()));
            queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser, queriedUser);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user with a test procedure, check that is IS NOT reflected in DB, then check after
     * running updateUserProcedures()
     */
    @Test
    void updateUserProcedures() {
        User testUser = new User("Bobby", new String[]{"Dongeth"}, "Flame", LocalDate.now(),
                "bdong", "flameman@hotmail.com", "password");
        ArrayList<Organ> testOrgansAffected = new ArrayList<>();
        testOrgansAffected.add(Organ.LIVER);
        Procedure testProcedure = new Procedure("Liver transplant","Bobby being the loose unit " +
                "he is has burned out his current liver.", LocalDate.now(), testOrgansAffected);
        ArrayList<Procedure> testPendingProcedures = new ArrayList<>();
        testPendingProcedures.add(testProcedure);
        testUser.setPendingProcedures(testPendingProcedures);

        try {
            database.insertUser(testUser);
            User queriedUser = database.getAllUsers().get(0);
            assertEquals(new ArrayList<Procedure>(), queriedUser.getPendingProcedures());

            database.updateUserProcedures(testUser);

            queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser.getPendingProcedures(), queriedUser.getPendingProcedures());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Insert a user with test diseases, check that is IS NOT reflected in DB, then check after
     * running updateUserDiseases()
     */
    @Test
    void updateUserDiseases() {
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

            database.updateUserProcedures(testUser);

            queriedUser = database.getAllUsers().get(0);
            assertEquals(testUser.getCurrentDiseases(), queriedUser.getCurrentDiseases());
            assertEquals(testUser.getCuredDiseases(), queriedUser.getCuredDiseases());

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Test
    void updateUserMedications() {
    }

    @Test
    void checkUniqueUser() {
    }

    @Test
    void insertClinician() {
    }

    @Test
    void updateClinicianDetails() {
    }

    @Test
    void updateClinicianAccountSettings() {
    }

    @Test
    void insertAdmin() {
    }

    @Test
    void updateAdminDetails() {
    }

    @Test
    void loginUser() {
    }

    @Test
    void getUserFromResultSet() {
    }

    @Test
    void refreshUserWaitinglists() {
    }

    @Test
    void transplantDeregister() {
    }

    @Test
    void loginClinician() {
    }

    @Test
    void getClinicianFromResultSet() {
    }

    @Test
    void loginAdmin() {
    }

    @Test
    void getAdminFromResultSet() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void getAllClinicians() {
    }

    @Test
    void getAllAdmins() {
    }

    @Test
    void removeUser() {
    }

    @Test
    void removeClinician() {
    }

    @Test
    void removeAdmin() {
    }

    @Test
    void resetDatabase() {
    }

    @Test
    void loadSampleData() {
    }
}