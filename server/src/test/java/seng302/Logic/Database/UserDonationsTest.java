package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.User;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class UserDonationsTest extends GenericTest {

    private UserDonations userDonations = new UserDonations();
    private GeneralUser generalUser = new GeneralUser();

    @Test
    public void getAllUserDonations() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        userDonations.getAllUserDonations((int) user.getId());
    }

    @Test
    public void getAllDonations() {
    }

    @Test
    public void insertDonation() {
    }

    @Test
    public void getOrganFromResultSet() {
    }

    @Test
    public void getDonationListItemFromName() {
    }

    @Test
    public void removeDonationListItem() {
    }

    @Test
    public void removeAllUserDonations() {
    }
}