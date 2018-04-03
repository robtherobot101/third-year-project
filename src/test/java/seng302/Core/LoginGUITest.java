package seng302.Core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.Controllers.LoginController;


import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

public class LoginGUITest extends ApplicationTest {

    Main mainGUI;

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

    private void enterAccountCreation() {
        // Assumed that calling method is currently on login screen
        clickOn("#createAccountButton");
    }

    /**
     * Simple proof of concept for testing TestFX
     */
    @Test
    public void createValidUser(){
        enterAccountCreation();
        clickOn("#usernameInput"); write("buzz");
        clickOn("#emailInput"); write("mkn29@uclive.ac.nz");
        clickOn("#passwordInput"); write("password123");
        clickOn("#passwordConfirmInput"); write("password123");
        clickOn("#firstNameInput"); write("Matthew");
        clickOn("#middleNamesInput"); write("Pieter");
        clickOn("#lastNameInput"); write("Knight");
        clickOn("#dateOfBirthInput"); write("12/06/1997");
        clickOn("#createAccountButton");
    }
}