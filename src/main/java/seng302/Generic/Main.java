package seng302.Generic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seng302.GUI.Controllers.*;
import seng302.GUI.TFScene;
import seng302.TUI.CommandLineInterface;
import seng302.User.Admin;
import seng302.User.Attribute.LoginType;
import seng302.User.Clinician;
import seng302.User.User;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static java.lang.Integer.max;

/**
 * Main class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class Main extends Application {
    public static final int mainWindowMinWidth = 800, mainWindowMinHeight = 600, mainWindowPrefWidth = 1250, mainWindowPrefHeight = 725;
    private static long nextUserId = -1, nextClinicianId = -1, nextAdminId = -1;
    private static Integer nextWaitingListId = -1;

    public static ObservableList<User> users = FXCollections.observableArrayList();
    public static ObservableList<Clinician> clinicians = FXCollections.observableArrayList();
    public static ObservableList<Admin> admins = FXCollections.observableArrayList();

    private static Stage stage;
    private static HashMap<TFScene, Scene> scenes = new HashMap<>();
    private static ArrayList<Stage> cliniciansUserWindows = new ArrayList<>();

    private static LoginController loginController;
    private static CreateAccountController createAccountController;
    private static ClinicianController clinicianController;
    private static AdminController adminController;

    private static AccountSettingsController accountSettingsController;
    private static ClinicianAccountSettingsController clinicianAccountSettingsController;
    private static UserWindowController userWindowController;
    private static MedicationsController medicationsController;
    private static TransplantWaitingListController transplantWaitingListController;
    private static MedicalHistoryDiseasesController medicalHistoryDiseasesController;
    private static MedicalHistoryProceduresController medicalHistoryProceduresController;
    private static WaitingListController waitingListController;

    private static String dialogStyle;

    public static Stage getStage() {
        return stage;
    }

    public static void addCliniciansUserWindow(Stage stage) {cliniciansUserWindows.add(stage);}

    public static void setClinician(Clinician clinician) {
        clinicianController.setClinician(clinician);
        clinicianController.updateDisplay();
        clinicianController.updateFoundUsers();
    }

    public static void setAdmin(Admin admin) {
        System.out.println("Main: Called set admin");
        adminController.setAdmin(admin);
    }

    public static void setCurrentUser(User currentUser) {
        userWindowController.setCurrentUser(currentUser);
        userWindowController.populateUserFields();
        userWindowController.populateHistoryTable();

        medicalHistoryDiseasesController.setCurrentUser(currentUser);
        medicalHistoryProceduresController.setCurrentUser(currentUser);
        waitingListController.setCurrentUser(currentUser);
        waitingListController.populateWaitingList();

        medicationsController.initializeUser(currentUser);
        controlViewForUser();
    }

    public static void addCurrentToMedicationUndoStack() {
        userWindowController.addCurrentToMedicationUndoStack();
    }

    public static void addCurrentToDiseaseUndoStack() {
        userWindowController.addCurrentToDiseaseUndoStack();
    }

    public static void updateDiseases() {
        medicalHistoryDiseasesController.updateDiseases();
    }


    public static void updateMedications() {
        medicationsController.updateMedications();
    }

    public static void addCurrentToProcedureUndoStack() {
        userWindowController.addCurrentToProceduresUndoStack();
    }

    public static void updateProcedures() {
        medicalHistoryProceduresController.updateProcedures();
    }

    /**
     * Adds the current user to the waiting list undo stack.
     */
    public static void addCurrentToWaitingListUndoStack() {
        userWindowController.addCurrentToWaitingListUndoStack();
    }

    /**
     * Calls the function which updates the waiting list pane.
     */
    public static void updateWaitingList() {
        waitingListController.populateWaitingList();

    }

    /**
     * Calls the function which updates the transplant waiting list pane.
     */
    public static void updateTransplantWaitingList() {
        transplantWaitingListController.updateTransplantList();
    }

    /**
     * Sets which controls for each panel are visible to the user.
     */
    public static void controlViewForUser() {
        medicationsController.setControlsShown(false);
        userWindowController.setControlsShown(false);
        waitingListController.setControlsShown(false);
        medicalHistoryProceduresController.setControlsShown(false);
        medicalHistoryDiseasesController.setControlsShown(false);
    }

    /**
     * Sets which controls for each panel are visible to the clinician.
     */
    public static void controlViewForClinician() {
        medicationsController.setControlsShown(true);
        userWindowController.setControlsShown(true);
        waitingListController.setControlsShown(true);
        medicalHistoryProceduresController.setControlsShown(true);
        medicalHistoryDiseasesController.setControlsShown(true);
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

    public static void setMedicalHistoryDiseasesController(MedicalHistoryDiseasesController medicalHistoryDiseasesController) {
        Main.medicalHistoryDiseasesController = medicalHistoryDiseasesController;
    }

    public static void setMedicalHistoryProceduresController(MedicalHistoryProceduresController medicalHistoryProceduresController) {
        Main.medicalHistoryProceduresController = medicalHistoryProceduresController;
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

    public static void setClinicianController(ClinicianController clinicianController) {
        Main.clinicianController = clinicianController;
    }

    public static void setAdminController(AdminController adminController) {
        Main.adminController = adminController;
    }


    public static ClinicianController getClinicianController() {
        return Main.clinicianController;
    }

    public static UserWindowController getUserWindowController() {
        return userWindowController;
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

    public static Integer getNextWaitingListId() {
        if (!userWindowController.getCurrentUser().getWaitingListItems().isEmpty()){
            nextWaitingListId = userWindowController.getCurrentUser().getWaitingListItems().size();
        } else {
            nextWaitingListId++;
        }
        return nextWaitingListId;
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
        for (User user : users) {
            if (user.getId() == id) {
                found = user;
                break;
            }
        }
        return found;
    }

    public static WaitingListController getWaitingListController(){
        return waitingListController;
    }

    /**
     * Find a specific user from the user list based on their name.
     *
     * @param names The names of the user to search for
     * @return The user objects that matched the input names
     */
    public static ObservableList<User> getUserByName(String[] names) {
        ObservableList<User> found = FXCollections.observableArrayList();
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
     * Searches through all the users using the given search term and returns the results which match.
     * The search term is broken into tokens separated by spaces. Each token must match at least one
     * part of the user's given or preferred names.
     *
     * For a token to match a name, both must begin the same and there must be no unmatched characters in the
     * token. For example, the token "dani" would not match the name "dan", but would match "daniel".
     *
     * The results are returned sorted by a score according to which names were matched.
     * See scoreUserOnSearch(User, List<String>)
     * If two users are ranked the same, they're sorted alphabetically
     * @param term The search term which will be broken into space separated tokens
     * @return A sorted list of results
     */
    public static ArrayList<User> getUsersByNameAlternative(String term){
        System.out.println("search: "+"'"+term+"'");
        if(term.equals("")){
            System.out.println("Empty");
            ArrayList<User> sorted = new ArrayList<>(Main.users);
            Collections.sort(sorted, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            return sorted;
        }
        String[] t = term.split(" ",-1);
        ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(t));
        System.out.println("token 1: " + "'"+tokens.get(0)+"'");
        //System.out.println("token 2: " + "'"+tokens.get(1)+"'");
        if(tokens.contains("")){
            tokens.remove("");
        }
        ArrayList<User> matched = new ArrayList<User>();
        for(User user: users){
            if(scoreUserOnSearch(user, tokens) > 0){
                matched.add(user);
            }
        }
        Collections.sort(matched, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                Integer o1Score= scoreUserOnSearch(o1, tokens);
                Integer o2Score = scoreUserOnSearch(o2, tokens);

                int scoreComparison = o2Score.compareTo(o1Score);
                if(scoreComparison == 0){
                    return o1.getName().compareTo(o2.getName());
                }
                return scoreComparison;
            }
        });
        return matched;
    }

    /**
     * Returns a score for a user based on how well their name matches the given search tokens.
     * Every token needs to entirely match all or some of one of the user's names starting at the beginning of each, otherwise
     * the lowest possible score, zero, is returned.
     *
     * For example, the tokens {"abc","def","ghi"} would match a user with the name "adcd defg ghij", so some positive integer
     * would be returned. But for a user with the name "abc def", zero would be returned as one token is unmatched. Likewise,
     * a user with the name "abw d ghi" would score zero because the tokens "abc" and "def" are un-matched.
     *
     * Matches on different parts of a name add different amounts to the total score. Last name matches contribute the most to the total score,
     * followed by, preferred last name, preferred first name, preferred middle names, first name, and middle names in descending order.
     * @param user The user whose name will be be scored
     * @param searchTokens The search tokens which will be compared with the given user's name
     * @return An integer score
     */
    public static int scoreUserOnSearch(User user, List<String> searchTokens){
        List<String> tokens = new ArrayList<String>();
        tokens.addAll(searchTokens);


        if(!allTokensMatched(user, tokens)){
            return 0;
        }
        int score = 0;
        String[] names = user.getNameArray();
        String[] prefNames = user.getPreferredNameArray();
        // Last name
        score += scoreNames(names, tokens, max(names.length-1,1), names.length, 6);

        if(!Arrays.equals(names, prefNames)) {
            // Preferred last name
            score += scoreNames(prefNames, tokens, max(prefNames.length - 1, 1), prefNames.length, 5);
            // Preferred first name
            score += scoreNames(prefNames, tokens, 0, 1, 4);
            // Preferred middle names
            score += scoreNames(prefNames, tokens, 1, prefNames.length - 1, 3);
        }

        //first name
        score += scoreNames(names, tokens, 0, 1, 2);
        //middle names
        score += scoreNames(names, tokens, 1, names.length-1, 1);

        return score;
    }


    /**
     * Returns true if all given tokens match at least one name from the given user's name array
     * or preferred name array. For a token to match a name, the beginning of each must be the same.
     * @param user The use whose names will be checked against the tokens
     * @param searchTokens The tokens
     * @return True if all tokens match at least one name, otherwise false
     */
    public static boolean allTokensMatched(User user, List<String> searchTokens){
        List<String> tokens = new ArrayList<String>();
        tokens.addAll(searchTokens);
        for(String name:user.getNameArray()){
            for(String token: new ArrayList<String>(tokens)){
                if(lengthMatchedScore(name, token)>0){
                    tokens.remove(token);
                }
            }
        }

        for(String name:user.getPreferredNameArray()){
            for(String token: new ArrayList<String>(tokens)){
                if(lengthMatchedScore(name, token)>0){
                    tokens.remove(token);
                }
            }
        }
        return tokens.isEmpty();
    }


    /**
     * Calculates a score based on the number of names between from and to which match at least one of the given tokens.
     * For each name which matches at least one token, the value of weight is added to the total score.
     *
     * For a token to match a name, both must begin the same and there must be no unmatched characters in the
     * token. For example, the token "dani" would not match the name "dan", but would match "daniel".
     * @param names The names which the tokens will attempt to match
     * @param tokens The list of tokens which will compared against the names
     * @param from The index of the first name to try
     * @param to One less than the last index which will be tried
     * @param weight The weight which will be awarded for each matched name
     * @return An integer score
     */
    public static int scoreNames(String[] names, List<String> tokens, int from, int to, int weight){
        if(names.length >= to && to > from){
            String[] middleNames = Arrays.copyOfRange(names, from, to);
            int score = 0;
            for(String middleName:middleNames){
                if(nameMatchesOneOf(middleName, tokens)){
                    score += weight;
                }
            }
            return score;
        }
        return 0;
    }

    /**
     * Returns true if at least of of the given tokens matches the given name.
     * For a token to match a name, both must begin the same and there must be no unmatched characters in the
     * token.
     *
     * For example, the token "dani" would not match the name "dan", but would match "daniel"
     * @param name The name which is compared with each token until a match is found
     * @param tokens The list of tokens to try against the name
     * @return True if a match was found, otherwise false
     */
    public static boolean nameMatchesOneOf(String name, List<String> tokens){
        for(String token:tokens){
            if(lengthMatchedScore(name, token) > 0){
                return true;
            }
        }
        return false;
    }



    /**
     * Returns a list of users matching the given search term for region.
     * If every token matches at least part of the
     * beginning of a one of part of a users name, that user will be returned.
     * @param term The search term containing space separated tokens
     * @return An ArrayList of users sorted by score first, and alphabetically by name second
     */
    public static ArrayList<User> getUsersByRegionAlternative(String term){
        String[] t = term.split(" ",-1);
        ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(t));
        if(tokens.contains("")){
            tokens.remove("");
        }
        ArrayList<User> matched = new ArrayList<User>();
        for(User d: users){
            if(d.getRegion() != null) {
                boolean allTokensMatchAName = true;
                for(String token:tokens){
                    if(!matchesAtleastOne(d.getRegion().split(" "), token)){
                        allTokensMatchAName = false;
                    }
                }
                if(allTokensMatchAName){
                    matched.add(d);
                }
            }

        }
        return matched;
    }

    /**
     * Returns a list of users matching the given search term for age.
     * If every token matches at least part of the
     * beginning of a one of part of a users name, that user will be returned.
     * @param term The search term containing space separated tokens
     * @return An ArrayList of users sorted by score first, and alphabetically by name second
     */
    public static ArrayList<User> getUsersByAgeAlternative(String term){
        String[] t = term.split(" ",-1);
        ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(t));
        if(tokens.contains("")){
            tokens.remove("");
        }
        ArrayList<User> matched = new ArrayList<User>();
        for(User d: users){
            if(d.getRegion() != null) {
                boolean allTokensMatchAName = true;
                for(String token:tokens){
                    if(!matches(d.getAgeString(), token)){
                        allTokensMatchAName = false;
                    }
                }
                if(allTokensMatchAName){
                    matched.add(d);
                }
            }

        }
        return matched;
    }


    /**
     * Returns the length of the longest matched common substring starting from the
     * beginning of string and searchTerm as long as the entire searchTerm is matched. If there
     * are unmatched characters in the searchTerm, zero is returned
     * @param string The string which is being searched
     * @param searchTerm The term which is being looked for
     * @return The length of the longest substring if the searchTerm is matched entirely
     */
    public static int lengthMatchedScore(String string, String searchTerm)
    {
        string = string.toLowerCase();
        searchTerm = searchTerm.toLowerCase();
        int i;
        if(searchTerm.length() > string.length()) return 0;
        for (i = 0; i < searchTerm.length(); i++)
        {
            if (searchTerm.charAt(i) != string.charAt(i)) {
                return 0;
            }
        }
        return i;
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
//        System.err.println("Non-critical error caught, probably platform dependent.");
//        e.printStackTrace();
//        if (Platform.isFxApplicationThread()) {
//            System.out.println(e);
//        } else {
//            System.err.println("An unexpected error occurred in "+t);
//        }
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
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        try {
            IO.setPaths();
            File users = new File(IO.getUserPath());
            if (users.exists()) {
                if (!IO.importUsers(users.getAbsolutePath(), LoginType.USER)) {
                    throw new IOException("User save file could not be loaded.");
                }
            } else {
                if (!users.createNewFile()) {
                    throw new IOException("User save file could not be created.");
                }
            }
            File clinicians = new File(IO.getClinicianPath());
            if (clinicians.exists()) {
                if (!IO.importUsers(clinicians.getAbsolutePath(), LoginType.CLINICIAN)) {
                    throw new IOException("Clinician save file could not be loaded.");
                }
            } else {
                if (!clinicians.createNewFile()) {
                    throw new IOException("Clinician save file could not be created.");
                }
                Clinician defaultClinician = new Clinician("default", "default", "default");
                Main.clinicians.add(defaultClinician);
                IO.saveUsers(IO.getClinicianPath(), LoginType.CLINICIAN);

            }
            File admins = new File(IO.getAdminPath());
            if (admins.exists()) {
                if (!IO.importUsers(admins.getAbsolutePath(), LoginType.ADMIN)) {
                    throw new IOException("Admin save file could not be loaded.");
                }
            } else {
                if (!admins.createNewFile()) {
                    throw new IOException("Admin save file could not be created.");
                }
                Admin defaultAdmin = new Admin("admin", "default", "default_admin");
                Main.admins.add(defaultAdmin);
                IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);

            }
            IO.streamOut = History.init();
            scenes.put(TFScene.login, new Scene(FXMLLoader.load(getClass().getResource("/fxml/login.fxml")),
                    TFScene.login.getWidth(), TFScene.login.getHeight()));
            scenes.put(TFScene.createAccount, new Scene(FXMLLoader.load(getClass().getResource("/fxml/createAccount.fxml")),
                    TFScene.createAccount.getWidth(), TFScene.createAccount.getHeight()));
            scenes.put(TFScene.userWindow, new Scene(FXMLLoader.load(getClass().getResource("/fxml/userWindow.fxml")),
                    mainWindowPrefWidth, mainWindowPrefHeight));
            scenes.put(TFScene.clinician, new Scene(FXMLLoader.load(getClass().getResource("/fxml/clinician.fxml")),
                    mainWindowPrefWidth, mainWindowPrefHeight));
            scenes.put(TFScene.transplantList, new Scene(FXMLLoader.load(getClass().getResource("/fxml/transplantList.fxml")),
                    mainWindowPrefWidth, mainWindowPrefHeight));

            loginController.setEnterEvent();
            createAccountController.setEnterEvent();
            scenes.put(TFScene.userWindow, new Scene(FXMLLoader.load(getClass().getResource("/fxml/userWindow.fxml")), mainWindowPrefWidth, mainWindowPrefHeight));
            scenes.put(TFScene.clinician, new Scene(FXMLLoader.load(getClass().getResource("/fxml/clinician.fxml")), mainWindowPrefWidth, mainWindowPrefHeight));
            scenes.put(TFScene.transplantList, new Scene(FXMLLoader.load(getClass().getResource("/fxml/transplantList.fxml")),mainWindowPrefWidth, mainWindowPrefHeight));
            scenes.put(TFScene.admin, new Scene(FXMLLoader.load(getClass().getResource("/fxml/admin.fxml")), mainWindowPrefWidth, mainWindowPrefHeight));

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

    /**
     * Set the currently displayed scene on the main window. Sets the width, height, and resizability appropriately.
     *
     * @param scene The scene to switch to
     */
    public static void setScene(TFScene scene) {
        stage.setResizable(true);
        stage.setScene(scenes.get(scene));
        if (scene == TFScene.userWindow || scene == TFScene.clinician || scene == TFScene.transplantList || scene == TFScene.admin) {
            stage.setMinWidth(mainWindowMinWidth);
            stage.setMinHeight(mainWindowMinHeight);
        } else {
            stage.setMinWidth(0);
            stage.setMinHeight(0);
        }
        stage.setWidth(scene.getWidth());
        stage.setHeight(scene.getHeight());

        if (!(scene.getWidth() == mainWindowPrefWidth)) {
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
