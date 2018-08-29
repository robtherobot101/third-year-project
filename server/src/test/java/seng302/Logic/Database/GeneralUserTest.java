package seng302.Logic.Database;

import org.junit.Test;
import seng302.Config.DatabaseConfiguration;
import seng302.HelperMethods;
import seng302.User.Attribute.BloodType;
import seng302.User.Attribute.Gender;
import seng302.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeneralUserTest extends GenericTest {

    private GeneralUser generalUser = new GeneralUser();

    @Test
    public void getUsers() throws SQLException {
        List<User> expected = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            expected.add(HelperMethods.insertUser(generalUser));
        }
        assertTrue(generalUser.getUsers(new HashMap<>()).containsAll(expected));
    }

    @Test
    public void getUserFromId() throws SQLException {
        User expected = HelperMethods.insertUser(generalUser);

        assertEquals(expected, generalUser.getUserFromId((int) expected.getId()));
    }

    @Test
    public void insertUser() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        User user2 = generalUser.getUserFromId((int) user.getId());
        assertEquals(user, user2);
    }

    /**
     * Test the function to get users from a raw resultset object from the database
     * @throws SQLException
     */
    @Test
    public void getUserFromResultSet() throws SQLException {

        User user = HelperMethods.insertUser(generalUser);
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT * FROM USER WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, user.getUsername());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                assertEquals(user, generalUser.getUserFromResultSet(resultSet));
                return;
            }
        }
        fail();
    }

    @Test
    public void updateUserAttributes() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);

        user.setCurrentAddress("221b Baker Stret");
        user.setRegion("Here");
        user.setDateOfBirth(LocalDate.of(1987, 8, 4));
        LocalDateTime dateOfDeath = LocalDateTime.of(LocalDate.of(2017, 8, 4), LocalTime.of(13, 1, 1));
        user.setDateOfDeath(dateOfDeath);
        user.setHeight(100);
        user.setWeight(200);
        user.setBloodPressure("High");
        user.setGender(Gender.NONBINARY);
        user.setBloodType(BloodType.AB_POS);
        //user.setEmail("newemail@newdomain.newtld");

        generalUser.updateUserAttributes(user, (int) user.getId());

        User user2 = generalUser.getUserFromId((int) user.getId());
        System.out.println(user2.getDateOfDeath().toString());
        System.out.println(user.getDateOfDeath().toString());

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
    public void removeUser() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);

        int numBefore = generalUser.getUsers(new HashMap<>()).size();
        generalUser.removeUser(user);
        int numAfter = generalUser.getUsers(new HashMap<>()).size();

        assertEquals(numBefore - 1, numAfter);
        assertFalse(generalUser.getUsers(new HashMap<>()).contains(user));

    }
}