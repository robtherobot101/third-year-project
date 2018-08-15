package seng302.TestFX;


import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import org.apache.http.client.HttpResponseException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.gui.TFScene;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.User.Attribute.Organ;
import seng302.User.User;
import seng302.User.WaitingListItem;

public class TransplantWaitingListTest extends TestFXTest {

    private TableView<WaitingListItem> transplantTable;
    private WaitingListItem transplantRow;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    /**
     * Refreshes the currently selected receiver in both tables of Medical History.
     */
    private void refreshTableSelections() {
        transplantTable = lookup("#transplantTable").queryTableView();
        transplantRow = transplantTable.getItems().get(0);
    }


    /**
     * helper function to create two new users. One a receiver and one a dummy.
     */
    private void createAccounts() {
        //DataManager.users.clear();
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

        clickOn("#userAttributesButton");
        clickOn("#regionField").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("Canterbury");
        clickOn("#saveButton");
        clickOn("OK");

        // Logout to be able to create another account
        clickOn("#logoutButton");
        sleep(100);
        clickOn("OK");

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    @Ignore
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
    @Ignore
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
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //check the transplant list
        clickOn("#transplantList");

        //check the transplant table
        transplantTable = lookup("#transplantTable").queryTableView();
        transplantRow = transplantTable.getItems().get(0);

        assertEquals(Organ.HEART, transplantRow.getOrganType());
        assertEquals("Bob Ross", transplantRow.getReceiverName());
    }

    /**
     * Test to check if receiver is removed from transplant waiting list when an organ is deregistered
     */
    @Ignore
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
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //deregister an organ
        doubleClickOn("Bob Ross");
        clickOn("#waitingListButton");
        clickOn("heart");
        clickOn("#deregisterOrganButton");
        clickOn("OK");

        //close user window
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //show the transplant list
        clickOn("#transplantList");
        sleep(1000);
        verifyThat("#transplantPane", Node::isVisible);
        //verifyThat("#transplantPane", hasText("No content in table"));
        //sleep(1000);
        verifyThat("No content in table", isVisible());
    }

    /**
     * Test to verify that when deregistering
     */
    @Ignore
    @Test
    public void checkDeregisterDeath() {
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
        clickOn("#registerOrganButton");
        clickOn("#organTypeComboBox");
        clickOn("liver");
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //deregister an organ
        clickOn("#transplantList");
        clickOn("heart");
        clickOn("#deregisterReceiverButton");
        clickOn("4: Successful Transplant");
        clickOn("3: Receiver Deceased");
        clickOn("OK");
        moveBy(-100, -50);
        clickOn();
        clickOn().write("10/10/2017");
        clickOn("OK");

        verifyThat("#transplantPane", Node::isVisible);
        //verifyThat("#transplantPane", hasText("No content in table"));
        verifyThat("No content in table", isVisible());
    }

    @Ignore
    @Test
    public void checkDeregisterError() {
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
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //deregister an organ
        clickOn("#transplantList");
        clickOn("heart");
        clickOn("#deregisterReceiverButton");
        clickOn("4: Successful Transplant");
        clickOn("1: Error Registering");
        clickOn("OK");

        verifyThat("#transplantPane", Node::isVisible);
        //verifyThat("#transplantPane", hasText("No content in table"));
        verifyThat("No content in table", isVisible());
    }

    /**
     * Checks to see that a future date cannot be entered.
     */
    @Ignore
    @Test
    public void checkFutureDate() {
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
        clickOn("#registerOrganButton");
        clickOn("#organTypeComboBox");
        clickOn("liver");
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //deregister an organ
        clickOn("#transplantList");
        clickOn("heart");
        clickOn("#deregisterReceiverButton");
        clickOn("4: Successful Transplant");
        clickOn("3: Receiver Deceased");
        clickOn("OK");
        moveBy(-100, -50);
        clickOn();
        clickOn().write("10/10/2097");
        clickOn("OK");
        clickOn();
        verifyThat("Please enter a date that is either today or earlier", Node::isVisible);
        //verifyThat("#transplantPane", hasText("No content in table"));
        //verifyThat("No content in table", isVisible());
    }

    @Ignore
    @Test
    public void checkDeregisterCure() {
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
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#diseasesButton");
        clickOn("#newDiseaseTextField").write("Whooping Cough");
        clickOn("#currentDateButton");
        clickOn("#addNewDiseaseButton");
        clickOn("#saveDiseaseButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //deregister an organ
        clickOn("#transplantList");
        clickOn("heart");
        clickOn("#deregisterReceiverButton");
        clickOn("4: Successful Transplant");
        clickOn("2: Disease Cured");
        clickOn("OK");
        clickOn("Yes");
        moveBy(-100, -50);
        clickOn();
        clickOn("Whooping Cough");
        clickOn("Cure");
        clickOn("OK");

        verifyThat("#transplantPane", Node::isVisible);
        //verifyThat("#transplantPane", hasText("No content in table"));
        verifyThat("No content in table", isVisible());
    }

    /**
     * Test to check if region filtering removes recievers that don't have the region searched.
     */
    @Ignore
    @Test
    public void checkRegionFilter() {
        User testUser = new User(
                "Bobby", new String[]{"Dong"}, "Flame",
                LocalDate.of(1969, 8, 4),
                "bflame",
                "flameman@hotmail.com",
                "password123");
        testUser.setRegion("Canterbury");
        try{
            WindowManager.getDataManager().getUsers().insertUser(testUser);
        }catch (HttpResponseException e) {
            Debugger.error("Should avoid using using DB for testing when possilbe. Failed to insert new user.");
        }
        testUser = new User(
                "Bob", new String[]{}, "Ross",
                LocalDate.of(1957, 12, 12),
                "bobr",
                "bob@live.com",
                "password");
        try{
            WindowManager.getDataManager().getUsers().insertUser(testUser);
        } catch (HttpResponseException e) {
            Debugger.error("Should avoid using DB for testing where possible. Failed to insert new user.");
        }

        WindowManager.resetScene(TFScene.userWindow);
        //createAccounts();

        //login as clinician
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");

        doubleClickOn("Bob Ross");
        //add organ to waiting list
        clickOn("#waitingListButton");
        clickOn("#organTypeComboBox");
        clickOn("heart");
        clickOn("#registerOrganButton");
        clickOn("#saveButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        doubleClickOn("Bobby Dong Flame");
        //add organ to waiting list
        clickOn("#waitingListButton");
        clickOn("#organTypeComboBox");
        clickOn("liver");
        clickOn("#registerOrganButton");
        clickOn("#saveButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //check the transplant list
        clickOn("#transplantListButton");

        clickOn("#regionSearchTextField").write("Canterb");
        //check the transplant table
        transplantTable = lookup("#transplantTable").queryTableView();
        transplantRow = transplantTable.getItems().get(0);
        assertEquals(Organ.LIVER, transplantRow.getOrganType());
        assertEquals("Bobby Dong Flame", transplantRow.getReceiverName());
        assertEquals(transplantTable.getItems().size(), 1);
    }

    /**
     * Test to see if organ filtering removes recievers that dont have the given organ on the waiting list
     */
    @Ignore
    @Test
    public void checkOrganFilter() {
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
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        doubleClickOn("Bobby Dong Flame");
        //add organ to waiting list
        clickOn("#waitingListButton");
        clickOn("#organTypeComboBox");
        sleep(300);
        clickOn("liver");
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //check the transplant list
        clickOn("#transplantList");

        clickOn("#organSearchComboBox");
        clickOn("heart");
        sleep(3000);
        //check the transplant table
        transplantTable = lookup("#transplantTable").queryTableView();
        transplantRow = transplantTable.getItems().get(0);
        assertEquals(Organ.HEART, transplantRow.getOrganType());
        assertEquals("Bob Ross", transplantRow.getReceiverName());
        assertEquals(transplantTable.getItems().size(), 1);
    }
}


