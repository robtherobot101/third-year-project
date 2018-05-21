package seng302.Generic;

import static seng302.Generic.IO.streamOut;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.GUI.CommandLineInterface;
import seng302.GUI.Controllers.AccountSettingsController;
import seng302.GUI.Controllers.AdminController;
import seng302.GUI.Controllers.ClinicianAccountSettingsController;
import seng302.GUI.Controllers.ClinicianController;
import seng302.GUI.Controllers.CreateAccountController;
import seng302.GUI.Controllers.LoginController;
import seng302.GUI.Controllers.TransplantWaitingListController;
import seng302.GUI.Controllers.UserWindowController;
import seng302.GUI.TFScene;
import seng302.User.Admin;
import seng302.User.Attribute.Organ;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.User;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.sun.javafx.scene.control.skin.Utils.getResource;
import static seng302.Generic.IO.streamOut;

/**
 * WindowManager class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class WindowManager extends Application {

    public static final int mainWindowMinWidth = 800, mainWindowMinHeight = 600, mainWindowPrefWidth = 1250, mainWindowPrefHeight = 725;

    private static Stage stage;
    private static HashMap<TFScene, Scene> scenes = new HashMap<>();
    private static Map<Stage, UserWindowController> cliniciansUserWindows = new HashMap<>();
    private static Image icon;
    private static String dialogStyle, menuButtonStyle, selectedMenuButtonStyle;

    //Main windows
    private static LoginController loginController;
    private static CreateAccountController createAccountController;
    private static ClinicianController clinicianController;
    private static AdminController adminController;
    private static UserWindowController userWindowController;

    private static AccountSettingsController accountSettingsController;
    private static ClinicianAccountSettingsController clinicianAccountSettingsController;
    private static TransplantWaitingListController clinicianTransplantWaitingListController, adminTransplantWaitingListController;

    private static Database database;


    public static Database getDatabase() {
        return database;
    }

    public static void setDatabase(Database database) {
        WindowManager.database = database;
    }

    /**
     * Returns the program icon.
     *
     * @return The icon as an Image.
     */
    public static Image getIcon() {
        return icon;
    }

    /**
     * returns the current stage
     *
     * @return the current stage
     */
    public static Stage getStage() {
        return stage;
    }

    public static void newCliniciansUserWindow(User user){
        Stage stage = new Stage();
        stage.getIcons().add(WindowManager.getIcon());
        stage.setMinHeight(WindowManager.mainWindowMinHeight);
        stage.setMinWidth(WindowManager.mainWindowMinWidth);
        stage.setHeight(WindowManager.mainWindowPrefHeight);
        stage.setWidth(WindowManager.mainWindowPrefWidth);

        stage.initModality(Modality.NONE);

        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource("/fxml/userWindow.fxml"));
            Parent root = loader.load();
            UserWindowController userWindowController = loader.getController();
            userWindowController.setTitleBar(stage);
            String text = History.prepareFileStringGUI(user.getId(), "view");
            History.printToFile(streamOut, text);

            userWindowController.setCurrentUser(user);
            userWindowController.populateHistoryTable();
            userWindowController.setControlsShown(true);
            cliniciansUserWindows.put(stage, userWindowController);


            Scene newScene = new Scene(root, mainWindowPrefWidth, mainWindowPrefHeight);
            stage.setScene(newScene);
            stage.show();
        } catch (IOException | NullPointerException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * sets the current clinician
     *
     * @param clinician the logged clinician
     */
    public static void setClinician(Clinician clinician) {
        clinicianController.setClinician(clinician);
        clinicianController.updateDisplay();
        clinicianController.updateFoundUsers();
        updateTransplantWaitingList();
    }

    /**
     * Set whether a menu button is selected and highlighted or not.
     *
     * @param button The button to set
     * @param selected Whether it should be highlighted
     */
    public static void setButtonSelected(Button button, boolean selected) {
        if (selected) {
            button.getStylesheets().remove(menuButtonStyle);
            button.getStylesheets().add(selectedMenuButtonStyle);
        } else {
            button.getStylesheets().remove(selectedMenuButtonStyle);
            button.getStylesheets().add(menuButtonStyle);
        }
    }

    /**
     * sets the current admin
     *
     * @param admin the logged admin
     */
    public static void setAdmin(Admin admin) {
        adminController.setAdmin(admin);
    }

    /**
     * sets the current user
     *
     * @param currentUser the current user
     */
    public static void setCurrentUser(User currentUser) {
        userWindowController.setCurrentUser(currentUser);
        userWindowController.populateHistoryTable();
        userWindowController.setControlsShown(false);
    }

    /**
     * Calls the function which updates the waiting list pane.
     */
    public static void updateUserWaitingLists() {
        for (UserWindowController userWindowController: cliniciansUserWindows.values()) {
            userWindowController.populateWaitingList();
        }
    }

    /**
     * Calls the function which updates the transplant waiting list pane.
     */
    public static void updateTransplantWaitingList() {
        clinicianTransplantWaitingListController.updateTransplantList();
        adminTransplantWaitingListController.updateTransplantList();

    }

    /**
     * sets the current user for the account settings controller
     *
     * @param currentUser the current user
     */
    public static void setCurrentUserForAccountSettings(User currentUser) {
        accountSettingsController.setCurrentUser(currentUser);
        accountSettingsController.populateAccountDetails();
    }

    /**
     * sets the current clinican for account settings
     *
     * @param currentClinician the current clinician
     */
    public static void setCurrentClinicianForAccountSettings(Clinician currentClinician) {
        clinicianAccountSettingsController.setCurrentClinician(currentClinician);
        clinicianAccountSettingsController.populateAccountDetails();
    }

    /**
     * sets the login controller
     *
     * @param loginController the current login controller
     */
    public static void setLoginController(LoginController loginController) {
        WindowManager.loginController = loginController;
    }


    public static void setCreateAccountController(CreateAccountController createAccountController) {
        WindowManager.createAccountController = createAccountController;
    }

    public static void setAccountSettingsController(AccountSettingsController accountSettingsController) {
        WindowManager.accountSettingsController = accountSettingsController;
    }

    public static void setClincianAccountSettingsController(ClinicianAccountSettingsController clinicianAccountSettingsController) {
        WindowManager.clinicianAccountSettingsController = clinicianAccountSettingsController;
    }

    public static void setTransplantWaitingListController(TransplantWaitingListController transplantWaitingListController) {
        if (scenes.get(TFScene.clinician) == null) {
            WindowManager.clinicianTransplantWaitingListController = transplantWaitingListController;
        } else {
            WindowManager.adminTransplantWaitingListController = transplantWaitingListController;
        }

    }

    public static void setClinicianController(ClinicianController clinicianController) {
        WindowManager.clinicianController = clinicianController;
    }

    public static void setAdminController(AdminController adminController) {
        WindowManager.adminController = adminController;
    }

    public static ClinicianController getClinicianController() {
        return WindowManager.clinicianController;
    }

    public static void showDeregisterDialog(WaitingListItem waitingListItem) {
        clinicianTransplantWaitingListController.showDeregisterDialog(waitingListItem);
    }

    public static Map<Stage, UserWindowController> getCliniciansUserWindows() {
        return cliniciansUserWindows;
    }

    public static void closeAllChildren() {
        for (Stage stage: cliniciansUserWindows.keySet()) {
            stage.close();
        }
        cliniciansUserWindows.clear();
    }

    public static void setUserWindowController(UserWindowController userWindowController) {
        WindowManager.userWindowController = userWindowController;
    }

    public static void setAccountSettingsEnterEvent() {
        accountSettingsController.setEnterEvent();
    }

    public static void setClinicianAccountSettingsEnterEvent() {
        clinicianAccountSettingsController.setEnterEvent();
    }

    /**
     * Run the GUI.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            database = new Database();
            database.connectToDatabase();
            launch(args);
        } else if (args.length == 1 && args[0].equals("-c")) {
            try {
                IO.setPaths();
                CommandLineInterface commandLineInterface = new CommandLineInterface();
                //commandLineInterface.run(System.in);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please either run using:" +
                    "\nGUI mode: java -jar app-0.0.jar" +
                    "\nCommand line mode: java -jar app-0.0.jar -c.");
        }

    }

    /**
     * To be honest with you guys, I'm not 100% sure on how this works but it does
     * <p>
     * Solves an error on Linux systems with menu bar skin issues cropping up on the FX thread
     *
     * @param t Thread?
     * @param e Exception?
     */
    private static void showError(Thread t, Throwable e) {
        System.err.println("Non-critical error caught, probably platform dependent.");
        e.printStackTrace();
        if (Platform.isFxApplicationThread()) {
            e.printStackTrace();
        } else {
            System.err.println("An unexpected error occurred in " + t);
        }
    }

    /**
     * Load in saved users and clinicians, and initialise the GUI.
     *
     * @param stage The stage to set the GUI up on
     */
    @Override
    public void start(Stage stage) {
        // This fixes errors which occur in different threads in TestFX
        Thread.setDefaultUncaughtExceptionHandler(WindowManager::showError);

        WindowManager.stage = stage;
        stage.setTitle("Transplant Finder");
        stage.setOnHiding(closeAllWindows -> {
            for (Stage userWindow : cliniciansUserWindows.keySet()) {
                userWindow.close();
            }
        });
        dialogStyle = WindowManager.class.getResource("/css/dialog.css").toExternalForm();
        menuButtonStyle = WindowManager.class.getResource("/css/menubutton.css").toExternalForm();
        selectedMenuButtonStyle = WindowManager.class.getResource("/css/selectedmenubutton.css").toExternalForm();
        icon = new Image(getClass().getResourceAsStream("/icon.png"));
        stage.getIcons().add(icon);
        try {
            IO.setPaths();

            //TODO Get rid of loading of users from json
//            File users = new File(IO.getUserPath());
//            if (users.exists()) {
//                if (!IO.importUsers(users.getAbsolutePath(), LoginType.USER)) {
//                    throw new IOException("User save file could not be loaded.");
//                }
//            } else {
//                if (!users.createNewFile()) {
//                    throw new IOException("User save file could not be created.");
//                }
//            }

            //TODO Get rid of loading clinicians from json
//            File clinicians = new File(IO.getClinicianPath());
//            if (clinicians.exists()) {
//                if (!IO.importUsers(clinicians.getAbsolutePath(), LoginType.CLINICIAN)) {
//                    throw new IOException("Clinician save file could not be loaded.");
//                }
//            } else {
//                if (!clinicians.createNewFile()) {
//                    throw new IOException("Clinician save file could not be created.");
//                }
//                Clinician defaultClinician = new Clinician("default", "default", "default");
//                DataManager.clinicians.add(defaultClinician);
//                IO.saveUsers(IO.getClinicianPath(), LoginType.CLINICIAN);
//
//            }

            //TODO Get rid of loading admins from json
//            File admins = new File(IO.getAdminPath());
//            if (admins.exists()) {
//                if (!IO.importUsers(admins.getAbsolutePath(), LoginType.ADMIN)) {
//                    throw new IOException("Admin save file could not be loaded.");
//                }
//            } else {
//                if (!admins.createNewFile()) {
//                    throw new IOException("Admin save file could not be created.");
//                }
//                Admin defaultAdmin = new Admin("admin", "default", "default_admin");
//                DataManager.admins.add(defaultAdmin);
//                IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);
//
//            }


            IO.streamOut = History.init();
            for (TFScene scene : TFScene.values()) {
                scenes.put(scene, new Scene(FXMLLoader.load(getClass().getResource(scene.getPath())), scene.getWidth(), scene.getHeight()));
            }
            loginController.setEnterEvent();
            createAccountController.setEnterEvent();

            stage.setX(100);
            stage.setY(80);
            setScene(TFScene.login);
            stage.show();

        } catch (URISyntaxException e) {
            System.err.println("Unable to read jar path. Please run from a directory with a simpler path.");
            e.printStackTrace();
            stop();
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
            stop();
        }
    }

    public static void resetScene(TFScene scene) {
        try {
            scenes.remove(scene);
            scenes.put(scene, new Scene(FXMLLoader.load(WindowManager.class.getResource(scene.getPath())), mainWindowPrefWidth,
                    mainWindowPrefHeight));
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
        }
    }

    public static Scene getScene(TFScene scene) {
        return scenes.get(scene);
    }

    /**
     * Set the currently displayed scene on the main window. Sets the width, height, and resizability appropriately.
     *
     * @param scene The scene to switch to
     */
    public static void setScene(TFScene scene) {
        stage.setResizable(true);
        stage.setScene(scenes.get(scene));
        if (scene == TFScene.userWindow || scene == TFScene.clinician || scene == TFScene.admin) {
            stage.setMinWidth(mainWindowMinWidth);
            stage.setMinHeight(mainWindowMinHeight);
        } else {
            stage.setMinWidth(0);
            stage.setMinHeight(0);
        }
        stage.setWidth(scene.getWidth());
        stage.setHeight(scene.getHeight());
        stage.setScene(null);
        stage.setScene(scenes.get(scene));

        if (!(scene.getWidth() == mainWindowPrefWidth)) {
            stage.setResizable(false);
        }
    }

    /**
     * Create a styled alert dialog.
     *
     * @param alertType The type of alert to create
     * @param title     The title for the alert dialog
     * @param header    The header text for the alert dialog
     * @param content   The content text for the alert dialog
     * @return The created alert object
     */
    public static Alert createAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        setIconAndStyle(alert.getDialogPane());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    /**
     * Set the css stylesheet and icon for a dialog given a reference to its DialogPane.
     *
     * @param dialogPane The DialogPane to style and set icon for.
     */
    public static void setIconAndStyle(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(dialogStyle);
        dialogPane.getStyleClass().add("dialog");
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(icon);
    }

    /**
     * Writes quit history before exiting the GUI.
     */
    @Override
    public void stop() {
        try {
            if (userWindowController.getCurrentUser() != null) {
                String text = History.prepareFileStringGUI(userWindowController.getCurrentUser().getId(), "quit");
                History.printToFile(IO.streamOut, text);
            }
        } catch (Exception e) {
            System.out.println("Error writing history.");
        }

        System.out.println("Exiting GUI");
        Platform.exit();
    }
}
