package seng302.TestFX;


import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import org.junit.*;
import seng302.Generic.TransplantWaitingListItem;
import seng302.Generic.Main;
import seng302.User.Attribute.Organ;
import java.util.concurrent.TimeoutException;
import static org.testfx.api.FxAssert.verifyThat;
import static org.junit.Assert.assertEquals;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class TransplantWaitingListTest extends TestFXTest{

    private TableView<TransplantWaitingListItem> transplantTable;
    private TransplantWaitingListItem transplantRow;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setup() {
        Main.users.clear();
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

        clickOn("#userAttributesButton");
        clickOn("#regionField").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("Canterbury");
        clickOn("#saveButton");
        clickOn("OK");

        // Logout to be able to create another account
        clickOn("#logoutButton");
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
     * Test to check if region filtering removes recievers that don't have the region searched.
     */
    @Test
    public void checkRegionFilter() {
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
        clickOn("liver");
        clickOn("#registerOrganButton");
        clickOn("#saveUserButton");
        clickOn("OK");
        clickOn("#exitUserButton");
        clickOn("OK");

        //check the transplant list
        clickOn("#transplantList");

        clickOn("#regionSearchTextField").write("Canterb");
        //check the transplant table
        transplantTable = lookup("#transplantTable").queryTableView();
        transplantRow = transplantTable.getItems().get(0);
        assertEquals(Organ.LIVER, transplantRow.getOrgan());
        assertEquals("Bobby Dong Flame", transplantRow.getName());
        assertEquals(transplantTable.getItems().size(), 1);
    }

    /**
     * Test to see if organ filtering removes recievers that dont have the given organ on the waiting list
     */
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
        assertEquals(Organ.HEART, transplantRow.getOrgan());
        assertEquals("Bob Ross", transplantRow.getName());
        assertEquals(transplantTable.getItems().size(), 1);
    }
}


