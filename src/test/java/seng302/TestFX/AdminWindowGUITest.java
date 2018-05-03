package seng302.TestFX;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Core.Disease;
import seng302.Generic.Main;
import seng302.User.Admin;
import seng302.User.Attribute.Gender;
import seng302.User.Clinician;
import seng302.User.Medication.Medication;
import seng302.User.User;

public class AdminWindowGUITest extends TestFXTest {
    private User currentSelectedUser;
    private Clinician currentSelectedClinician;
    private Admin currentSelectedAdmin;

    @BeforeClass
    public static void setupClass() throws TimeoutException  {
        defaultTestSetup();
    }

    /**
     * Refreshes the currently selected profile from the three profile tabs
     */
    private void refreshTableSelections() {
        TabPane tableTabPane = lookup("#tableTabPane").query();

        switch(tableTabPane.getSelectionModel().getSelectedItem().getId()){
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


    /**
     * Add a simple user and verify it appears with appropriate details in the TabPane
     */
    @Test
    public void addGoodUser() {
        loginAsDefaultAdmin();
        clickOn("#fileMenu");
        moveTo("#createMenu");
        // To align the movement properly:
        moveTo("#adminMenuItem");
        clickOn("#userMenuItem");

        // Create a user
        clickOn("#usernameInput"); write("buzz");
        clickOn("#emailInput"); write("mkn29@uclive.ac.nz");
        clickOn("#passwordInput"); write("password123");
        clickOn("#passwordConfirmInput"); write("password123");
        clickOn("#dateOfBirthInput"); write("12/06/1997");
        clickOn("#firstNameInput"); write("Matthew");
        clickOn("#middleNamesInput"); write("Pieter");
        clickOn("#lastNameInput"); write("Knight");

        clickOn("#createAccountButton");
        sleep(500);
        //Make sure that the create account button is no longer shown (because the account is now created and the scene should have changed)
        assertNull(lookup("#createAccountButton").query());

        clickOn("Matthew Pieter Knight");
        refreshTableSelections();
        // Check this is the user:
        assertEquals(LocalDate.of(1997, 6, 12), currentSelectedUser.getDateOfBirth());
    }

    /**
     * Add a simple user and verify it appears with appropriate details in the TabPane
     */
    @Test
    public void addAdmin() {
        loginAsDefaultAdmin();
        clickOn("#fileMenu");
        moveTo("#createMenu");
        clickOn("#adminMenuItem");

        // Create a user
        clickOn("#usernameInput"); write("buzz");
        clickOn("#passwordInput"); write("password123");
        clickOn("#passwordConfirmInput"); write("password123");
        clickOn("#firstNameInput"); write("Matthew");
        clickOn("#middleNamesInput"); write("Pieter");
        clickOn("#lastNameInput"); write("Knight");

        clickOn("#createAccountButton");
        sleep(500);
        //Make sure that the create account button is no longer shown (because the account is now created and the scene should have changed)
        assertNull(lookup("#createAccountButton").query());

        clickOn("Matthew Pieter Knight");
        refreshTableSelections();
        // Check this is the user:
        assertEquals("buzz", currentSelectedAdmin.getUsername());
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
        clickOn("default_admin");
        refreshTableSelections();
        assertEquals("default_admin", currentSelectedAdmin.getName());
    }
}