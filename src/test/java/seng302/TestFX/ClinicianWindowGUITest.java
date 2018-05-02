package seng302.TestFX;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Generic.Main;
import seng302.User.Attribute.Organ;
import seng302.User.Clinician;
import seng302.User.User;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ClinicianWindowGUITest extends  TestFXTest {
    int resultsPerPage;
    int numberXOfResults;
    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }


    @Before
    public void setUp () throws Exception {
        loginAsDefaultClinician();
        numberXOfResults = Main.getClinicianController().getNumberXofResults();
        resultsPerPage = Main.getClinicianController().getResultsPerPage();
    }

    public void changeNumberOfResultsToDisplay(String regex){
        ComboBox options = lookup("#numberOfResutsToDisplay").queryComboBox();
        clickOn(options);
        for(int i = 0; i < options.getItems().size(); i++) type(KeyCode.UP);

        for(int i = 0; i < options.getItems().size(); i++){
            if(options.getSelectionModel().getSelectedItem().toString().matches(regex)){
                type(KeyCode.ENTER);
                sleep(2000);
                break;
            }
            type(KeyCode.DOWN);
        }
    }

    @Test
    public void lessThanAPageOfResults_displayNComboboxDisabled() throws TimeoutException{
        Main.users.clear();
        for(int i = 0; i < resultsPerPage-1; i++)
            Main.users.add(new User("A" + i, LocalDate.now()));
        clickOn("#profileSearchTextField").write("A");
        waitForNodeEnabled(10,"#profileTable");
        Node displayNCombobox = lookup("#numberOfResutsToDisplay").queryComboBox();
        assertTrue(displayNCombobox.isDisable());
    }

    @Test
    public void moreThanOnePageAndLessThanXResults_displayNComboboxHasAllYResultsOption() throws TimeoutException{
        Main.users.clear();
        for(int i = 0; i < resultsPerPage+1; i++)
            Main.users.add(new User("A" + i, LocalDate.now()));
        clickOn("#profileSearchTextField").write("A");
        waitForNodeEnabled(10,"#profileTable");
        ComboBox displayNCombobox = lookup("#numberOfResutsToDisplay").queryComboBox();
        assertEquals(2,displayNCombobox.getItems().size());
        assertTrue(((String)displayNCombobox.getItems().get(1)).matches("All [0-9]* results"));
    }

    @Test
    public void moreThanXResults_displayNComboboxHasAllYResultsOption() throws TimeoutException{
        Main.users.clear();
        int i;
        for(i = 0; i < numberXOfResults+1; i++)
            Main.users.add(new User("A" + i, LocalDate.now()));
        clickOn("#profileSearchTextField").write("A");
        waitForNodeEnabled(10,"#profileTable");
        ComboBox displayNCombobox = lookup("#numberOfResutsToDisplay").queryComboBox();
        assertEquals(3,displayNCombobox.getItems().size());
        assertTrue(((String)displayNCombobox.getItems().get(1)).matches("Top [0-9]* results"));
        assertTrue(((String)displayNCombobox.getItems().get(2)).matches("All " + i + " results"));
    }

    @Test
    public void clickOnProfile_opensProfile() throws TimeoutException{
        Main.users.clear();
        User u1 = new User("Victor", LocalDate.now());
        Main.users.add(u1);
        clickOn("#profileSearchTextField").write("victor");
        waitForNodeEnabled(10,"#profileTable");
        TableView profileTable = lookup("#profileTable").queryTableView();
        doubleClickOn((Node)from(profileTable).lookup(u1.getNameExt()).query());
        waitForEnabled(10,"#attributesGridPane");
    }

    @Test
    public void searchForProfile_sortedResultsInTable() throws TimeoutException{
        User u1 = new User("Victor,Abby,West", LocalDate.now());
        User u2 = new User("Abby,Matthers,Black", LocalDate.now());
        User u3 = new User("Matthew,Warner,Hope", LocalDate.now());
        User u4 = new User("Billy,Bobby,Harry", LocalDate.now());
        User u5 = new User("Downton,Abby", LocalDate.now());
        Main.users.add(u1);
        Main.users.add(u2);
        Main.users.add(u3);
        Main.users.add(u4);
        Main.users.add(u5);
        clickOn("#profileSearchTextField").write("Abby");
        waitForNodeEnabled(10,"#profileTable");
        TableView profileTable = lookup("#profileTable").queryTableView();
        assertEquals(3,profileTable.getItems().size());
        assertEquals(u5,profileTable.getItems().get(0));
        assertEquals(u2,profileTable.getItems().get(1));
        assertEquals(u1,profileTable.getItems().get(2));
    }

    @Test
    public void changeNumberOfResultsDisplayed_numberOfResultsInTableIsCorrect() throws TimeoutException{
        Main.users.clear();
        int i;
        for(i = 0; i < numberXOfResults*2; i++)
            Main.users.add(new User("A" + i, LocalDate.now()));
        clickOn("#profileSearchTextField").write("A");
        waitForNodeEnabled(10,"#profileTable");
        TableView profileTable = lookup("#profileTable").queryTableView();
        changeNumberOfResultsToDisplay("Top [0-9]* results");
        assertEquals(numberXOfResults,profileTable.getItems().size());
        changeNumberOfResultsToDisplay("All [0-9]* results");
        assertEquals(numberXOfResults*2,profileTable.getItems().size());
        changeNumberOfResultsToDisplay("First page");
        assertEquals(resultsPerPage,profileTable.getItems().size());
    }

    @Test
    public void changeClinicianSettings_updatesClinician(){
        clickOn("#updateClinician");
        clickOn("#clinicianName").write("new name");
        clickOn("#clinicianAddress").write("new address");
        clickOn("#clinicianRegion").write("new region");

        Clinician clinician = Main.getClinicianController().getClinician();
        clickOn("#confirmUpdateButton");
        assertEquals("new name", clinician.getName());
        assertEquals("new address", clinician.getWorkAddress());
        assertEquals("new region", clinician.getRegion());
    }

    @Test
    public void changeClinicianSettings_updatesClinicianDisplay(){
        clickOn("#updateClinician");
        clickOn("#clinicianName").write("a");
        clickOn("#clinicianAddress").write("b");
        clickOn("#clinicianRegion").write("c");

        Clinician clinician = Main.getClinicianController().getClinician();
        clickOn("#confirmUpdateButton");
        assertEquals("Name: a", lookup("#nameLabel").queryLabeled().getText());
        assertEquals("Address: b", lookup("#addressLabel").queryLabeled().getText());
        assertEquals("Region: c", lookup("#regionLabel").queryLabeled().getText());
    }

    @Test
    public void noChangesInClinicianDialog_updateButtonDisabled(){
        clickOn("#updateClinician");
        Button confirmUpdateButton = lookup("#confirmUpdateButton").queryButton();
        assertTrue(confirmUpdateButton.isDisable());
    }

    @Test
    public void reversedChangesInClinicianDialog_updateButtonDisabled(){
        clickOn("#updateClinician");
        clickOn("#clinicianName").write("a");
        type(KeyCode.BACK_SPACE);
        clickOn("#clinicianAddress").write("b");
        type(KeyCode.BACK_SPACE);
        clickOn("#clinicianRegion").write("c");
        type(KeyCode.BACK_SPACE);

        Button confirmUpdateButton = lookup("#confirmUpdateButton").queryButton();
        assertTrue(confirmUpdateButton.isDisable());
    }

    @Test
    public void changeAccountSettings_updatesClinician() throws TimeoutException {
        clickOn("#updateAccountSettingsClinicianButton");
        write("default");
        clickOn("#updateAccountSettingsOKButton");

        Clinician clinician = Main.getClinicianController().getClinician();

        clickOn("#usernameField");
        push(KeyCode.CONTROL, KeyCode.A, KeyCode.DELETE);
        write("new username");

        clickOn("#passwordField");
        push(KeyCode.CONTROL, KeyCode.A, KeyCode.DELETE);
        write("new password");

        clickOn("#accountSettingsUpdateButton");

        waitForEnabled(10, "#updateAccountSettingsConfirmationOKButton");
        clickOn("#updateAccountSettingsConfirmationOKButton");

        assertEquals("new username", clinician.getUsername());
        assertEquals("new password", clinician.getPassword());
    }
}
