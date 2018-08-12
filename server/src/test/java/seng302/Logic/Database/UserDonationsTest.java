package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.Attribute.Organ;
import seng302.Model.User;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class UserDonationsTest extends GenericTest {

    private UserDonations userDonations = new UserDonations();
    private GeneralUser generalUser = new GeneralUser();

    @Test
    public void getAllUserDonations() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.getOrgans().add(Organ.BONE);
        generalUser.patchEntireUser(user, (int)user.getId(), true);
        assertEquals(user.getOrgans(), userDonations.getAllUserDonations((int) user.getId()));
    }

    @Test
    public void getAllDonations() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.getOrgans().add(Organ.BONE);
        generalUser.patchEntireUser(user, (int)user.getId(), true);

        user = HelperMethods.insertUser(generalUser);
        user.getOrgans().add(Organ.INTESTINE);
        user.getOrgans().add(Organ.KIDNEY);
        generalUser.patchEntireUser(user, (int)user.getId(), true);

        Set<Organ> expected = new HashSet<Organ>();
        expected.add(Organ.BONE);
        expected.add(Organ.INTESTINE);
        expected.add(Organ.KIDNEY);
        assertEquals(expected, userDonations.getAllDonations());
    }

    @Test
    public void insertDonation() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.getOrgans().add(Organ.BONE);
        userDonations.insertDonation(Organ.BONE, (int)user.getId());
        assertEquals(user.getOrgans(), userDonations.getAllUserDonations((int) user.getId()));
    }

    @Test
    public void getDonationListItemFromName() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.getOrgans().add(Organ.BONE);
        generalUser.patchEntireUser(user, (int)user.getId(), true);
        assertEquals(Organ.BONE, userDonations.getDonationListItemFromName("BONE-MARROW", (int)user.getId()));
    }

    @Test
    public void removeDonationListItem() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.getOrgans().add(Organ.BONE);
        generalUser.patchEntireUser(user, (int)user.getId(), true);
        userDonations.removeDonationListItem((int)user.getId(), "BONE-MARROW");
        user.getOrgans().remove(Organ.BONE);
        assertEquals(user.getOrgans(), userDonations.getAllUserDonations((int) user.getId()));
    }

    @Test
    public void removeAllUserDonations() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.getOrgans().add(Organ.BONE);
        user.getOrgans().add(Organ.KIDNEY);
        user.getOrgans().add(Organ.SKIN);
        generalUser.patchEntireUser(user, (int)user.getId(), true);
        userDonations.removeAllUserDonations((int)user.getId());
        assertEquals(new HashSet<>(), userDonations.getAllUserDonations((int) user.getId()));
    }
}