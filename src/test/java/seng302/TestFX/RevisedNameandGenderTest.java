package seng302.TestFX;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.junit.runners.JUnit4;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;
import seng302.User.Attribute.Organ;
import seng302.User.User;
import seng302.Generic.Main;
import seng302.User.Medication.Medication;

import java.util.EnumSet;
import java.util.Iterator;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import static org.testfx.api.FxToolkit.registerPrimaryStage;

public class RevisedNameandGenderTest extends ApplicationTest {

    private static final boolean runHeadless = true;

    private Main mainGUI;



    @Before
    public void setUp () throws Exception {
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start (Stage stage) throws Exception {
        mainGUI = new Main();
        mainGUI.start(stage);
    }

    private void enterAttributesPanel() {

        Main.users.clear();
        // Assumed that calling method is currently on login screen
        clickOn("#createAccountButton");

        // Create a valid user
        clickOn("#usernameInput").write("test");
        clickOn("#emailInput").write("testie@testmail.com");
        clickOn("#passwordInput").write("password123");
        clickOn("#passwordConfirmInput").write("password123");
        clickOn("#firstNameInput").write("Testie");
        clickOn("#middleNamesInput").write("Test");
        clickOn("#lastNameInput").write("McTest");
        clickOn("#dateOfBirthInput").write("20/4/1969");

        doubleClickOn("#createAccountButton");

        // Logout to be able to login as a clinician
        clickOn("#logoutButton");
        clickOn("OK");

        // Login as default clinician
        clickOn("#identificationInput");
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");


        //Click on the Created User in clinician table and enter the medications panel.
        doubleClickOn("Testie Test McTest");
        clickOn("#attributesButton");
    }


    @Test
    public void changeNameTest(){

        enterAttributesPanel();

        verifyThat("#userDisplayText", LabeledMatchers.hasText("Currently logged in as: Testie Test McTest"));
        verifyThat("#settingAttributesLabel", LabeledMatchers.hasText("Attributes for Testie Test McTest"));

        //Add a new medication for the user.
        doubleClickOn("#firstNameField").write("New");
        doubleClickOn("#middleNameField").write("Name");
        doubleClickOn("#lastNameField").write("Test");
        clickOn("#saveButton");

        Assert.assertEquals(Main.users.get(0).getName(), "Testie Test McTest");
        Assert.assertEquals(Main.users.get(0).getPreferredName(),"New Name Test");
        verifyThat("#userDisplayText", LabeledMatchers.hasText("Currently logged in as: New Name Test"));
        verifyThat("#settingAttributesLabel", LabeledMatchers.hasText("Attributes for New Name Test"));

    }

    @Test
    public void changeGenderTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getGender(), null);
        Assert.assertEquals(Main.users.get(0).getGenderIdentity(), null);

        clickOn("#genderComboBox").clickOn("Male");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getGender().toString(), "Male");
        Assert.assertEquals(Main.users.get(0).getGenderIdentity().toString(), "Male");

        clickOn("#genderComboBox").clickOn("Female");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getGender().toString(), "Male");
        Assert.assertEquals(Main.users.get(0).getGenderIdentity().toString(), "Female");

    }

    @Test
    public void changeAddressTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getCurrentAddress(),null);

        doubleClickOn("#addressField").write("3 Test Street");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getCurrentAddress(), "3 Test Street");


        clickOn("#addressField");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#addressField").write("8 Trial Road");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getCurrentAddress(), "8 Trial Road");
    }

    @Test
    public void changeRegionTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getRegion(), null);

        doubleClickOn("#regionField").write("Testchurch");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getRegion(), "Testchurch");

        doubleClickOn("#regionField").write("Trialton");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getRegion(), "Trialton");
    }

    @Test
    public void changeDateOfBirthTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getDateOfBirth().toString(), "1969-04-20");

        clickOn("#dateOfBirthPicker");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        doubleClickOn("#dateOfBirthPicker").write("01/05/1970");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getDateOfBirth().toString(), "1970-05-01");

        clickOn("#dateOfBirthPicker");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        doubleClickOn("#dateOfBirthPicker").write("10/07/1997");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getDateOfBirth().toString(), "1997-07-10");
    }

    @Test
    public void changeDateOfDeathTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getDateOfDeath(), null);

        clickOn("#dateOfDeathPicker");
        doubleClickOn("#dateOfDeathPicker").write("01/05/1970");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getDateOfDeath().toString(), "1970-05-01");

        clickOn("#dateOfDeathPicker");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        doubleClickOn("#dateOfDeathPicker").write("10/07/1997");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getDateOfDeath().toString(), "1997-07-10");
    }

    @Ignore
    @Test
    public void changeHeightTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getHeight(), -1.0,0.001);

        doubleClickOn("#heightField").write("187");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getHeight(), 187,0.001);

        doubleClickOn("#heightField").write("178.5");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getHeight(), 178.5, 0.001);
    }

    @Test
    public void changeWeightTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getWeight(), -1.0,0.001);

        doubleClickOn("#weightField").write("87");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getWeight(), 87,0.001);

        doubleClickOn("#weightField").write("78.5");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getWeight(), 78.5, 0.001);

        Assert.assertEquals(Main.users.get(0).getHeight(), -1.0,0.001);

        doubleClickOn("#heightField").write("187");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getHeight(), 187,0.001);

        doubleClickOn("#heightField").write("178.5");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getHeight(), 178.5, 0.001);
    }

    @Test
    public void changeBloodTypeTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getBloodType(), null);

        clickOn("#bloodTypeComboBox").clickOn("O+");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getBloodType().toString(), "O+");

        clickOn("#bloodTypeComboBox").clickOn("AB-");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getBloodType().toString(), "AB-");

    }

    @Test
    public void changeOrgansToDonate(){

        enterAttributesPanel();

        EnumSet<Organ> organs = EnumSet.noneOf(Organ.class);

        Assert.assertEquals(Main.users.get(0).getOrgans(), organs);

        clickOn("#liverCheckBox");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        organs.add(Organ.LIVER);
        Assert.assertEquals(Main.users.get(0).getOrgans(), organs);

        clickOn("#kidneyCheckBox");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        organs.add(Organ.KIDNEY);
        Assert.assertEquals(Main.users.get(0).getOrgans(), organs);

        clickOn("#kidneyCheckBox");
        clickOn("#liverCheckBox");
        clickOn("#connectiveTissueCheckBox");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        organs = EnumSet.noneOf(Organ.class);
        organs.add(Organ.TISSUE);
        Assert.assertEquals(Main.users.get(0).getOrgans(), organs);

    }

    @Test
    public void changeSmokerStatusTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getSmokerStatus(), null);

        clickOn("#smokerStatusComboBox").clickOn("Never");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getSmokerStatus().toString(), "Never");

        clickOn("#smokerStatusComboBox").clickOn("Current");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getSmokerStatus().toString(), "Current");

    }

    @Test
    public void changeBloodPressureTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getBloodPressure(), "");

        doubleClickOn("#bloodPressureTextField").write("21/30");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getBloodPressure(), "21/30");

        clickOn("#bloodPressureTextField");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        doubleClickOn("#bloodPressureTextField").write("30/10");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getBloodPressure(), "30/10");
    }

    @Test
    public void changeAlcoholConsumptionTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getAlcoholConsumption(), null);

        clickOn("#alcoholConsumptionComboBox").clickOn("Alcoholic");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getAlcoholConsumption().toString(), "Alcoholic");

        clickOn("#alcoholConsumptionComboBox").clickOn("None");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getAlcoholConsumption().toString(), "None");

    }

    @Test
    public void changeBmiTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getWeight(), -1.0, 0.001);
        Assert.assertEquals(Main.users.get(0).getHeight(), -1.0, 0.001);
        verifyThat("#bmiLabel", LabeledMatchers.hasText(""));

        doubleClickOn("#weightField").write("83");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getWeight(), 83, 0.001);
        Assert.assertEquals(Main.users.get(0).getHeight(), -1.0, 0.001);
        verifyThat("#bmiLabel", LabeledMatchers.hasText(""));

        doubleClickOn("#heightField").write("178");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getWeight(), 83, 0.001);
        Assert.assertEquals(Main.users.get(0).getHeight(), 178, 0.001);
        verifyThat("#bmiLabel", LabeledMatchers.hasText("BMI: 26.20"));

        clickOn("#weightField");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#weightField").write("72");

        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getWeight(), 72, 0.001);
        Assert.assertEquals(Main.users.get(0).getHeight(), 178, 0.001);
        verifyThat("#bmiLabel", LabeledMatchers.hasText("BMI: 22.72"));
    }

    @Test
    public void testHistoryPaneButtonTest(){

        enterAttributesPanel();

        clickOn("Action History");
        verifyThat("#historyGridPane", Node::isVisible);
    }

    @Ignore
    @Test
    public void changeAccountSettingsTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getUsername(), "test");
        Assert.assertEquals(Main.users.get(0).getEmail(), "testie@testmail.com");
        Assert.assertEquals(Main.users.get(0).getPassword(), "password123");

        clickOn("#userAccountSettings");

        sleep(1000);

        write("password123");
        push(KeyCode.ENTER);

        sleep(1000);

        verifyThat("#accountBackground", Node::isVisible);

        clickOn("#usernameField");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#usernameField").write("NewUsername");

        clickOn("#emailField");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#emailField").write("new.email@test.com");

        clickOn("#passwordField");
        push(KeyCode.CONTROL,KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#passwordField").write("newPassword");

        clickOn("#updateButton");

        push(KeyCode.ENTER);

        Assert.assertEquals(Main.users.get(0).getUsername(), "NewUsername");
        Assert.assertEquals(Main.users.get(0).getEmail(), "new.email@test.com");
        Assert.assertEquals(Main.users.get(0).getPassword(), "newPassword");
    }


}
