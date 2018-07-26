package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.User.Attribute.BloodType;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UserTest {
    private User testUser;

    @Before
    public void setup() {
        testUser = new User("Test er", new String[]{"Middle", "Names"}, "lName", LocalDate.parse("21/02/1890", User.dateFormat), null, Gender.NONBINARY,
        1.8, 64, BloodType.O_POS, "Canterbury", "12345 Ilam Road", "tstr1", "tester@test.com", "testeriscool", "New Zealand",
                null, null, null);
    }

    @Test
    public void testAddOrgan() {
        testUser.setOrgan(Organ.KIDNEY);
        assertTrue(testUser.getOrgans().contains(Organ.KIDNEY));
    }

    @Test
    public void testRemoveOrgan() {
        testUser.setOrgan(Organ.KIDNEY);
        testUser.removeOrgan(Organ.KIDNEY);
        assertTrue(testUser.getOrgans().isEmpty());
    }

    @Test
    public void testIsDonor_emptyOrganList_returnsFalse() {
        testUser.getOrgans().clear();
        assertFalse(testUser.isDonor());
    }

    @Test
    public void testIsDonor_nonEmptyOrganList_returnsTrue() {
        testUser.setOrgan(Organ.LIVER);
        assertTrue(testUser.isDonor());
    }

    @Test
    public void testIsReceiver_emptyWaitingList_returnsFalse() {
        testUser.getWaitingListItems().clear();
        assertFalse(testUser.isReceiver());
    }

    @Test
    public void testIsReceiver_registeredOrgansInWaitingList_returnsTrue() {
        WaitingListItem newItem = new WaitingListItem("","",0, Organ.LIVER);
        newItem.registerOrgan();
        testUser.getWaitingListItems().add(newItem);
        assertTrue(testUser.isReceiver());
    }

    @Test
    public void testRemoveWaitingListItem() {
        WaitingListItem newItem = new WaitingListItem("","",0, Organ.LIVER);
        newItem.registerOrgan();
        testUser.getWaitingListItems().add(newItem);
        testUser.removeWaitingListItem(Organ.LIVER);
        assertFalse(testUser.isReceiver());
    }

    @Test
    public void testConflictingOrgans_noConflictingOrgans_returnsEmptySet(){
        User user = new User("test user", LocalDate.now());
        user.getWaitingListItems().add(
                new WaitingListItem("","",-1,Organ.HEART));
        user.getOrgans().add(Organ.KIDNEY);
        assertTrue(user.conflictingOrgans().isEmpty());
    }

    @Test
    public void testConflictingOrgans_conflictingOrgans_returnsConflictingOrgans(){
        User user = new User("test user", LocalDate.now());
        user.getWaitingListItems().add(new WaitingListItem("","",-1,Organ.KIDNEY));
        user.getWaitingListItems().add(new WaitingListItem("","",-1,Organ.HEART));
        user.getOrgans().add(Organ.KIDNEY);
        user.getOrgans().add(Organ.HEART);
        assertEquals(new HashSet<Organ>(Arrays.asList(Organ.KIDNEY,Organ.HEART)),user.conflictingOrgans());
    }

    @Test
    public void testAttributeChecker() {
        User testUser1 = new User("first", "last", LocalDate.parse("21/02/1890", User.dateFormat), null, Gender.MALE,
                Gender.MALE, BloodType.AB_NEG, 2, 55, "somethingroad", "christchurch",
                "city", 1234, "country", "033145253", "+64113021452", "a@a.com");
        User testUser2 = new User(testUser1);
        assertTrue(testUser1.attributeFieldsEqual(testUser2));
    }

    @Test
    public void testAttributeCopy() {
        User user = new User("test user", LocalDate.now());
        user.copyFieldsFrom(testUser);
        assertTrue(user.attributeFieldsEqual(testUser));
    }

    @Test
    public void testUserTypeCheckerDonor() {
        testUser.setOrgan(Organ.KIDNEY);
        assertEquals("Donor", testUser.getType());
    }

    @Test
    public void testUserTypeCheckerReceiver() {
        testUser.getWaitingListItems().add(new WaitingListItem("","",-1,Organ.KIDNEY));
        assertEquals("Receiver", testUser.getType());
    }

    @Test
    public void testUserTypeCheckerDonorReceiver() {
        testUser.setOrgan(Organ.KIDNEY);
        testUser.getWaitingListItems().add(new WaitingListItem("","",-1,Organ.KIDNEY));
        assertEquals("Donor/Receiver", testUser.getType());
    }

    @Test
    public void testUserTypeCheckerNoType() {
        assertEquals("", testUser.getType());
    }

    @Test
    public void testCLIUserFormat() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = now.toLocalDate();
        User user = new User("test user", nowDate);
        assertEquals("User (ID 1) created at " + now.format(User.dateTimeFormat) + " "
                + "\n-Name: test user"
                + "\n-Preferred Name: test user"
                + "\n-Date of Birth: " + nowDate.format(User.dateFormat)
                + "\n-Date of death: null"
                + "\n-Gender: null"
                + "\n-Height: null"
                + "\n-Weight: null"
                + "\n-Blood type: null"
                + "\n-Region: "
                + "\n-Current address: "
                + "\n-Last Modified: null"
                + "\n-Organs to donate: [].", user.toString());

    }
}
