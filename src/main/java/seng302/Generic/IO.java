package seng302.Generic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.User.Admin;
import seng302.User.Attribute.LoginType;
import seng302.User.Clinician;
import seng302.User.User;

public class IO {

    private static long nextUserId = -1, nextClinicianId = -1, nextAdminId = -1;
    private static String jarPath, userPath, clinicianPath, adminPath;
    public static PrintStream streamOut;

    private static Gson gson = new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).create();

    /**
     * Class to serialize LocalDates without requiring reflective access
     */
    private static class LocalDateSerializer implements JsonSerializer<LocalDate> {

        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(User.dateFormat.format(date));
        }
    }

    /**
     * Class to serialize LocalDateTimes without requiring reflective access
     */
    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(User.dateTimeFormat.format(date));
        }
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
     * get path to the user json file
     *
     * @return path to the user json file
     */
    public static String getUserPath() {
        return userPath;
    }

    /**
     * get the path to the jar file
     *
     * @return path to the jar file
     */
    public static String getJarPath() {
        return jarPath;
    }

    /**
     * get path to the clinician json file
     *
     * @return path to the clinician json file
     */
    public static String getClinicianPath() {
        return clinicianPath;
    }

    /**
     * get path to the admin json file
     *
     * @return path to the admin json file
     */
    public static String getAdminPath() {
        return adminPath;
    }

    /**
     * set the path to the jar file
     *
     * @param jarPath path to the jar file
     */
    public static void setJarPath(String jarPath) {
        IO.jarPath = jarPath;
    }

    /**
     * Get the unique id number for the next user or the last id number issued.
     *
     * @param increment Whether to increment the unique id counter before returning the unique id value.
     * @param type Whether to increment and return clinician, user or admin.
     * @return returns either the next unique id number or the last issued id number depending on whether increment
     * was true or false
     */
    public static long getNextId(boolean increment, LoginType type) {
        recalculateNextId(LoginType.ADMIN);
        recalculateNextId(LoginType.USER);
        recalculateNextId(LoginType.CLINICIAN);
        if (increment) {
            switch (type) {
                case USER:
                    nextUserId++;
                    break;
                case CLINICIAN:
                    nextClinicianId++;
                    break;
                case ADMIN:
                    nextAdminId++;
                    break;
            }
        }
        switch (type) {
            case USER:
                return nextUserId;
            case CLINICIAN:
                return nextClinicianId;
            case ADMIN:
                return nextAdminId;
            default:
                // Unreachable
                return -69;
        }
    }


    /**
     * Changes the next id to be issued to a new user to be correct for the current users list.
     *
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
            case ADMIN:
                nextAdminId = -1;
                for (Admin admin : Main.admins) {
                    if (admin.getStaffID() > nextAdminId) {
                        nextAdminId = admin.getStaffID();
                    }
                }
                break;
        }
    }

    /**
     * Save the user or clinician list to a json file.
     *
     * @param path The path of the file to save to
     * @param loginType the type of user being saved
     * @return Whether the save completed successfully
     */
    public static boolean saveUsers(String path, LoginType loginType) {
        PrintStream outputStream = null;
        File outputFile;
        boolean success;
        try {
            outputFile = new File(path);
            outputStream = new PrintStream(new FileOutputStream(outputFile));
            switch (loginType) {
                case USER:
                    gson.toJson(Main.users, outputStream);
                    break;
                case CLINICIAN:
                    gson.toJson(Main.clinicians, outputStream);
                    break;
                case ADMIN:
                    gson.toJson(Main.admins, outputStream);
                    break;
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
     * @param loginType the account type of the users
     * @return Whether the command executed successfully
     */
    public static boolean importUsers(String path, LoginType loginType) {
        File inputFile = new File(path);
        Path filePath;
        try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            return false;
        }
        Type type;
        try (InputStream in = Files.newInputStream(filePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            switch (loginType) {
                case USER:
                    type = new TypeToken<ArrayList<User>>() {
                    }.getType();
                    ArrayList<User> importedUsers = gson.fromJson(reader, type);
                    System.out.println("Opened file successfully.");
                    Main.users.clear();
                    nextUserId = -1;
                    Main.users.addAll(importedUsers);
                    recalculateNextId(LoginType.USER);
                    System.out.println("Imported list successfully.");
                    return true;
                case CLINICIAN:
                    type = new TypeToken<ArrayList<Clinician>>() {
                    }.getType();
                    ArrayList<Clinician> importedClinicians = gson.fromJson(reader, type);
                    System.out.println("Opened file successfully.");
                    Main.clinicians.clear();
                    nextClinicianId = -1;
                    Main.clinicians.addAll(importedClinicians);
                    recalculateNextId(LoginType.CLINICIAN);
                    System.out.println("Imported list successfully.");
                    return true;
                case ADMIN:
                    type = new TypeToken<ArrayList<Admin>>() {
                    }.getType();
                    ArrayList<Admin> importedAdmins = gson.fromJson(reader, type);
                    System.out.println("Opened file successfully.");
                    Main.admins.clear();
                    nextClinicianId = -1;
                    Main.admins.addAll(importedAdmins);
                    recalculateNextId(LoginType.ADMIN);
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
     * Opens a window to navigate to the CSV file user wants to import.
     *
     * @return The file path of the JSON to import
     */
    public static String getSelectedFilePath() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions =
            new FileChooser.ExtensionFilter(
                "JSON Files", "*.json");

        fileChooser.getExtensionFilters().add(fileExtensions);
        try {
            File file = fileChooser.showOpenDialog(stage);
            return file.getAbsolutePath();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * method to set the paths for the user, clinician and admin files
     *
     * @throws URISyntaxException Not used
     */
    public static void setPaths() throws URISyntaxException {
        jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        userPath = jarPath + File.separatorChar + "users.json";
        clinicianPath = jarPath + File.separatorChar + "clinicians.json";
        adminPath = jarPath + File.separatorChar + "admins.json";
    }
}
