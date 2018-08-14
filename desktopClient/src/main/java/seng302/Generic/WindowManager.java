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
 * WindowManager class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class WindowManager extends Application {

    public static final int mainWindowMinWidth = 800, mainWindowMinHeight = 600, mainWindowPrefWidth = 1250, mainWindowPrefHeight = 725;

    private static Stage stage;
    private static HashMap<TFScene, Scene> scenes = new HashMap<>();
    private static Map<Stage, UserController> cliniciansUserWindows = new HashMap<>();
    private static Image icon;
    private static String dialogStyle, menuButtonStyle, selectedMenuButtonStyle;

    //Main windows
    private static LoginController loginController;
    private static CreateUserController createUserController;
    private static ClinicianController clinicianController;
    private static AdminController adminController;
    private static UserController userController;

    private static ClinicianSettingsController clinicianSettingsController;
    private static ClinicianWaitingListController clinicianClinicianWaitingListController, adminClinicianWaitingListController;

    private static DataManager dataManager;

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static void setDataManager(DataManager dataManager) {
        WindowManager.dataManager = dataManager;
    }

    private static boolean TESTING = true;

    private static Map<Object, Object> config = new ConfigParser().getConfig();

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
     * Creates a new user window from a clinician's view.
     * @param user The user to create the window for
     * @param token the users token
     */
    public static void newAdminsUserWindow(User user, String token){
        Stage stage = new Stage();
        stage.getIcons().add(WindowManager.getIcon());
        stage.setMinHeight(WindowManager.mainWindowMinHeight);
        stage.setMinWidth(WindowManager.mainWindowMinWidth);
        stage.setHeight(WindowManager.mainWindowPrefHeight);
        stage.setWidth(WindowManager.mainWindowPrefWidth);

        stage.initModality(Modality.NONE);

        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource("/fxml/user/user.fxml"));
            Parent root = loader.load();
            UserController newUserController = loader.getController();
            newUserController.setTitleBar(stage);

            newUserController.setCurrentUser(user, token);
            newUserController.addHistoryEntry("Clinician opened", "A clinician opened this profile to view and/or edit information.");

            newUserController.setControlsShown(true);
            cliniciansUserWindows.put(stage, newUserController);


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
     * Creates a new user window from a clinician's view.
     * @param user The user to create the window for
     * @param token the users token
     */
    public static void newCliniciansUserWindow(User user, String token){
        Stage stage = new Stage();
        stage.getIcons().add(WindowManager.getIcon());
        stage.setMinHeight(WindowManager.mainWindowMinHeight);
        stage.setMinWidth(WindowManager.mainWindowMinWidth);
        stage.setHeight(WindowManager.mainWindowPrefHeight);
        stage.setWidth(WindowManager.mainWindowPrefWidth);

        stage.initModality(Modality.NONE);
        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource("/fxml/user/user.fxml"));
            Parent root = loader.load();
            UserController newUserController = loader.getController();
            newUserController.setTitleBar(stage);

            newUserController.setCurrentUser(user, token);
            newUserController.addHistoryEntry("Clinician opened", "A clinician opened this profile to view and/or edit information.");

            newUserController.setControlsShown(true);
            newUserController.getAttributesController().setDeathControlsShown(true);
            cliniciansUserWindows.put(stage, newUserController);


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
     * sets the current admin
     *
     * @param admin the logged admin
     * @param token the users token
     */
    public static void setCurrentAdmin(Admin admin, String token) {
        adminController.setAdmin(admin, token);
    }

    public static Admin getCurrentAdmin() { return adminController.getAdmin(); }

    /**
     * sets the current user
     *
     * @param currentUser the current user
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
     * sets the current clinican for account settings
     *
     * @param currentClinician the current clinician
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
     * sets the create user controller
     * @param createUserController the current create user controller
     */
    public static void setCreateUserController(CreateUserController createUserController) {
        WindowManager.createUserController = createUserController;
    }

    /**
     * sets the clinician settings controller
     * @param clinicianSettingsController the current clinician settings controller
     */
    public static void setClinicianAccountSettingsController(ClinicianSettingsController clinicianSettingsController) {
        WindowManager.clinicianSettingsController = clinicianSettingsController;
    }

    /**
     * sets the clinician waiting list controller
     * @param clinicianWaitingListController the current clinician waiting list controller
     */
    public static void setTransplantWaitingListController(ClinicianWaitingListController clinicianWaitingListController) {
        if (scenes.get(TFScene.clinician) == null) {
            WindowManager.clinicianClinicianWaitingListController = clinicianWaitingListController;
        } else {
            WindowManager.adminClinicianWaitingListController = clinicianWaitingListController;
        }

    }

    /**
     * sets the clinician controller
     * @param clinicianController the current clinician controller
     */
    public static void setClinicianController(ClinicianController clinicianController) {
        WindowManager.clinicianController = clinicianController;
    }

    /**
     * sets the admin controller
     * @param adminController the current admin controller
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
     * refreshes the admin
     */
    public static void refreshAdmin() {
        adminController.refreshLatestProfiles();
        adminController.updateFoundUsers();
    }

    /**
     * show the dialog for deregistering a waiting list item
     * @param waitingListItem the waiting list item to deregister
     * @param user the user
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
        TESTING = false;
        if (args.length == 0) {
            launch(args);
        } else if (args.length == 1 && args[0].equals("-c")) {
            try {
                IO.setPaths();
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
        Debugger.log("Non-critical error caught, probably platform dependent.");
        Debugger.log(e.getStackTrace());
        Debugger.error(e.getStackTrace());
        e.printStackTrace();
        if (Platform.isFxApplicationThread()) {
            Debugger.error(e.getStackTrace());
        } else {
            Debugger.error("An unexpected error occurred in " + t);
        }
    }

    public static boolean isTESTING() {
        return TESTING;
    }

    public static Map<Object, Object> getConfig() {
        return config;
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
     * Creates a standard DataManager which manipulates the database via the API server.
     * @return A new DataManager instance
     */
    public DataManager createDatabaseDataManager() {
        String serverAddress = (String) config.get("server");
        if(TESTING) serverAddress = "http://csse-s302g3.canterbury.ac.nz/testing/api/v1";

        APIServer server = new APIServer(serverAddress);
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
                if (result.get() == ButtonType.OK) {
                    return checkConnection();
                } else {
                    return false;
                }
            }

        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Alert alert = createAlert(Alert.AlertType.CONFIRMATION, "Server offline", "Cannot Connect to Server", "Would you like to try again? (Will exit program if not)");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
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
                System.err.println("Unable to read jar path. Please run from a directory with a simpler path.");
                e.printStackTrace();
                stop();
            } catch (IOException e) {
                System.err.println("Unable to load fxml or save file.");
                e.printStackTrace();
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

            /*getScene(TFScene.userWindow).setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.F5) {
                    refreshUser();
                }
            });*/
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
     * Logs out from the server before exiting the GUI.
     */
    @Override
    public void stop() {
        Debugger.log("Exiting GUI");
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
