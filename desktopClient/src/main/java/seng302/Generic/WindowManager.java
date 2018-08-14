package seng302.Generic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Database.AdminsDB;
import seng302.Data.Database.CliniciansDB;
import seng302.Data.Database.GeneralDB;
import seng302.Data.Database.UsersDB;
import seng302.Data.Interfaces.AdminsDAO;
import seng302.Data.Interfaces.CliniciansDAO;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Data.Local.AdminsM;
import seng302.Data.Local.CliniciansM;
import seng302.Data.Local.GeneralM;
import seng302.Data.Local.UsersM;
import seng302.GUI.Controllers.Admin.AdminController;
import seng302.GUI.Controllers.Clinician.ClinicianAvailableOrgansController;
import seng302.GUI.Controllers.Clinician.ClinicianController;
import seng302.GUI.Controllers.Clinician.ClinicianSettingsController;
import seng302.GUI.Controllers.Clinician.ClinicianWaitingListController;
import seng302.GUI.Controllers.LoginController;
import seng302.GUI.Controllers.User.CreateUserController;
import seng302.GUI.Controllers.User.UserController;
import seng302.GUI.TFScene;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.Medication.InteractionApi;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static seng302.Generic.IO.getJarPath;

/**
 * WindowManager class that contains program initialization code and Data that must be accessible from multiple parts of the
 * program.
 */
public class WindowManager extends Application {

    public static final int MAIN_WINDOW_MIN_WIDTH = 800;
    public static final int MAIN_WINDOW_MIN_HEIGHT = 600;
    public static final int MAIN_WINDOW_PREF_WIDTH = 1250;
    public static final int MAIN_WINDOW_PREF_HEIGHT = 725;

    private static Stage stage;
    private static HashMap<TFScene, Scene> scenes = new HashMap<>();
    private static Map<Stage, UserController> cliniciansUserWindows = new HashMap<>();
    private static Image icon;
    private static String dialogStyle;
    private static String menuButtonStyle;
    private static String selectedMenuButtonStyle;

    //Main windows
    private static LoginController loginController;
    private static CreateUserController createUserController;
    private static ClinicianController clinicianController;
    private static AdminController adminController;
    private static UserController userController;

    private static ClinicianSettingsController clinicianSettingsController;
    private static ClinicianWaitingListController clinicianClinicianWaitingListController;
    private static ClinicianWaitingListController adminClinicianWaitingListController;
    private static ClinicianAvailableOrgansController clinicianClinicianAvailableOrgansController;
    private static ClinicianAvailableOrgansController adminClinicianAvailableController;

    private static DataManager dataManager;

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static void setDataManager(DataManager dataManager) {
        WindowManager.dataManager = dataManager;
    }

    private static boolean testing = true;

    private static String unableTo = "Unable to load fxml or save file.";

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

    /**
     * Creates a new User window from a Clinician's view.
     * @param user The User to create the window for
     * @param token the users token
     */
    public static void newAdminsUserWindow(User user, String token){
        Stage stage = new Stage();
        stage.getIcons().add(WindowManager.getIcon());
        stage.setMinHeight(WindowManager.MAIN_WINDOW_MIN_HEIGHT);
        stage.setMinWidth(WindowManager.MAIN_WINDOW_MIN_WIDTH);
        stage.setHeight(WindowManager.MAIN_WINDOW_PREF_HEIGHT);
        stage.setWidth(WindowManager.MAIN_WINDOW_PREF_WIDTH);

        stage.initModality(Modality.NONE);

        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource("/fxml/user/user.fxml"));
            Parent root = loader.load();
            UserController newUserController = loader.getController();
            newUserController.setTitleBar(stage);

            newUserController.setCurrentUser(user, token);
            newUserController.addHistoryEntry("Clinician opened", "A Clinician opened this profile to view and/or edit information.");

            newUserController.setControlsShown(true);
            cliniciansUserWindows.put(stage, newUserController);


            Scene newScene = new Scene(root, MAIN_WINDOW_PREF_WIDTH, MAIN_WINDOW_PREF_HEIGHT);
            stage.setScene(newScene);
            stage.show();
        } catch (IOException | NullPointerException e) {
            Debugger.error(unableTo);
            Debugger.error(e.getLocalizedMessage());
            Platform.exit();
        }
    }

    /**
     * Creates a new User window from a Clinician's view.
     * @param user The User to create the window for
     * @param token the users token
     */
    public static void newCliniciansUserWindow(User user, String token){
        Stage stage = new Stage();
        stage.getIcons().add(WindowManager.getIcon());
        stage.setMinHeight(WindowManager.MAIN_WINDOW_MIN_HEIGHT);
        stage.setMinWidth(WindowManager.MAIN_WINDOW_MIN_WIDTH);
        stage.setHeight(WindowManager.MAIN_WINDOW_PREF_HEIGHT);
        stage.setWidth(WindowManager.MAIN_WINDOW_PREF_WIDTH);

        stage.initModality(Modality.NONE);
        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource("/fxml/user/user.fxml"));
            Parent root = loader.load();
            UserController newUserController = loader.getController();
            newUserController.setTitleBar(stage);

            newUserController.setCurrentUser(user, token);
            newUserController.addHistoryEntry("Clinician opened", "A Clinician opened this profile to view and/or edit information.");

            newUserController.setControlsShown(true);
            newUserController.getAttributesController().setDeathControlsShown(true);
            cliniciansUserWindows.put(stage, newUserController);


            Scene newScene = new Scene(root, MAIN_WINDOW_PREF_WIDTH, MAIN_WINDOW_PREF_HEIGHT);
            stage.setScene(newScene);
            stage.show();
        } catch (IOException | NullPointerException e) {
            Debugger.error(unableTo);
            Debugger.error(e.getLocalizedMessage());
            Platform.exit();
        }
    }


    /**
     * sets the current Clinician
     *
     * @param clinician the logged Clinician
     * @param token the users token
     */
    public static void setCurrentClinician(Clinician clinician, String token) {
        clinicianController.setClinician(clinician, token);
        clinicianController.updateDisplay();
        clinicianController.updateFoundUsers();
    }

    public static Clinician getCurrentClinician() {
        return clinicianController.getClinician();
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
     * sets the current Admin
     *
     * @param admin the logged Admin
     * @param token the users token
     */
    public static void setCurrentAdmin(Admin admin, String token) {
        adminController.setAdmin(admin, token);
    }

    public static Admin getCurrentAdmin() { return adminController.getAdmin(); }

    /**
     * sets the current User
     *
     * @param currentUser the current User
     * @param token the users token
     */
    public static void setCurrentUser(User currentUser, String token) {
        userController.setCurrentUser(currentUser, token);
        userController.setControlsShown(false);
    }

    public static User getCurrentUser() {
        return userController.getCurrentUser();
    }

    /**
     * Calls the function which updates the waiting list pane.
     */
    public static void updateUserWaitingLists() {
        for (UserController userController : cliniciansUserWindows.values()) {
            userController.populateWaitingList();
        }
    }

    /**
     * Calls the function which updates the transplant waiting list pane.
     */
    public static void updateTransplantWaitingList() {
        if (clinicianClinicianWaitingListController.hasToken()) {
            clinicianClinicianWaitingListController.updateTransplantList();
        }
        if (adminClinicianWaitingListController.hasToken()) {
            adminClinicianWaitingListController.updateTransplantList();
        }

    }

    /**
     * Calls the function which updates the available organs pane.
     */
    public static void updateAvailableOrgans() {
        if (clinicianClinicianAvailableOrgansController.hasToken()) {
            clinicianClinicianAvailableOrgansController.updateOrgans();
        }
        if (adminClinicianAvailableController.hasToken()) {
            adminClinicianAvailableController.updateOrgans();
        }

    }

    /**
     * sets the current clinican for account settings
     *
     * @param currentClinician the current Clinician
     * @param token the users token
     */
    public static void setCurrentClinicianForAccountSettings(Clinician currentClinician, String token) {
        clinicianSettingsController.setCurrentClinician(currentClinician, token);
    }

    /**
     * sets the login controller
     *
     * @param loginController the current login controller
     */
    public static void setLoginController(LoginController loginController) {
        WindowManager.loginController = loginController;
    }

    /**
     * sets the create User controller
     * @param createUserController the current create User controller
     */
    public static void setCreateUserController(CreateUserController createUserController) {
        WindowManager.createUserController = createUserController;
    }

    /**
     * sets the Clinician settings controller
     * @param clinicianSettingsController the current Clinician settings controller
     */
    public static void setClinicianAccountSettingsController(ClinicianSettingsController clinicianSettingsController) {
        WindowManager.clinicianSettingsController = clinicianSettingsController;
    }

    /**
     * sets the Clinician waiting list controller
     * @param clinicianWaitingListController the current Clinician waiting list controller
     */
    public static void setTransplantWaitingListController(ClinicianWaitingListController clinicianWaitingListController) {
        if (scenes.get(TFScene.clinician) == null) {
            WindowManager.clinicianClinicianWaitingListController = clinicianWaitingListController;
        } else {
            WindowManager.adminClinicianWaitingListController = clinicianWaitingListController;
        }
    }

    public static void setClinicianAvailableOrgansController(ClinicianAvailableOrgansController clinicianAvailableOrgansController) {
        if (scenes.get(TFScene.clinician) == null) {
            WindowManager.clinicianClinicianAvailableOrgansController = clinicianAvailableOrgansController;
        } else {
            WindowManager.adminClinicianAvailableController = clinicianAvailableOrgansController;
        }
    }

    /**
     * sets the Clinician controller
     * @param clinicianController the current Clinician controller
     */
    public static void setClinicianController(ClinicianController clinicianController) {
        WindowManager.clinicianController = clinicianController;
    }

    /**
     * sets the Admin controller
     * @param adminController the current Admin controller
     */
    public static void setAdminController(AdminController adminController) {
        WindowManager.adminController = adminController;
    }

    public static ClinicianController getClinicianController() {
        return clinicianController;
    }

    /**
     * updates the found users
     */
    public static void updateFoundClinicianUsers() {
        if (clinicianController.hasToken()) {
            clinicianController.updateFoundUsers();
        }
        if (adminController.hasToken()) {
            adminController.updateFoundUsers();
        }
    }

    /**
     * refreshes the Admin
     */
    public static void refreshAdmin() {
        adminController.refreshLatestProfiles();
        adminController.updateFoundUsers();
    }

    /**
     * show the dialog for deregistering a waiting list item
     * @param waitingListItem the waiting list item to deregister
     * @param user the User
     */
    public static void showDeregisterDialog(WaitingListItem waitingListItem, User user) {
        clinicianClinicianWaitingListController.showDeregisterDialog(waitingListItem, user);
    }

    public static Map<Stage, UserController> getCliniciansUserWindows() {
        return cliniciansUserWindows;
    }

    public static void closeAllChildren() {
        for (Stage stage: cliniciansUserWindows.keySet()) {
            stage.close();
        }
        cliniciansUserWindows.clear();
    }

    public static void setUserController(UserController userController) {
        WindowManager.userController = userController;
    }

    public static void setClinicianAccountSettingsEnterEvent() {
        clinicianSettingsController.setEnterEvent();
    }

    /**
     * Run the GUI.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        testing = false;
        if (args.length == 0) {
            launch(args);
        } else if (args.length == 1 && args[0].equals("-c")) {
            try {
                IO.setPaths();
            } catch (URISyntaxException e) {
                Debugger.error(e.getLocalizedMessage());
            }
        } else {
            Debugger.log("Please either run using:" +
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
        Debugger.log("Non-critical error caught, probably platform dependent.");
        Debugger.log(e.getStackTrace());
        Debugger.error(e.getStackTrace());
        if (Platform.isFxApplicationThread()) {
            Debugger.error(e.getStackTrace());
        } else {
            Debugger.error("An unexpected error occurred in " + t);
        }
    }

    public static boolean isTesting() {
        return testing;
    }


    /**
     * Creates an internal, non-persistant DataManager (For testing and debugging)
     * @return A new DataManager instance
     */
    public DataManager createLocalDataManager() {
        UsersDAO users = new UsersM();
        CliniciansDAO clinicians = new CliniciansM();
        AdminsDAO admins = new AdminsM();
        GeneralDAO general = new GeneralM(users,clinicians,admins);
        return new DataManager(users,clinicians,admins,general);
    }

    /**
     * Creates a standard DataManager which manipulates the Database via the API server.
     * @return A new DataManager instance
     */
    public DataManager createDatabaseDataManager() {
        String localServer = "http://localhost:7015/api/v1";
        String properServer = "http://csse-s302g3.canterbury.ac.nz:80/api/v1";
        String testingServer = "http://csse-s302g3.canterbury.ac.nz:80/testing/api/v1";

        APIServer server;
        if(testing) server = new APIServer(testingServer);
        else server = new APIServer(properServer);
        UsersDAO users = new UsersDB(server);
        CliniciansDAO clinicians = new CliniciansDB(server);
        AdminsDAO admins = new AdminsDB(server);
        GeneralDAO general = new GeneralDB(server);
        return new DataManager(users,clinicians,admins,general);
    }

    /**
     * checks the connection to the server.
     * @return returns true if can connect to the server, otherwise false
     */
    private boolean checkConnection() {
        try {
            if (!dataManager.getGeneral().status()) {
                Alert alert = createAlert(Alert.AlertType.CONFIRMATION, "Server offline", "Cannot Connect to Server", "Would you like to try again? (Will exit program if not)");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    return checkConnection();
                } else {
                    return false;
                }
            }

        } catch (HttpResponseException e) {
            Debugger.error(e.getLocalizedMessage());
        } catch (Exception e) {
            Alert alert = createAlert(Alert.AlertType.CONFIRMATION, "Server offline", "Cannot Connect to Server", "Would you like to try again? (Will exit program if not)");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                return checkConnection();
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Load in saved users and clinicians, and initialise the GUI.
     *
     * @param stage The stage to set the GUI up on
     */
    @Override
    public void start(Stage stage) {
        dataManager = createDatabaseDataManager();
        //dataManager = createLocalDataManager();

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


        if (checkConnection()) {
            try {
                IO.setPaths();
                setupDrugInteractionCache();
                for (TFScene scene : TFScene.values()) {
                    scenes.put(scene, new Scene(FXMLLoader.load(getClass().getResource(scene.getPath())), scene.getWidth(), scene.getHeight()));
                }
                loginController.setEnterEvent();
                createUserController.setEnterEvent();

                stage.setX(100);
                stage.setY(80);
                setScene(TFScene.login);
                stage.show();

            } catch (URISyntaxException e) {
                Debugger.error("Unable to read jar path. Please run from a directory with a simpler path.");
                Debugger.error(e.getLocalizedMessage());
                stop();
            } catch (IOException e) {
                Debugger.error(unableTo);
                Debugger.error(e.getLocalizedMessage());
                stop();
            }

            getScene(TFScene.clinician).setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.F5) {
                    updateTransplantWaitingList();
                    updateUserWaitingLists();
                    refreshAdmin();
                }
            });

            getScene(TFScene.admin).setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.F5) {
                    updateTransplantWaitingList();
                    updateUserWaitingLists();

                }
            });
        } else {
            stop();
        }
    }

    /**
     * Sets up a drug interaction cache
     */
    public void setupDrugInteractionCache(){
        Cache cache = IO.importCache(getJarPath() + File.separatorChar + "interactions.json");
        InteractionApi.setCache(cache);
    }

    /**
     * resets the scene
     * @param scene the scene to reset
     */
    public static void resetScene(TFScene scene) {
        try {
            scenes.remove(scene);
            scenes.put(scene, new Scene(FXMLLoader.load(WindowManager.class.getResource(scene.getPath())), MAIN_WINDOW_PREF_WIDTH,
                    MAIN_WINDOW_PREF_HEIGHT));
        } catch (IOException e) {
            Debugger.error(unableTo);
            Debugger.error(e.getLocalizedMessage());
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
            stage.setMinWidth(MAIN_WINDOW_MIN_WIDTH);
            stage.setMinHeight(MAIN_WINDOW_MIN_HEIGHT);
        } else {
            stage.setMinWidth(0);
            stage.setMinHeight(0);
        }
        stage.setWidth(scene.getWidth());
        stage.setHeight(scene.getHeight());
        stage.setScene(null);
        stage.setScene(scenes.get(scene));

        if (scene.getWidth() != MAIN_WINDOW_PREF_WIDTH) {
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
     * Logs out from the server before exiting the GUI.
     */
    @Override
    public void stop() {
        Debugger.log("Exiting GUI");
        clinicianClinicianAvailableOrgansController.stopTimer();
        if (userController != null && userController.hasToken()) {
            userController.serverLogout();
        }
        if (clinicianController != null && clinicianController.hasToken()) {
            clinicianController.serverLogout();
        }
        if (adminController != null && adminController.hasToken()) {
            adminController.serverLogout();
        }
        Platform.exit();
    }
}
