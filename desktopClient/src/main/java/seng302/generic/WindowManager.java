package seng302.generic;

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
import seng302.data.database.AdminsDB;
import seng302.data.database.CliniciansDB;
import seng302.data.database.GeneralDB;
import seng302.data.database.UsersDB;
import seng302.data.interfaces.AdminsDAO;
import seng302.data.interfaces.CliniciansDAO;
import seng302.data.interfaces.GeneralDAO;
import seng302.data.interfaces.UsersDAO;
import seng302.data.local.AdminsM;
import seng302.data.local.CliniciansM;
import seng302.data.local.GeneralM;
import seng302.data.local.UsersM;
import seng302.gui.controllers.admin.AdminController;
import seng302.gui.controllers.clinician.ClinicianAvailableOrgansController;
import seng302.gui.controllers.clinician.ClinicianController;
import seng302.gui.controllers.clinician.ClinicianSettingsController;
import seng302.gui.controllers.clinician.ClinicianWaitingListController;
import seng302.gui.controllers.LoginController;
import seng302.gui.controllers.user.CreateUserController;
import seng302.gui.controllers.user.UserController;
import seng302.gui.TFScene;
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

import static seng302.generic.IO.getJarPath;

/**
 * WindowManager class that contains program initialization code and data that must be accessible from multiple parts of the
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

            User latest = WindowManager.getDataManager().getUsers().getUser((int)user.getId(), token);
            latest = latest == null ? user : latest;
            newUserController.setCurrentUser(latest, token);
            newUserController.addHistoryEntry("Clinician opened", "A clinician opened this profile to view and/or edit information.");

            newUserController.setControlsShown(true);
            cliniciansUserWindows.put(stage, newUserController);


            Scene newScene = new Scene(root, MAIN_WINDOW_PREF_WIDTH, MAIN_WINDOW_PREF_HEIGHT);
            stage.setScene(newScene);
            stage.show();
            newUserController.setRefreshEvent();
        } catch (IOException | NullPointerException e) {
            Debugger.error(unableTo);
            Debugger.error(e.getLocalizedMessage());
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
            newUserController.addHistoryEntry("clinician opened", "A clinician opened this profile to view and/or edit information.");

            newUserController.setControlsShown(true);
            newUserController.getAttributesController().setDeathControlsShown(true);
            cliniciansUserWindows.put(stage, newUserController);


            Scene newScene = new Scene(root, MAIN_WINDOW_PREF_WIDTH, MAIN_WINDOW_PREF_HEIGHT);
            stage.setScene(newScene);
            stage.show();
            newUserController.setRefreshEvent();
        } catch (IOException | NullPointerException e) {
            Debugger.error(unableTo);
            Debugger.error(e.getLocalizedMessage());
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
     * Calls for an auto refresh of the avaliable organs table after the next tick.
     * @param value the bool value. T = refresh F = no refresh
     */
    public static void updateAvailableOrgansAutoRefresh(boolean value) {
        if (clinicianClinicianAvailableOrgansController.hasToken()) {
            clinicianClinicianAvailableOrgansController.setAutoRefresh(true);
        }
        if (adminClinicianAvailableController.hasToken()) {
            adminClinicianAvailableController.setAutoRefresh(true);
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

    public static void setClinicianAvailableOrgansController(ClinicianAvailableOrgansController clinicianAvailableOrgansController) {
        if (scenes.get(TFScene.clinician) == null) {
            WindowManager.clinicianClinicianAvailableOrgansController = clinicianAvailableOrgansController;
        } else {
            WindowManager.adminClinicianAvailableController = clinicianAvailableOrgansController;
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
     * Run the gui.
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
                    "\ngui mode: java -jar app-0.0.jar" +
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
        Debugger.error(e.getMessage());
        if (Platform.isFxApplicationThread()) {
            Debugger.error(e.getLocalizedMessage());
        } else {
            Debugger.error("An unexpected error occurred in " + t);
        }
    }

    public static boolean isTesting() {
        return testing;
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
    private DataManager createDatabaseDataManager() {
        String serverAddress = (String) config.get("server");
        if(testing) serverAddress = "http://csse-s302g3.canterbury.ac.nz/testing/api/v1";

        APIServer server = new APIServer("http://localhost:7015/api/v1");
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
     * Load in saved users and clinicians, and initialise the gui.
     *
     * @param stage The stage to set the gui up on
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
                }
            });

            getScene(TFScene.admin).setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.F5) {
                    adminController.refresh();
                }
            });

            getScene(TFScene.userWindow).setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.F5) {
                    userController.refresh();
                }
            });
        } else {
            stop();
        }
    }

    /**
     * Sets up a drug interaction cache
     */
    private void setupDrugInteractionCache(){
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

        if (!(scene.getWidth() == MAIN_WINDOW_PREF_WIDTH)) {
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
     * Logs out from the server before exiting the gui.
     */
    @Override
    public void stop() {
        Debugger.log("Exiting gui");
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
