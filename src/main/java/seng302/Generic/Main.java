package seng302.Generic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import seng302.GUI.Controllers.*;
import seng302.GUI.TFScene;
import seng302.TUI.CommandLineInterface;
import seng302.User.Clinician;
import seng302.User.User;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Main class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class Main extends Application {
    public static final int mainWindowMinWidth = 800, mainWindowMinHeight = 600, mainWindowPrefWidth = 1250, mainWindowPrefHeight = 725;

    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Clinician> clinicians = new ArrayList<>();

    private static Stage stage;
    private static HashMap<TFScene, Scene> scenes = new HashMap<>();
    private static ArrayList<Stage> cliniciansUserWindows = new ArrayList<>();

    private static LoginController loginController;
    private static CreateAccountController createAccountController;
    private static ClinicianController clinicianController;

    private static AccountSettingsController accountSettingsController;
    private static ClinicianAccountSettingsController clinicianAccountSettingsController;
    private static UserWindowController userWindowController;
    private static MedicationsController medicationsController;
    private static TransplantWaitingListController transplantWaitingListController;
    private static WaitingListController waitingListController;

    private static String dialogStyle;

    public static void addCliniciansUserWindow(Stage stage) {cliniciansUserWindows.add(stage);}

    public static void setClinician(Clinician clinician) {
        clinicianController.setClinician(clinician);
        clinicianController.updateDisplay();
        clinicianController.updateFoundUsers("");
        clinicianController.updatePageButtons();
        clinicianController.displayCurrentPage();
        clinicianController.updateResultsSummary();
    }

    public static void setClinicianController(ClinicianController clinicianController) {
        Main.clinicianController = clinicianController;
    }

    public static void setCurrentUser(User currentUser) {
        userWindowController.setCurrentUser(currentUser);
        userWindowController.populateUserFields();
        userWindowController.populateHistoryTable();

        waitingListController.setCurrentUser(currentUser);
        waitingListController.populateWaitingList();

        medicationsController.initializeUser(currentUser);
    }

    public static void addCurrentToMedicationUndoStack() {
        userWindowController.addCurrentToMedicationUndoStack();
    }

    public static void updateMedications() {
        medicationsController.updateMedications();
    }

    /**
     * Sets the medications view to be unable to edit for a user.
     */
    public static void medicationsViewForUser() {
        medicationsController.setControlsShown(false);
    }

    /**
     * Sets the medications view to be able to edit for a clinican.
     */
    public static void medicationsViewForClinician() {
        medicationsController.setControlsShown(true);
    }

    public static void setCurrentUserForAccountSettings(User currentUser) {
        accountSettingsController.setCurrentUser(currentUser);
        accountSettingsController.populateAccountDetails();
    }

    public static void setCurrentClinicianForAccountSettings(Clinician currentClinician) {
        clinicianAccountSettingsController.setCurrentClinician(currentClinician);
        clinicianAccountSettingsController.populateAccountDetails();
    }

    public static void setLoginController(LoginController loginController) {
        Main.loginController = loginController;
    }

    public static void setCreateAccountController(CreateAccountController createAccountController) {
        Main.createAccountController = createAccountController;
    }

    public static void setMedicationsController(MedicationsController medicationsController) {
        Main.medicationsController = medicationsController;
    }

    public static void setWaitingListController(WaitingListController waitingListController) {
        Main.waitingListController = waitingListController;
    }

    public static void setAccountSettingsController(AccountSettingsController accountSettingsController) {
        Main.accountSettingsController = accountSettingsController;
    }

    public static void setClincianAccountSettingsController(ClinicianAccountSettingsController clinicianAccountSettingsController) {
        Main.clinicianAccountSettingsController = clinicianAccountSettingsController;
    }

    public static void setTransplantWaitingListController(TransplantWaitingListController transplantWaitingListController) {
        Main.transplantWaitingListController = transplantWaitingListController;
    }

    public static ClinicianController getClinicianController() {
        return Main.clinicianController;
    }

    public static TransplantWaitingListController getTransplantWaitingListController() { return Main.transplantWaitingListController; }

    public static ArrayList<Stage> getCliniciansUserWindows(){
        return cliniciansUserWindows;
    }

    public static void setUserWindowController(UserWindowController userWindowController) {
        Main.userWindowController = userWindowController;
    }

    public static String getDialogStyle() {
        return dialogStyle;
    }

    public static void setAccountSettingsEnterEvent() {
        accountSettingsController.setEnterEvent();
    }

    public static void setClinicianAccountSettingsEnterEvent() {
        clinicianAccountSettingsController.setEnterEvent();
    }

    /**
     * Find a specific user from the user list based on their name.
     *
     * @param names The names of the user to search for
     * @return The user objects that matched the input names
     */
    public static ArrayList<User> getUserByName(String[] names) {
        ArrayList<User> found = new ArrayList<>();
        if (names.length == 0) {
            return Main.users;
        }
        int matched;
        for (User user : Main.users) {
            matched = 0;
            for (String name : user.getNameArray()) {
                if (name.toLowerCase().contains(names[matched].toLowerCase())) {
                    matched++;
                    if (matched == names.length) {
                        break;
                    }
                }
            }
            if (matched == names.length) {
                found.add(user);
            }
        }
        return found;
    }

    /**
     * Find a specific user from the user list based on their id.
     *
     * @param id The id of the user to search for
     * @return The user object or null if the user was not found
     */
    public static User getUserById(long id) {
        if (id < 0) {
            return null;
        }
        User found = null;
        for (User user : Main.users) {
            if (user.getId() == id) {
                found = user;
                break;
            }
        }
        return found;
    }

    /**
     * Returns true if the given token matches the beginning of at least one of the given names.
     * Otherwise returns false.
     * @param names The list of names
     * @param token The token to test
     * @return A boolean value which denotes whether or not the token matches a name
     */
    public static boolean matchesAtleastOne(String[] names, String token){
        for(String name:names){
            if(matches(name, token)){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of users matching the given search term.
     * Results are sorted by their score first, and alphabetically second.
     * The search term is broken into tokens, which should be separated by spaces in the term param.
     * If every token matches at least part of the
     * beginning of a one of part of a users name, that user will be returned.
     * @param term The search term containing space separated tokens
     * @return An ArrayList of users sorted by score first, and alphabetically by name second
     */
    public static ArrayList<User> getUsersByNameAlternative(String term){
        String[] t = term.split(" ",-1);
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(t));
        if(tokens.contains("")){
            tokens.remove("");
        }
        ArrayList<User> matched = new ArrayList<>();
        for(User d: users){
            boolean allTokensMatchAName = true;
            for(String token:tokens){
                if(!matchesAtleastOne(d.getNameArray(), token)){
                    allTokensMatchAName = false;
                }
            }
            if(allTokensMatchAName){
                matched.add(d);
            }
        }
        matched.sort((o1, o2) -> {
            Integer o1Score = 0;
            for (String name : o1.getNameArray()) {
                for (String token : tokens) {
                    if (matches(name, token)) {
                        o1Score += lengthMatchedScore(name, token);
                    }
                }
            }
            Integer o2Score = 0;
            for (String name : o2.getNameArray()) {
                for (String token : tokens) {
                    if (matches(name, token)) {
                        o2Score += lengthMatchedScore(name, token);
                    }
                }
            }
            int scoreComparison = o2Score.compareTo(o1Score);
            if (scoreComparison == 0) {
                return o1.getName().compareTo(o2.getName());
            }
            return scoreComparison;
        });
        return matched;
    }

    /**
     * Returns the length of the longest matched common substring starting from the
     * beginning of string and searchTerm, minus the length of the string.
     * The maximum value returned is zero. This method is used for scoring different
     * strings against a search term.
     * @param string The string which is being searched
     * @param searchTerm The term which is being looked for
     * @return The length of the longest substring minus the length of string
     */
    public static int lengthMatchedScore(String string, String searchTerm)
    {
        string = string.toLowerCase();
        searchTerm = searchTerm.toLowerCase();
        int maxLength = Math.min(string.length(), searchTerm.length());
        int i;
        for (i = 0; i < maxLength; i++)
        {
            if (searchTerm.charAt(i) != string.charAt(i)) {
                return i-string.length();
            }
        }
        return i-string.length();
    }

    /**
     * Returns true if the given token matches the beginning of the given string.
     * Otherwise returns false.
     * Case is ignored.
     * @param string The string to test
     * @param token The given search token
     * @return True if token and string at least start the same, otherwise false.
     */
    public static boolean matches(String string, String token){
        string = string.toLowerCase();
        token = token.toLowerCase();
        return string.matches(token + ".*");
    }

    /**
     * Run the GUI.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            launch(args);
        } else if (args.length == 1 && args[0].equals("-c")) {
            try {
                IO.setPaths();
                CommandLineInterface commandLineInterface = new CommandLineInterface();
                commandLineInterface.run();
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
     *
     * Solves an error on Linux systems with menu bar skin issues cropping up on the FX thread
     *
     * @param t Thread?
     * @param e Exception?
     */
    private static void showError(Thread t, Throwable e) {
        System.err.println("Non-critical error caught, probably platform dependent.");
        e.printStackTrace();
        if (Platform.isFxApplicationThread()) {
            System.out.println(e);
        } else {
            System.err.println("An unexpected error occurred in "+t);
        }
    }

    /**
     * Load in saved users and clinicians, and initialise the GUI.
     * @param stage The stage to set the GUI up on
     */
    @Override
    public void start(Stage stage) {
        // This fixes errors which occur in different threads in TestFX
        Thread.setDefaultUncaughtExceptionHandler(Main::showError);

        Main.stage = stage;
        stage.setTitle("Transplant Finder");
        stage.setOnHiding( closeAllWindows -> {
            for(Stage userWindow:cliniciansUserWindows){
                userWindow.close();
            }
        });
        dialogStyle = Main.class.getResource("/css/dialog.css").toExternalForm();
        //stage.getIcons().add(new Image(getClass().getResourceAsStream("/test.png")));
        try {
            IO.setPaths();
            File users = new File(IO.getUserPath());
            if (users.exists()) {
                if (!IO.importUsers(users.getAbsolutePath(), true)) {
                    throw new IOException("User save file could not be loaded.");
                }
            } else {
                if (!users.createNewFile()) {
                    throw new IOException("User save file could not be created.");
                }
            }
            File clinicians = new File(IO.getClinicianPath());
            if (clinicians.exists()) {
                if (!IO.importUsers(clinicians.getAbsolutePath(), false)) {
                    throw new IOException("Clinician save file could not be loaded.");
                }
            } else {
                if (!clinicians.createNewFile()) {
                    throw new IOException("Clinician save file could not be created.");
                }
                Clinician defaultClinician = new Clinician("default", "default", "default");
                Main.clinicians.add(defaultClinician);
                IO.saveUsers(IO.getClinicianPath(), false);
            }
            IO.streamOut = History.init();
            scenes.put(TFScene.login, new Scene(FXMLLoader.load(getClass().getResource("/fxml/login.fxml")), 400, 250));
            loginController.setEnterEvent();
            scenes.put(TFScene.createAccount, new Scene(FXMLLoader.load(getClass().getResource("/fxml/createAccount.fxml")), 400, 415));
            createAccountController.setEnterEvent();
            scenes.put(TFScene.userWindow, new Scene(FXMLLoader.load(getClass().getResource("/fxml/userWindow.fxml")), mainWindowPrefWidth, mainWindowPrefHeight));
            scenes.put(TFScene.clinician, new Scene(FXMLLoader.load(getClass().getResource("/fxml/clinician.fxml")), mainWindowPrefWidth, mainWindowPrefHeight));
            scenes.put(TFScene.transplantList, new Scene(FXMLLoader.load(getClass().getResource("/fxml/transplantList.fxml")),mainWindowPrefWidth, mainWindowPrefHeight));

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

    public static void clearUserScreen() {
        try {
            scenes.remove(TFScene.userWindow);
            scenes.put(TFScene.userWindow, new Scene(FXMLLoader.load(Main.class.getResource("/fxml/userWindow.fxml")), mainWindowPrefWidth, mainWindowPrefHeight));
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
        }
    }

    public static Scene getScene(TFScene scene) {
        return scenes.get(scene);
    }

    public static void setScene(TFScene scene) {
        stage.setScene(scenes.get(scene));
        if (scene == TFScene.userWindow || scene == TFScene.clinician || scene == TFScene.transplantList) {
            stage.setMinWidth(mainWindowMinWidth);
            stage.setMinHeight(mainWindowMinHeight);
            stage.setWidth(mainWindowPrefWidth);
            stage.setHeight(mainWindowPrefHeight);
            stage.setResizable(true);
        } else {
            stage.setMinWidth(0);
            stage.setMinHeight(0);
            if (scene == TFScene.login) {
                stage.setScene(null);
                stage.setScene(scenes.get(scene));
            }
            stage.setResizable(false);
        }
    }

    /**
     * Create a styled alert dialog.
     *
     * @param alertType The type of alert to create
     * @param title The title for the alert dialog
     * @param header The header text for the alert dialog
     * @param content The content text for the alert dialog
     * @return The created alert object
     */
    public static Alert createAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(dialogStyle);
        alert.getDialogPane().getStyleClass().add("dialog");
        return alert;
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
