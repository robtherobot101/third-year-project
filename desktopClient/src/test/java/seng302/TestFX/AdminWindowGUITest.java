package seng302.TestFX;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.User;

public class AdminWindowGUITest extends TestFXTest {

    private User currentSelectedUser;
    private Clinician currentSelectedClinician;
    private Admin currentSelectedAdmin;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setupTest() throws SQLException {
    }


    /**
     * Refreshes the currently selected profile from the three profile tabs
     */
    private void refreshTableSelections() {
        TabPane tableTabPane = lookup("#tableTabPane").query();

        switch (tableTabPane.getSelectionModel().getSelectedItem().getId()) {
            case "usersTab":
                TableView<User> userTableView = lookup("#userTableView").query();
                currentSelectedUser = userTableView.getSelectionModel().getSelectedItem();
                currentSelectedClinician = null;
                currentSelectedAdmin = null;
                break;
            case "administratorsTab":
                TableView<Admin> adminTableView = lookup("#adminTableView").query();
                currentSelectedAdmin = adminTableView.getSelectionModel().getSelectedItem();
                currentSelectedClinician = null;
                currentSelectedUser = null;
                break;
            case "cliniciansTab":
                TableView<Clinician> clinicianTableView = lookup("#clinicianTableView").query();
                currentSelectedClinician = clinicianTableView.getSelectionModel().getSelectedItem();
                currentSelectedUser = null;
                currentSelectedAdmin = null;
                break;
        }
    }

    @Test
    public void checkExistenceDefaultClinician() {
        loginAsDefaultAdmin();
        clickOn("#cliniciansTab");
        clickOn("default");
        refreshTableSelections();
        assertEquals("default", currentSelectedClinician.getName());
    }

    @Test
    public void checkExistenceDefaultAdmin() {
        loginAsDefaultAdmin();
        clickOn("#administratorsTab");
        clickOn("default");
        refreshTableSelections();
        assertEquals("default", currentSelectedAdmin.getName());
    }
}