package seng302.Core;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

import com.google.gson.reflect.TypeToken;

import java.util.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import seng302.Controllers.*;

import seng302.Files.History;

/**
 * Main class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class Main extends Application {
    private static long nextDonorId = -1, nextClinicianId = -1;
    public static ArrayList<Donor> donors = new ArrayList<>();
    public static ArrayList<Clinician> clinicians = new ArrayList<>();
    public static PrintStream streamOut;
    private static String jarPath, donorPath, clinicianPath;
    private static Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).create();

    private static Stage stage;
    private static HashMap<TFScene, Scene> scenes = new HashMap<>();
    private static ArrayList<Stage> cliniciansDonorWindows = new ArrayList<>();

    private static LoginController loginController;
    private static CreateAccountController createAccountController;
    private static ClinicianController clinicianController;

    private static AccountSettingsController accountSettingsController;
    private static ClinicianAccountSettingsController clinicianAccountSettingsController;
    private static UserWindowController userWindowController;
    private static MedicationsController medicationsController;

    private static String dialogStyle;

    /**
     * Class to serialize LocalDates without requiring reflective access
     */
    private static class LocalDateSerializer implements JsonSerializer<LocalDate> {
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Donor.dateFormat.format(date));
        }
    }

    /**
     * Class to serialize LocalDateTimes without requiring reflective access
     */
    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Donor.dateTimeFormat.format(date));
        }
    }

    /**
     * Class to deserialize LocalDates without requiring reflective access
     */
    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        public LocalDate deserialize(JsonElement date, Type typeOfSrc, JsonDeserializationContext context) {
            return LocalDate.parse(date.toString().replace("\"", ""), Donor.dateFormat);
        }
    }

    /**
     * Class to deserialize LocalDateTimes without requiring reflective access
     */
    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        public LocalDateTime deserialize(JsonElement date, Type typeOfSrc, JsonDeserializationContext context) {
            return LocalDateTime.parse(date.toString().replace("\"", ""), Donor.dateTimeFormat);
        }
    }


    public static void addCliniciansDonorWindow(Stage stage) {cliniciansDonorWindows.add(stage);}

    public static void setClinician(Clinician clinician) {
        clinicianController.setClinician(clinician);
        clinicianController.updateDisplay();
        clinicianController.updateFoundDonors("");
        clinicianController.updatePageButtons();
        clinicianController.displayCurrentPage();
        clinicianController.updateResultsSummary();
    }

    public static void setClinicianController(ClinicianController clinicianController) {
        Main.clinicianController = clinicianController;
    }

    public static void setCurrentDonor(Donor currentDonor) {
        userWindowController.setCurrentDonor(currentDonor);
        userWindowController.populateDonorFields();
        userWindowController.populateHistoryTable();

        medicationsController.setCurrentDonor(currentDonor);
        medicationsController.populateMedications(true);
    }

    /**
     * Sets the medications view to be unable to edit for a donor.
     */
    public static void medicationsViewForDonor() {
        medicationsController.setControlsShown(false);
    }

    /**
     * Sets the medications view to be able to edit for a clinican.
     */
    public static void medicationsViewForClinician() {
        medicationsController.setControlsShown(true);
    }

    public static void setCurrentDonorForAccountSettings(Donor currentDonor) {
        accountSettingsController.setCurrentDonor(currentDonor);
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

    public static void setAccountSettingsController(AccountSettingsController accountSettingsController) {
        Main.accountSettingsController = accountSettingsController;
    }

    public static void setClincianAccountSettingsController(ClinicianAccountSettingsController clincianAccountSettingsController) {
        Main.clinicianAccountSettingsController = clincianAccountSettingsController;
    }

    public static ClinicianController getClinicianController() {
        return Main.clinicianController;
    }

    public static ArrayList<Stage> getCliniciansDonorWindows(){
        return cliniciansDonorWindows;
    }

    public static void setUserWindowController(UserWindowController userWindowController) {
        Main.userWindowController = userWindowController;
    }

    public static String getDonorPath() {
        return donorPath;
    }

    public static String getJarPath() {
        return jarPath;
    }

    public static String getClinicianPath() {
        return clinicianPath;
    }

    public static Stage getStage() {return stage; }

    public static String getDialogStyle() {
        return dialogStyle;
    }

    /**
     * Only called in testing.
     *
     * @param jarPath the jarpath of the app.
     */
    public static void setJarPath(String jarPath) {
        Main.jarPath = jarPath;
    }

    public static void setAccountSettingsEnterEvent() {
        accountSettingsController.setEnterEvent();
    }

    /**
     * Get the unique id number for the next donor or the last id number issued.
     *
     * @param increment Whether to increment the unique id counter before returning the unique id value
     * @return returns either the next unique id number or the last issued id number depending on whether increment
     * was true or false
     */
    public static long getNextId(boolean increment, boolean donor) {
        if (increment) {
            if (donor) {
                nextDonorId++;
            } else {
                nextClinicianId++;
            }
        }
        if (donor) {
            return nextDonorId;
        } else {
            return nextClinicianId;
        }
    }

    /**
     * Find a specific donor from the donor list based on their id.
     *
     * @param id The id of the donor to search for
     * @return The donor object or null if the donor was not found
     */
    public static Donor getDonorById(long id) {
        if (id < 0) {
            return null;
        }
        Donor found = null;
        for (Donor donor : donors) {
            if (donor.getId() == id) {
                found = donor;
                break;
            }
        }
        return found;
    }

    /**
     * Find a specific donor from the donor list based on their name.
     *
     * @param names The names of the donor to search for
     * @return The donor objects that matched the input names
     */
    public static ArrayList<Donor> getDonorByName(String[] names) {
        ArrayList<Donor> found = new ArrayList<>();
        if (names.length == 0) {
            return donors;
        }
        int matched;
        for (Donor donor : donors) {
            matched = 0;
            for (String name : donor.getNameArray()) {
                if (name.toLowerCase().contains(names[matched].toLowerCase())) {
                    matched++;
                    if (matched == names.length) {
                        break;
                    }
                }
            }
            if (matched == names.length) {
                found.add(donor);
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
     * Returns a list of donors matching the given search term.
     * Results are sorted by their score first, and alphabetically second.
     * The search term is broken into tokens, which should be separated by spaces in the term param.
     * If every token matches at least part of the
     * beginning of a one of part of a donors name, that donor will be returned.
     * @param term The search term containing space separated tokens
     * @return An ArrayList of donors sorted by score first, and alphabetically by name second
     */
    public static ArrayList<Donor> getDonorsByNameAlternative(String term){
        String[] t = term.split(" ",-1);
        ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(t));
        if(tokens.contains("")){
            tokens.remove("");
        }
        ArrayList<Donor> matched = new ArrayList<Donor>();
        for(Donor d: donors){
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
        Collections.sort(matched, new Comparator<Donor>() {
            @Override
            public int compare(Donor o1, Donor o2) {
                Integer o1Score = 0;
                for(String name: o1.getNameArray()){
                    for(String token:tokens){
                        if(matches(name, token)){
                            o1Score += lengthMatchedScore(name, token);
                        }
                    }
                }
                Integer o2Score = 0;
                for(String name: o2.getNameArray()){
                    for(String token:tokens) {
                        if(matches(name, token)){
                            o2Score += lengthMatchedScore(name, token);
                        }
                    }
                }
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
     * Save the donor or clinician list to a json file.
     *
     * @param path The path of the file to save to
     * @param donors whether to save the donors or clinicians
     * @return Whether the save completed successfully
     */
    public static boolean saveUsers(String path, boolean donors) {
        PrintStream outputStream = null;
        File outputFile;
        boolean success;
        try {
            outputFile = new File(path);
            outputStream = new PrintStream(new FileOutputStream(outputFile));
            if (donors) {
                gson.toJson(Main.donors, outputStream);
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
     * Imports a JSON object of donor or clinician information and replaces the information in the donor/clinician list.
     *
     * @param path path of the file.
     * @param donors whether the imported file contains donors or clinicians
     * @return Whether the command executed successfully
     */
    public static boolean importUsers(String path, boolean donors) {
        File inputFile = new File(path);
        Path filePath;
        try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            return false;
        }
        Type type;
        try (InputStream in = Files.newInputStream(filePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            if (donors) {
                type = new TypeToken<ArrayList<Donor>>() {}.getType();
                ArrayList<Donor> importedList = gson.fromJson(reader, type);
                System.out.println("Opened file successfully.");
                Main.donors.clear();
                nextDonorId = -1;
                Main.donors.addAll(importedList);
                recalculateNextId(true);
                System.out.println("Imported list successfully.");
                return true;
            } else {
                type = new TypeToken<ArrayList<Clinician>>() {}.getType();
                ArrayList<Clinician> importedList = gson.fromJson(reader, type);
                System.out.println("Opened file successfully.");
                Main.clinicians.clear();
                nextClinicianId = -1;
                Main.clinicians.addAll(importedList);
                recalculateNextId(false);
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
     * Changes the next id to be issued to a new donor to be correct for the current donors list.
     * @param donor Whether to recalculate donor or clinician id
     */
    public static void recalculateNextId(boolean donor) {
        if (donor) {
            nextDonorId = -1;
            for (Donor nextDonor : Main.donors) {
                if (nextDonor.getId() > nextDonorId) {
                    nextDonorId = nextDonor.getId();
                }
            }
        } else {
            nextClinicianId = -1;
            for (Clinician clinician : Main.clinicians) {
                if (clinician.getStaffID() > nextClinicianId) {
                    nextClinicianId = clinician.getStaffID();
                }
            }
        }
    }


    /**
     * Run the GUI.
     *
     * @param args Not used
     */
    public static void main(String[] args) {
        launch(args);
        /*
        try {
            jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
            CommandLineInterface commandLineInterface = new CommandLineInterface();
            commandLineInterface.run();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
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
        if (Platform.isFxApplicationThread()) {
            System.out.println(e);
        } else {
            System.err.println("An unexpected error occurred in "+t);
        }
    }

    /**
     * Load in saved donors and clinicians, and initialise the GUI.
     * @param stage The stage to set the GUI up on
     */
    @Override
    public void start(Stage stage) {
        // This fixes errors which occur in different threads in TestFX
        Thread.setDefaultUncaughtExceptionHandler(Main::showError);

        Main.stage = stage;
        stage.setTitle("Transplant Finder");
        stage.setOnHiding( closeAllWindows -> {
            for(Stage donorWindow:cliniciansDonorWindows){
                donorWindow.close();
            }
        });
        dialogStyle = Main.class.getResource("/css/dialog.css").toExternalForm();
        //stage.getIcons().add(new Image(getClass().getResourceAsStream("/test.png")));
        try {
            jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
            donorPath = jarPath + File.separatorChar + "donors.json";
            clinicianPath = jarPath + File.separatorChar + "clinicians.json";
            File donors = new File(donorPath);
            if (donors.exists()) {
                if (!importUsers(donors.getAbsolutePath(), true)) {
                    throw new IOException("Donor save file could not be loaded.");
                }
            } else {
                if (!donors.createNewFile()) {
                    throw new IOException("Donor save file could not be created.");
                }
            }
            File clinicians = new File(clinicianPath);
            if (clinicians.exists()) {
                if (!importUsers(clinicians.getAbsolutePath(), false)) {
                    throw new IOException("Clinician save file could not be loaded.");
                }
            } else {
                if (!clinicians.createNewFile()) {
                    throw new IOException("Clinician save file could not be created.");
                }
                Clinician defaultClinician = new Clinician("default", "default", "default");
                Main.clinicians.add(defaultClinician);
                Main.saveUsers(clinicianPath, false);
            }
            streamOut = History.init();
            scenes.put(TFScene.login, new Scene(FXMLLoader.load(getClass().getResource("/fxml/login.fxml")), 400, 250));
            loginController.setEnterEvent();
            scenes.put(TFScene.createAccount, new Scene(FXMLLoader.load(getClass().getResource("/fxml/createAccount.fxml")), 400, 415));
            createAccountController.setEnterEvent();
            scenes.put(TFScene.userWindow, new Scene(FXMLLoader.load(getClass().getResource("/fxml/userWindow.fxml")), 900, 575));
            scenes.put(TFScene.clinician, new Scene(FXMLLoader.load(getClass().getResource("/fxml/clinician.fxml")), 800, 600));

            setScene(TFScene.login);
            stage.setResizable(true);
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

    public static Scene getScene(TFScene scene) {
        return scenes.get(scene);
    }

    public static void setScene(TFScene scene) {
        stage.setScene(scenes.get(scene));
        stage.setResizable(scene == TFScene.userWindow || scene == TFScene.clinician);

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
        try{
            String text = History.prepareFileStringGUI(userWindowController.getCurrentDonor().getId(), "quit");
            History.printToFile(Main.streamOut, text);
        } catch(Exception e) {
            System.out.println("Error writing history.");
        }

        System.out.println("Exiting GUI");
        Platform.exit();
    }
}
