package seng302.TestFX;

import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.Core.TransplantWaitingListItem;
import seng302.Generic.Main;
import seng302.User.Attribute.Organ;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
import static org.junit.Assert.assertEquals;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class TransplantWaitingListTest extends ApplicationTest{
    private static final boolean runHeadless = false;

    private TableView<TransplantWaitingListItem> transplantTable;
    private TransplantWaitingListItem transplantRow;

    /**
     * Ensures the tests are run in background if the property runHeadless == true
     *
     * Note: tests still take the same amount of time in background
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setupSpec() throws Exception {
        if (runHeadless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("headless.geometry", "1600x1200-32");
        }
        registerPrimaryStage();
    }


    @After
    public void tearDown () throws Exception {
        Main.users.clear();
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start (Stage stage) throws Exception {
        Main mainGUI = new Main();
        mainGUI.start(stage);
        Main.users.clear();
    }

    /**
     * Refreshes the currently selected reciever in both tables of Medical History
     */
    private void refreshTableSelections() {
        transplantTable = lookup("#transplantTable").query();
        transplantRow = transplantTable.getSelectionModel().getSelectedItem();
    }


    /**
     * helper function to create two new users. one a receiver and one a dummy
     */
    private void createAccounts() {
        Main.users.clear();
        // Assumed that calling method is currently on login screen
        clickOn("#createAccountButton");

        // Create a valid user
        clickOn("#usernameInput").write("bflame");
        clickOn("#emailInput").write("flameman@hotmail.com");
        clickOn("#passwordInput").write("password123");
        clickOn("#passwordConfirmInput").write("password123");
        clickOn("#firstNameInput").write("Bobby");
        clickOn("#middleNamesInput").write("Dong");
        clickOn("#lastNameInput").write("Flame");
        clickOn("#dateOfBirthInput").write("4/8/1969");

        doubleClickOn("#createAccountButton");

        // Logout to be able to create another account
        clickOn("#logoutButton");
        clickOn("OK");

        // Assumed that calling method is currently on login screen
        clickOn("#createAccountButton");


        // Create a valid user
        clickOn("#usernameInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("bobr");
        clickOn("#emailInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("bob@live.com");
        clickOn("#passwordInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("password");
        clickOn("#passwordConfirmInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("password");
        clickOn("#firstNameInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("Bob");
        clickOn("#middleNamesInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#lastNameInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("Ross");
        clickOn("#dateOfBirthInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("12/12/1957");

        doubleClickOn("#createAccountButton");

        // Logout to be able to create another account
        clickOn("#logoutButton");
        clickOn("OK");
    }


    /**
     * Tests to check if the waiting list table is empty when no users exist
     */
    @Test
    public void checkForBlankTables() {
        //login as clinician
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");

        //navigate to table
        clickOn("#transplantList");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //check if no recivers in table
        verifyThat("No content in table", isVisible());

    }

    /**
     * Test to check if table correctly adds a new entry when a receiver is detected
     */
    @Test
    public void checkForFullTable() {
        createAccounts();

        //login as clinician
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");

        doubleClickOn("Bob Ross");
        //add organ to waiting list
        clickOn("#waitingListButton");
        clickOn("#organTypeComboBox");
        clickOn("heart");
        clickOn("#addOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //check the transplant list
        clickOn("#transplantList");

        //check the transplant table
        clickOn("Bob Ross");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        refreshTableSelections();

        assertEquals(Organ.HEART, transplantRow.getOrgan());
        assertEquals("Bob Ross", transplantRow.getName());
    }

    /**
     * Test to check if receiver is removed from transplant waiting list when an organ is deregistered
     */
    @Test
    public void checkDeregister() {
        createAccounts();

        //login as clinician
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");

        doubleClickOn("Bob Ross");
        //add organ to waiting list
        clickOn("#waitingListButton");
        clickOn("#organTypeComboBox");
        clickOn("heart");
        clickOn("#addOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //deregister an organ
        doubleClickOn("Bob Ross");
        clickOn("#waitingListButton");
        clickOn("heart");
        clickOn("#removeOrganButton");

        //close user window
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //show the transplant list
        clickOn("#transplantList");
        verifyThat("No content in table", isVisible());
    }
}
