package seng302.Generic;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.GUI.Controllers.ClinicianController;
import seng302.ProfileReader.*;
import seng302.User.Admin;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.User;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IO {

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
     * Save the user or clinician list to a json file.
     *
     * @param path      The path of the file to save to
     * @param loginType the type of user being saved
     * @return Whether the save completed successfully
     */
    public static boolean saveUsers(String path, ProfileType loginType) {
        PrintStream outputStream = null;
        File outputFile;
        boolean success;
        try {
            outputFile = new File(path);
            outputStream = new PrintStream(new FileOutputStream(outputFile));
            switch (loginType) {
                case USER:
                    gson.toJson(DataManager.users, outputStream);
                    break;
                case CLINICIAN:
                    gson.toJson(DataManager.clinicians, outputStream);
                    break;
                case ADMIN:
                    gson.toJson(DataManager.admins, outputStream);
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
     * Saves a Cache object to the file path inside the Cache Object.
     *
     * @param cache The Cache object to be saved.
     */
    public static void saveCache(Cache cache){
        File outputFile = new File(cache.getFilePath());
        try{
            PrintStream outputStream = new PrintStream(new FileOutputStream(outputFile));
            gson.toJson(cache, outputStream);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports a JSON object of user or clinician information and replaces the information in the user/clinician list.
     *
     * @param path      path of the file.
     * @param profileType the account type of the users
     * @return Whether the command executed successfully
     */
    public static boolean importProfiles(String path, ProfileType profileType) {
        Debugger.log("importProfile called with profile type: " + profileType);
        switch (profileType) {
            case USER:
                ProfileReader<User> userReader = new UserReaderJSON();
                List<User> readUsers = userReader.getProfiles(path);
                if (readUsers != null) {
                    DataManager.users.addAll(readUsers);
                }
                return true;
            case CLINICIAN:
                ProfileReader<Clinician> clinicianReader = new ClinicianReaderJSON();
                List<Clinician> readClinicians = clinicianReader.getProfiles(path);
                if (readClinicians != null) {
                    DataManager.clinicians.addAll(readClinicians);
                }
                return true;
            case ADMIN:
                ProfileReader<Admin> adminReader = new AdminReaderJSON();
                List<Admin> readAdmins = adminReader.getProfiles(path);
                if (readAdmins != null) {
                    DataManager.admins.addAll(readAdmins);
                }
                return true;
        }
        return false;
    }

    public static boolean importUserCSV(String path) {
        Debugger.log("importUserCSV called");
        ProfileReader<User> userReader = new UserReaderCSV();
        List<User> readUsers = userReader.getProfiles(path);
        if (readUsers != null) {
            DataManager.users.addAll(readUsers);
        }
        return true;
    }

    /**
     * Imports a JSON file and tries to convert it in to a Cache object.
     *
     * @param path The path to the JSON file.
     * @return Returns a Cache object.
     */
    public static Cache importCache(String path){
        File inputFile = new File(path);
        Path filePath;
        try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            return new Cache(path);
        }
        try (InputStream in = Files.newInputStream(filePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Type type = new TypeToken<Cache>() {
            }.getType();
            Cache importedCache = gson.fromJson(reader, type);
            Debugger.log("Opened user file successfully.");
            importedCache.purgeEntriesOlderThan(Duration.ofDays(7));
            return importedCache;
        } catch (IOException e) {
            Debugger.error("IOException on " + path + ": Check your inputs and permissions!");
        } catch (JsonSyntaxException | DateTimeException e1) {
            Debugger.error("Invalid syntax in input file.");
        } catch (NullPointerException e2) {
            Debugger.error("Input file was empty.");
        }
        return new Cache(path);
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
        jarPath = new File(WindowManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        userPath = jarPath + File.separatorChar + "users.json";
        clinicianPath = jarPath + File.separatorChar + "clinicians.json";
        adminPath = jarPath + File.separatorChar + "admins.json";
    }
}