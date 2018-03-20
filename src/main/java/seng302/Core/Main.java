package seng302.Core;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import seng302.Controllers.AccountSettingsController;
import seng302.Controllers.ClinicianController;
import seng302.Controllers.CreateAccountController;
import seng302.Controllers.LoginController;
import seng302.Controllers.UserWindowController;

import seng302.Files.History;

/**
 * Main class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class Main extends Application {
    private static long nextDonorId = -1, nextClinicianId = -1;
    public static ArrayList<Donor> donors = new ArrayList<>();
    // Is there a way to make this accessible in the controllers but not public? Don't like the idea of a public filewriter.
    public static PrintStream streamOut;
    public static ArrayList<Clinician> clinicians = new ArrayList<>();
    private static String jarPath, donorPath, clinicianPath;
    private static ArrayList<Donor> donorUndoStack = new ArrayList<>();
    private static ArrayList<Donor> donorRedoStack = new ArrayList<>();
    private static Stage stage;
    private static HashMap<TFScene, Scene> scenes = new HashMap<>();
    private static HashMap<Stage, Scene> userWindows = new HashMap<>();
    private static LoginController loginController;
    private static CreateAccountController createAccountController;
    private static ClinicianController clinicianController;
    private static AccountSettingsController accountSettingsController;

    public static void addUserWindow(Stage stage, Scene scene) {
        userWindows.put(stage, scene);
    }

    public static void setClinician(Clinician clinician) {
        clinicianController.setClinician(clinician);
    }

    public static void setClinicianController(ClinicianController clinicianController) {
        Main.clinicianController = clinicianController;
    }

    private static UserWindowController userWindowController;

    public static void setCurrentDonor(Donor currentDonor) {
        userWindowController.setCurrentDonor(currentDonor);
        userWindowController.populateDonorFields();
        userWindowController.populateHistoryTable();
    }

    public static void setCurrentDonorForAccountSettings(Donor currentDonor) {
        accountSettingsController.setCurrentDonor(currentDonor);
        accountSettingsController.populateAccountDetails();
    }

    /**
     * Adds a donor object to the donor undo stack. This is called whenever a user saves any changes in the GUI.
     *
     * @param donor donor object being added to the top of the stack.
     */
    public static void addDonorToUndoStack(Donor donor){
        Donor prevDonor = new Donor(donor);
        donorUndoStack.add(prevDonor);
    }

    /**
     * Called when clicking the undo button. Takes the most recent donor object on the stack and returns it.
     * Then removes it from the undo stack and adds it to the redo stack.
     *
     * @return the most recent saved version of the donor.
     */
    public static Donor donorUndo(Donor oldDonor){
        if (donorUndoStack != null){
            Donor newDonor = donorUndoStack.get(donorUndoStack.size()-1);
            donorUndoStack.remove(donorUndoStack.size()-1);
            donorRedoStack.add(oldDonor);
            if (streamOut != null){
//                String text = History.prepareFileStringGUI(oldDonor.getId(), "undo");
//                History.printToFile(streamOut, text);
            }
            return newDonor;
        } else {
            System.out.println("Undo somehow being called with nothing to undo.");
            return null;
        }
    }

    /**
     * A reverse of undo. Can only be called if an action has already been undone, and re loads the donor from the redo stack.
     * @return the donor on top of the redo stack.
     */
    public static Donor donorRedo(Donor newDonor){
        if (donorRedoStack != null){
            Donor oldDonor = donorRedoStack.get(donorRedoStack.size()-1);
            addDonorToUndoStack(newDonor);
            donorRedoStack.remove(donorRedoStack.size()-1);
            if (streamOut != null) {
//                String text = History.prepareFileStringGUI(oldDonor.getId(), "redo");
//                History.printToFile(streamOut, text);
            }
            return oldDonor;
        } else {
            System.out.println("Redo somehow being called with nothing to redo.");
            return null;
        }
    }


    public static ArrayList<Donor> getDonorUndoStack() {
        return donorUndoStack;
    }

    public static ArrayList<Donor> getDonorRedoStack() {
        return donorRedoStack;
    }

    public static void setLoginController(LoginController loginController) {
        Main.loginController = loginController;
    }

    public static void setCreateAccountController(CreateAccountController createAccountController) {
        Main.createAccountController = createAccountController;
    }

    public static void setAccountSettingsController(AccountSettingsController accountSettingsController) {
        Main.accountSettingsController = accountSettingsController;
    }

    public static void setUserWindowController(UserWindowController userWindowController) {
        Main.userWindowController = userWindowController;
    }



    /**
     * Class to serialize LocalDates without requiring reflective access
     */
    private static class LocalDateSerializer implements JsonSerializer<LocalDate> {
        private static ArrayList<Donor> donorUndoStack;

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

    private static Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).create();

    public static String getDonorPath() {
        return donorPath;
    }

    public static String getJarPath() {
        return jarPath;
    }

    public static String getClinicianPath() {
        return clinicianPath;
    }

    /**
     * Only called in testing.
     *
     * @param jarPath the jarpath of the app.
     */
    public static void setJarPath(String jarPath) {
        Main.jarPath = jarPath;
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
     * Save the donor list to a json file.
     *
     * @param path The path of the file to save to
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
     * Imports a JSON object of donor information and replaces the information in the donor list.
     *
     * @param path path of the file.
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
     * Run the command line interface with 4 test donors preloaded.
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

    @Override
    public void start(Stage stage) {
        Main.stage = stage;
        stage.setTitle("Transplant Finder");
        //stage.getIcons().add(new Image(getClass().getResourceAsStream("/test.png")));
        try {
            jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
            donorPath = jarPath + File.separatorChar + "donors.json";
            clinicianPath = jarPath + File.separatorChar + "clinicians.json";
            File donors = new File(donorPath);
            if (donors.exists()) {
                if (!importUsers(donors.getAbsolutePath(), true)) {
                    //throw new IOException("Donor save file could not be loaded.");
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
            scenes.put(TFScene.accountSettings, new Scene(FXMLLoader.load(getClass().getResource("/fxml/accountSettings.fxml")), 270, 350));

            setScene(TFScene.login);
            stage.setResizable(false);
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
        stage.setResizable(scene == TFScene.userWindow);

    }

    @Override
    public void stop() {
        try{
            String text = History.prepareFileStringGUI(userWindowController.getCurrentDonor().getId(), "quit");
            History.printToFile(Main.streamOut, text);
        } catch(Exception e) {
            System.out.println("Oh hello there");
        }

        System.out.println("Exiting GUI");
        Platform.exit();
    }
}
