package seng302.Logic.Database;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Model.Attribute.BloodType;
import seng302.Model.Attribute.Gender;
import seng302.Model.Medication.Medication;
import seng302.Model.User;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GeneralUserTest {

    private GeneralUser generalUser = new GeneralUser();


    @BeforeClass
    @AfterClass
    public static void reset() throws SQLException {
        Administration administration = new Administration();
        administration.reset();
        administration.resample();
    }

    @Test
    public void patchEntireUser() {
    }

    @Test
    public void updateAllMedications() throws SQLException {
        String[] middle = {"Middle"};
        String[] ingredients = {"water", "Air"};
        User user = new User("First", middle, "Last", LocalDate.of(1997, 8, 4), "username1", "email@domain.com", "password");
        generalUser.insertUser(user);
        int id = generalUser.getIdFromUser(user.getUsername());
        List<Medication> medications = Arrays.asList(new Medication("paracetamol", ingredients), new Medication("placebo", ingredients));
        generalUser.updateAllMedications(medications, id);
        assertEquals(user.getCurrentMedications(), generalUser.getUserFromId(id).getCurrentMedications());
    }

    @Test
    public void updateAllProcedures() {
    }

    @Test
    public void updateAllDiseases() {
    }

    @Test
    public void updateWaitingListItems() {
    }

    @Test
    public void getUsers() {
    }

    @Test
    public void buildUserQuery() {
    }

    @Test
    public void nameFilter() {
    }

    @Test
    public void matchFilter() {
    }

    @Test
    public void ageFilter() {
    }

    @Test
    public void organFilter() {
    }

    @Test
    public void userTypeFilter() {
    }

    @Test
    public void isDonorFilter() {
    }

    @Test
    public void isReceiverFilter() {
    }

    @Test
    public void getUserFromId() {
    }

    @Test
    public void insertUser() throws SQLException {
        String[] middle = {"Middle"};
        User user = new User("First", middle, "Last", LocalDate.of(1997, 8, 4), "username2", "email@domain.com", "password");
        generalUser.insertUser(user);
        long id = generalUser.getIdFromUser(user.getUsername());
        user.setId(id);
        User user2 = generalUser.getUserFromId((int) id);
        assertEquals(user, user2);
    }

    @Test
    public void getIdFromUser() {
    }

    @Test
    public void getUserFromResultSet() {
    }

    @Test
    public void updateUserAttributes() throws SQLException {
        String[] middle = {"Middle"};
        User user = new User("First", middle, "Last", LocalDate.of(1997, 8, 4), "username3", "email3@domain.com", "password");
        generalUser.insertUser(user);
        long id = generalUser.getIdFromUser(user.getUsername());
        user.setId(id);

        user.setCurrentAddress("221b Baker Stret");
        user.setRegion("Here");
        user.setDateOfBirth(LocalDate.of(1987, 8, 4));
        user.setDateOfDeath(LocalDate.now());
        user.setHeight(100);
        user.setWeight(200);
        user.setBloodPressure("High");
        user.setGender(Gender.NONBINARY);
        user.setBloodType(BloodType.AB_POS);
        user.setEmail("newemail@newdomain.newtld");

        generalUser.updateUserAttributes(user, (int) user.getId());

        User user2 = generalUser.getUserFromId((int) id);
        assertEquals(user.getCurrentAddress(), user2.getCurrentAddress());
        assertEquals(user.getRegion(), user2.getRegion());
        assertEquals(user.getDateOfBirth(), user2.getDateOfBirth());
        assertEquals(user.getDateOfDeath(), user2.getDateOfDeath());
        assertEquals(user.getHeight(), user2.getHeight(), 0.01);
        assertEquals(user.getWeight(), user2.getWeight(), 0.01);
        assertEquals(user.getBloodPressure(), user2.getBloodPressure());
        assertEquals(user.getGender(), user2.getGender());
        assertEquals(user.getBloodType(), user2.getBloodType());
        assertEquals(user.getEmail(), user2.getEmail());
    }

    @Test
    public void removeUser() {

    }
}