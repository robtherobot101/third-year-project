package seng302.Generic;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import seng302.Controllers.MedicalHistoryDiseasesController;
import seng302.Controllers.MedicalHistoryProceduresController;
import seng302.GUI.Controllers.*;
import seng302.GUI.TFScene;
import seng302.User.Admin;
import seng302.User.Attribute.LoginType;
import seng302.TUI.CommandLineInterface;
import seng302.User.Clinician;
import seng302.User.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Main class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class Main extends Application {
    public static final int mainWindowMinWidth = 800, mainWindowMinHeight = 600, mainWindowPrefWidth = 1250, mainWindowPrefHeight = 725;
    private static long nextUserId = -1, nextClinicianId = -1, nextAdminId = -1;
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Clinician> clinicians = new ArrayList<>();

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

    /**
     * Class to deserialize LocalDates without requiring reflective access
     */
    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        public LocalDate deserialize(JsonElement date, Type typeOfSrc, JsonDeserializationContext context) {
            return LocalDate.parse(date.toString().replace("\"", ""), User.dateFormat);
        }
    }

    /**
     * Class to deserialize LocalDateTimes without requiring reflective access
     */
    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        public LocalDateTime deserialize(JsonElement date, Type typeOfSrc, JsonDeserializationContext context) {
            return LocalDateTime.parse(date.toString().replace("\"", ""), User.dateTimeFormat);
        }
    }

    /**
     * Sets the medications view to be able to edit for a clinican.
     */
    public static void medicationsViewForClinician() {
        medicationsController.setControlsShown(true);
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

    public static void updateMedications() {
        medicationsController.updateMedications();
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

    /**
     * Sets the medical history diseases view to be unable to edit for a donor.
     */
    public static void medicalHistoryDiseasesViewForDonor() {
        medicalHistoryDiseasesController.setControlsShown(false);
    }

    /**
     * Sets the medical history view diseases to be able to edit for a clinican.
     */
    public static void medicalHistoryDiseasesViewForClinician() {
        medicalHistoryDiseasesController.setControlsShown(true);
    }

    /**
     * Sets the medical history procedures view to be unable to edit for a donor.
     */
    public static void medicalHistoryProceduresViewForDonor() {
        medicalHistoryProceduresController.setControlsShown(false);
    }

    /**
     * Sets the medical history procedures view to be able to edit for a clinican.
     */
    public static void medicalHistoryProceduresViewForClinician() {
        medicalHistoryProceduresController.setControlsShown(true);
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

    public static WaitingListController getWaitingListController(){
        return waitingListController;
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
     * Save the user or clinician list to a json file.
     *
     * @param path The path of the file to save to
     * @param users whether to save the users or clinicians
     * @return Whether the save completed successfully
     */
    public static boolean saveUsers(String path, boolean users) {
        PrintStream outputStream = null;
        File outputFile;
        boolean success;
        try {
            outputFile = new File(path);
            outputStream = new PrintStream(new FileOutputStream(outputFile));
            if (users) {
                gson.toJson(Main.users, outputStream);
            } else {
                gson.toJson(Main.clinicians, outputStream);
            }
            success = true;
        } catch (IOException e) {
            success = false;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return success;
    }

    /**
     * Imports a JSON object of user or clinician information and replaces the information in the user/clinician list.
     *
     * @param path path of the file.
     * @param users whether the imported file contains users or clinicians
     * @return Whether the command executed successfully
     */
    public static boolean importUsers(String path, boolean users) {
        File inputFile = new File(path);
        Path filePath;
        try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            return false;
        }
        Type type;
        try (InputStream in = Files.newInputStream(filePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            if (users) {
                type = new TypeToken<ArrayList<User>>() {}.getType();
                ArrayList<User> importedList = gson.fromJson(reader, type);
                System.out.println("Opened file successfully.");
                Main.users.clear();
                nextUserId = -1;
                Main.users.addAll(importedList);
                recalculateNextId(LoginType.USER);
                System.out.println("Imported list successfully.");
                return true;
            } else {
                type = new TypeToken<ArrayList<Clinician>>() {}.getType();
                ArrayList<Clinician> importedList = gson.fromJson(reader, type);
                System.out.println("Opened file successfully.");
                Main.clinicians.clear();
                nextClinicianId = -1;
                Main.clinicians.addAll(importedList);
                recalculateNextId(LoginType.CLINICIAN);
                System.out.println("Imported list successfully.");
                return true;
            }
        } catch (IOException e) {
            System.out.println("IOException on " + path + ": Check your inputs and permissions!");
        } catch (JsonSyntaxException | DateTimeException e1) {
            System.out.println("Invalid syntax in input file.");
        } catch (NullPointerException e2) {
            System.out.println("Input file was empty.");
            return true;
        }
        return false;
    }

    /**
     * Changes the next id to be issued to a new user to be correct for the current users list.
     * @param type Whether to recalculate user, clinician or admin ID
     */
    public static void recalculateNextId(LoginType type) {
        switch (type) {
            case USER:
                nextUserId = -1;
                for (User nextUser : Main.users) {
                    if (nextUser.getId() > nextUserId) {
                        nextUserId = nextUser.getId();
                    }
                }
                break;
            case CLINICIAN:
                nextClinicianId = -1;
                for (Clinician clinician : Main.clinicians) {
                    if (clinician.getStaffID() > nextClinicianId) {
                        nextClinicianId = clinician.getStaffID();
                    }
                }
                break;
        }

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
        stage = stage;
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
                Admin defaultAdmin = new Admin("admin", "default", "default_admin");
                Main.clinicians.add(defaultClinician);
                Main.clinicians.add(defaultAdmin);
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
            scenes.put(TFScene.admin, new Scene(FXMLLoader.load(getClass().getResource("/fxml/admin.fxml")), 800, 600));

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
        if (scene == TFScene.userWindow || scene == TFScene.clinician || scene == TFScene.transplantList || scene == TFScene.admin) {
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
