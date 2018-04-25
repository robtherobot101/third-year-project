package seng302.TestFX;

import java.util.concurrent.TimeoutException;
import org.junit.BeforeClass;
import org.junit.Test;

public class CreateAndLoginGUITest extends TestFXTest {
    @BeforeClass
    public static void setupClass() throws TimeoutException  {
        defaultTestSetup();
    }

    /**
     * Simple proof of concept test of inputting a valid user for testing TestFX
     */
    @Test
    public void createValidUser(){
        clickOn("#createAccountButton");

        clickOn("#usernameInput"); write("buzz");
        clickOn("#emailInput"); write("mkn29@uclive.ac.nz");
        clickOn("#passwordInput"); write("password123");
        clickOn("#passwordConfirmInput"); write("password123");
        clickOn("#firstNameInput"); write("Matthew");
        clickOn("#middleNamesInput"); write("Pieter");
        clickOn("#lastNameInput"); write("Knight");
        clickOn("#dateOfBirthInput"); write("12/06/1997");
        clickOn("#createAccountButton");
        //TODO add a check to verify the creation is complete
    }

    @Test
    public void testLoginAsDefaultClinician() {
        loginAsDefaultClinician();
        //TODO add a check to verify the login is complete
    }
}