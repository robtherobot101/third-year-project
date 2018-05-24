package seng302.TestFX;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.*;
import org.testfx.api.FxRobot;
import seng302.Generic.DataManager;
import seng302.Generic.WindowManager;
import seng302.User.Clinician;
import seng302.User.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


public class ClinicianWindowGUITest extends  TestFXTest {
    int resultsPerPage;
    int numberXOfResults;
    Clinician testClinician;
    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }


    @Before
    public void setUp () throws Exception {
        testClinician = new Clinician("testUsername","testPassword","testName");
        openClinicianWindow(testClinician);
        numberXOfResults = WindowManager.getClinicianController().getNumberXofResults();
        resultsPerPage = WindowManager.getClinicianController().getResultsPerPage();
    }

    @After
    public void tearDown () {
        Platform.runLater(() ->{
            for(Window w:this.listWindows()){
                w.hide();
            }
        });

    }


    public void changeNumberOfResultsToDisplay(String regex){
        ComboBox options = lookup("#numberOfResutsToDisplay").queryComboBox();
        clickOn(options);
        for(int i = 0; i < options.getItems().size(); i++) type(KeyCode.UP);

        for(int i = 0; i < options.getItems().size(); i++){
            if(options.getSelectionModel().getSelectedItem().toString().matches(regex)){
                type(KeyCode.ENTER);
                sleep(500);
                break;
            }
            type(KeyCode.DOWN);
        }
    }

    @Test
    public void lessThanAPageOfResults_displayNComboBoxDisabled() throws TimeoutException{
        DataManager.users.clear();
        for(int i = 0; i < resultsPerPage-1; i++)
            DataManager.users.add(new User("A" + i, LocalDate.now()));
        clickOn("#profileSearchTextField").write("A");
        waitForNodeEnabled(10,"#profileTable");
        Node displayNCombobox = lookup("#numberOfResutsToDisplay").queryComboBox();
        assertTrue(displayNCombobox.isDisable());
    }

    @Test
    public void moreThanOnePageAndLessThanXResults_displayNComboBoxHasAllYResultsOption() throws TimeoutException{
        DataManager.users.clear();
        for(int i = 0; i < resultsPerPage+1; i++)
            DataManager.users.add(new User("A" + i, LocalDate.now()));
        clickOn("#profileSearchTextField").write("A");
        waitForNodeEnabled(10,"#profileTable");
        ComboBox displayNCombobox = lookup("#numberOfResutsToDisplay").queryComboBox();
        assertEquals(2,displayNCombobox.getItems().size());
        assertTrue(((String)displayNCombobox.getItems().get(1)).matches("All [0-9]* results"));
    }

    @Test
    public void moreThanXResults_displayNComboBoxHasAllYResultsOption() throws TimeoutException{
        DataManager.users.clear();
        int i;
        for(i = 0; i < numberXOfResults+1; i++)
            DataManager.users.add(new User("A" + i, LocalDate.now()));
        clickOn("#profileSearchTextField").write("A");
        waitForNodeEnabled(10,"#profileTable");
        ComboBox displayNCombobox = lookup("#numberOfResutsToDisplay").queryComboBox();
        assertEquals(3,displayNCombobox.getItems().size());
        assertTrue(((String)displayNCombobox.getItems().get(1)).matches("Top [0-9]* results"));
        assertTrue(((String)displayNCombobox.getItems().get(2)).matches("All " + i + " results"));
    }

    @Ignore
    @Test
    public void clickOnProfile_opensProfile() throws TimeoutException{
        DataManager.users.clear();
        User u1 = new User("Victor", LocalDate.now());
        DataManager.users.add(u1);
        clickOn("#profileSearchTextField").write("victor");
        waitForNodeEnabled(10,"#profileTable");
        TableView profileTable = lookup("#profileTable").queryTableView();
        doubleClickOn((Node)from(profileTable).lookup(u1.getName()).query());
        waitForEnabled(10,"#attributesGridPane");
    }

    @Test
    public void searchForProfile_sortedResultsInTable() throws TimeoutException{
        User u1 = new User("Victor,Abby,West", LocalDate.now());
        User u2 = new User("Abby,Matthers,Black", LocalDate.now());
        User u3 = new User("Matthew,Warner,Hope", LocalDate.now());
        User u4 = new User("Billy,Bobby,Harry", LocalDate.now());
        User u5 = new User("Downton,Abby", LocalDate.now());
        DataManager.users.add(u1);
        DataManager.users.add(u2);
        DataManager.users.add(u3);
        DataManager.users.add(u4);
        DataManager.users.add(u5);
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
        DataManager.users.clear();
        int i;
        for(i = 0; i < numberXOfResults*2; i++)
            DataManager.users.add(new User("A" + i, LocalDate.now()));
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
        clickOn("#updateClinicianButton");
        clickOn("#clinicianName").write("default");
        clickOn("#clinicianAddress").write("default");
        clickOn("#clinicianRegion").write("default");

        Clinician clinician = WindowManager.getClinicianController().getClinician();
        clickOn("Update");

        pressDialogOKButtons();
        assertEquals("default", clinician.getName());
        assertEquals("default", clinician.getWorkAddress());
        assertEquals("default", clinician.getRegion());
    }

    @Test
    public void changeClinicianSettings_updatesClinicianDisplay() throws TimeoutException{
        clickOn("#updateClinicianButton");
        clickOn("#clinicianName").write("newTestName");
        clickOn("#clinicianAddress").write("newTestAddress");
        clickOn("#clinicianRegion").write("newTestRegion");

        Clinician clinician = WindowManager.getClinicianController().getClinician();
        clickOn("#clinicianSettingsPopupUpdateButton");
        pressDialogOKButtons();
        assertEquals("Name: newTestName", lookup("#nameLabel").queryLabeled().getText());
        assertEquals("Address: newTestAddress", lookup("#addressLabel").queryLabeled().getText());
        assertEquals("Region: newTestRegion", lookup("#regionLabel").queryLabeled().getText());
    }

    @Test
    public void noChangesInClinicianDialog_updateButtonDisabled(){
        clickOn("#updateClinicianButton");
        Button confirmUpdateButton = lookup("Update").queryButton();
        assertTrue(confirmUpdateButton.isDisable());
    }

    @Test
    public void reversedChangesInClinicianDialog_updateButtonDisabled(){
        clickOn("#updateClinicianButton");
        clickOn("#clinicianName").write("a");
        type(KeyCode.BACK_SPACE);
        clickOn("#clinicianAddress").write("b");
        type(KeyCode.BACK_SPACE);
        clickOn("#clinicianRegion").write("c");
        type(KeyCode.BACK_SPACE);


        Button confirmUpdateButton = lookup("Update").queryButton();
        assertTrue(confirmUpdateButton.isDisable());
    }

    public void pressDialogOKButtons(){
        sleep(500);
        for(Window window: this.listWindows()){
            for(Node nodeWithOKText : from(window.getScene().getRoot()).lookup("OK").queryAll()){
                if(nodeWithOKText instanceof Button){
                    clickOn(nodeWithOKText);
                }
            }
        }
    }

    @Test
    public void changeAccountSettings_updatesClinician() throws TimeoutException {
        Platform.runLater(() ->{
            try{
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/accountSettingsClinician.fxml"));
                Stage stage = new Stage();
                stage.getIcons().add(WindowManager.getIcon());
                stage.setResizable(false);
                stage.setTitle("Account Settings");
                stage.setScene(new Scene(root, 290, 280));
                stage.initModality(Modality.APPLICATION_MODAL);

                WindowManager.setCurrentClinicianForAccountSettings(testClinician);
                WindowManager.setClinicianAccountSettingsEnterEvent();

                stage.showAndWait();

                }catch (IOException e){
                System.out.println(e);
            }
        });

        waitForNodeVisible(10,"#usernameField");
        clickOn("#usernameField");
        push(KeyCode.CONTROL, KeyCode.A, KeyCode.DELETE);
        write("newTestUsername");

        clickOn("#passwordField");
        push(KeyCode.CONTROL, KeyCode.A, KeyCode.DELETE);
        write("newTestPassword");

        clickOn("Update");
        System.out.println(Arrays.toString(this.listWindows().toArray()));
        pressDialogOKButtons();
        assertEquals("newTestUsername", testClinician.getUsername());
        assertEquals("newTestPassword", testClinician.getPassword());
    }
}
