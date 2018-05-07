package seng302.TestFX;

import static org.testfx.api.FxAssert.verifyThat;

import java.util.EnumSet;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.Generic.DataManager;
import seng302.User.Attribute.Organ;

public class RevisedNameandGenderTest extends TestFXTest {

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    private void enterAttributesPanel() {

        DataManager.users.clear();
        addTestUser();
        loginAs(DataManager.users.get(0));

        clickOn("#userAttributesButton");
    }


    @Test
    public void changeNameTest() {

        enterAttributesPanel();

        verifyThat("#userDisplayText", LabeledMatchers.hasText("Currently logged in as: Bobby Dong Flame"));
        verifyThat("#settingAttributesLabel", LabeledMatchers.hasText("Attributes for Bobby Dong Flame"));

        //Add a new medication for the user.
        doubleClickOn("#firstNameField").write("New");
        doubleClickOn("#middleNameField").write("Name");
        doubleClickOn("#lastNameField").write("Test");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.ENTER);
        Assert.assertEquals(DataManager.users.get(0).getName(), "Bobby Dong Flame");
        Assert.assertEquals(DataManager.users.get(0).getPreferredName(), "New Name Test");
        verifyThat("#userDisplayText", LabeledMatchers.hasText("Currently logged in as: New Name Test"));
        verifyThat("#settingAttributesLabel", LabeledMatchers.hasText("Attributes for New Name Test"));

    }

    @Test
    public void changeGenderTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getGender(), null);
        Assert.assertEquals(DataManager.users.get(0).getGenderIdentity(), null);

        clickOn("#genderComboBox").clickOn("Male");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getGender().toString(), "Male");
        Assert.assertEquals(DataManager.users.get(0).getGenderIdentity().toString(), "Male");

        clickOn("#genderComboBox").clickOn("Female");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getGender().toString(), "Male");
        Assert.assertEquals(DataManager.users.get(0).getGenderIdentity().toString(), "Female");

    }

    @Test
    public void changeAddressTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getCurrentAddress(), null);

        doubleClickOn("#addressField").write("3 Test Street");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getCurrentAddress(), "3 Test Street");

        clickOn("#addressField");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#addressField").write("8 Trial Road");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getCurrentAddress(), "8 Trial Road");
    }

    @Test
    public void changeRegionTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getRegion(), null);

        doubleClickOn("#regionField").write("Testchurch");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getRegion(), "Testchurch");

        doubleClickOn("#regionField").write("Trialton");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getRegion(), "Trialton");
    }

    @Test
    public void changeDateOfBirthTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getDateOfBirth().toString(), "1969-08-04");

        clickOn("#dateOfBirthPicker");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        doubleClickOn("#dateOfBirthPicker").write("01/05/1970");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getDateOfBirth().toString(), "1970-05-01");

        clickOn("#dateOfBirthPicker");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        doubleClickOn("#dateOfBirthPicker").write("10/07/1997");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getDateOfBirth().toString(), "1997-07-10");
    }

    @Test
    public void changeDateOfDeathTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getDateOfDeath(), null);

        clickOn("#dateOfDeathPicker");
        doubleClickOn("#dateOfDeathPicker").write("01/05/1970");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getDateOfDeath().toString(), "1970-05-01");

        clickOn("#dateOfDeathPicker");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        doubleClickOn("#dateOfDeathPicker").write("10/07/1997");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getDateOfDeath().toString(), "1997-07-10");
    }


    @Test
    public void changeBloodTypeTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getBloodType(), null);

        clickOn("#bloodTypeComboBox").clickOn("O+");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getBloodType().toString(), "O+");

        clickOn("#bloodTypeComboBox").clickOn("AB-");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getBloodType().toString(), "AB-");

    }

    @Test
    public void changeOrgansToDonate() {

        enterAttributesPanel();

        EnumSet<Organ> organs = EnumSet.noneOf(Organ.class);

        Assert.assertEquals(DataManager.users.get(0).getOrgans(), organs);

        clickOn("#liverCheckBox");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        organs.add(Organ.LIVER);
        Assert.assertEquals(DataManager.users.get(0).getOrgans(), organs);

        clickOn("#kidneyCheckBox");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        organs.add(Organ.KIDNEY);
        Assert.assertEquals(DataManager.users.get(0).getOrgans(), organs);

        clickOn("#kidneyCheckBox");
        clickOn("#liverCheckBox");
        clickOn("#connectiveTissueCheckBox");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        organs = EnumSet.noneOf(Organ.class);
        organs.add(Organ.TISSUE);
        Assert.assertEquals(DataManager.users.get(0).getOrgans(), organs);

    }

    @Test
    public void changeSmokerStatusTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getSmokerStatus(), null);

        clickOn("#smokerStatusComboBox").clickOn("Never");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getSmokerStatus().toString(), "Never");

        clickOn("#smokerStatusComboBox").clickOn("Current");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getSmokerStatus().toString(), "Current");

    }

    @Test
    public void changeBloodPressureTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getBloodPressure(), "");

        doubleClickOn("#bloodPressureTextField").write("21/30");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getBloodPressure(), "21/30");

        clickOn("#bloodPressureTextField");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        doubleClickOn("#bloodPressureTextField").write("30/10");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getBloodPressure(), "30/10");
    }

    @Test
    public void changeAlcoholConsumptionTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getAlcoholConsumption(), null);

        clickOn("#alcoholConsumptionComboBox").clickOn("Alcoholic");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getAlcoholConsumption().toString(), "Alcoholic");

        clickOn("#alcoholConsumptionComboBox").clickOn("None");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getAlcoholConsumption().toString(), "None");

    }

    @Test
    public void changeBmiTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getWeight(), -1.0, 0.001);
        Assert.assertEquals(DataManager.users.get(0).getHeight(), -1.0, 0.001);
        verifyThat("#bmiLabel", LabeledMatchers.hasText(""));

        doubleClickOn("#weightField").write("83");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getWeight(), 83, 0.001);
        Assert.assertEquals(DataManager.users.get(0).getHeight(), -1.0, 0.001);
        verifyThat("#bmiLabel", LabeledMatchers.hasText(""));

        doubleClickOn("#heightField").write("178");
        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getWeight(), 83, 0.001);
        Assert.assertEquals(DataManager.users.get(0).getHeight(), 178, 0.001);
        verifyThat("#bmiLabel", LabeledMatchers.hasText("BMI: 26.20"));

        clickOn("#weightField");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#weightField").write("72");

        clickOn("#saveButton");
        sleep(100);
        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(DataManager.users.get(0).getWeight(), 72, 0.001);
        Assert.assertEquals(DataManager.users.get(0).getHeight(), 178, 0.001);
        verifyThat("#bmiLabel", LabeledMatchers.hasText("BMI: 22.72"));
    }

    @Test
    public void testHistoryPaneButtonTest() {

        enterAttributesPanel();

        clickOn("Action History");
        verifyThat("#historyGridPane", Node::isVisible);
    }

    @Ignore
    @Test
    public void changeAccountSettingsTest() {

        enterAttributesPanel();

        Assert.assertEquals(DataManager.users.get(0).getUsername(), "test");
        Assert.assertEquals(DataManager.users.get(0).getEmail(), "testie@testmail.com");
        Assert.assertEquals(DataManager.users.get(0).getPassword(), "password123");

        clickOn("#userAccountSettings");

        sleep(1000);

        write("password123");
        push(KeyCode.ENTER);

        sleep(1000);

        verifyThat("#accountBackground", Node::isVisible);

        clickOn("#usernameField");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#usernameField").write("NewUsername");

        clickOn("#emailField");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#emailField").write("new.email@test.com");

        clickOn("#passwordField");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#passwordField").write("newPassword");

        clickOn("#updateButton");

        push(KeyCode.ENTER);

        Assert.assertEquals(DataManager.users.get(0).getUsername(), "NewUsername");
        Assert.assertEquals(DataManager.users.get(0).getEmail(), "new.email@test.com");
        Assert.assertEquals(DataManager.users.get(0).getPassword(), "newPassword");
    }


}
