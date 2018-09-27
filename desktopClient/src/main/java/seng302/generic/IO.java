package seng302.generic;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import seng302.User.Admin;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.Importers.*;
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
import java.util.concurrent.ThreadLocalRandom;

public class  IO {

    private static String jarPath;

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
     * get the path to the jar file
     *
     * @return path to the jar file
     */
    public static String getJarPath() {
        return jarPath;
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
            Debugger.error(e.getLocalizedMessage());
        }
    }

    /**
     * Imports a JSON object of user or clinician information and replaces the information in the user/clinician list.
     *
     * @param path      path of the file.
     * @param profileType the account type of the users
     * @param token the users token
     * @return Whether the command executed successfully
     */
    public static boolean importProfiles(String path, ProfileType profileType, String token) {
        Debugger.log("importProfile called with profile type: " + profileType);
        switch (profileType) {
            case USER:
                try {
                    ProfileReader<User> userReader = new UserReaderJSON();
                    List<User> readUsers = userReader.getProfiles(path);
                    if (readUsers != null) {
                        for(User u : readUsers) {
                            System.out.println(u.getPassphrase());
                            WindowManager.getDataManager().getUsers().insertUser(u);
                            System.out.println("inserted ");
                        }
                    }
                    return true;
                } catch (HttpResponseException e) {
                    Debugger.error("Failed to import users from Json.");
                }
                break;
            case CLINICIAN:
                try {
                    ProfileReader<Clinician> clinicianReader = new ClinicianReaderJSON();
                    List<Clinician> readClinicians = clinicianReader.getProfiles(path);
                    if (readClinicians != null) {
                        for(Clinician c : readClinicians) {
                            WindowManager.getDataManager().getClinicians().insertClinician(c, token);
                        }
                    }
                    return true;
                } catch (HttpResponseException e) {
                    Debugger.error("Failed to import clinicians from Json.");
                }
                break;
            case ADMIN:
                try {
                    ProfileReader<Admin> adminReader = new AdminReaderJSON();
                    List<Admin> readAdmins = adminReader.getProfiles(path);
                    if (readAdmins != null) {
                        for(Admin a : readAdmins) {
                            WindowManager.getDataManager().getAdmins().insertAdmin(a, token);
                        }
                    }
                    return true;

                } catch (HttpResponseException e) {
                    Debugger.error("Failed to import admins from Json.");
                }
                break;
        }
        return false;
    }

    /**
     * Imports users from csv file
     * @param path the path to the csv file
     */
    public static void importUserCSV(String path) {
        Task taskToRun = runTestThread(path);
        new Thread(taskToRun).start();
    }

    /**
     * Runs a threaded CSV import.
     *
     * @param path The path of the CSV
     * @return The task to run
     */
    private static Task runTestThread(String path) {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                final int max = 4;

                // Start the timer
                long importTimeTaken = System.nanoTime();
                double duration;

                Debugger.log("importUserCSV called");
                ProfileReader<User> userReader = new UserReaderCSV();
                List<User> readUsers = userReader.getProfiles(path);
                updateProgress(1, max);

                // Get time taken to process on client side
                duration = (System.nanoTime() - importTimeTaken) / 1000000000.0;
                Debugger.log("Time taken to process on clientside: " + duration + "s");

                // Check if no users read
                if (readUsers.isEmpty()) {
                    Debugger.log("CSV import, no entries read");
                    this.failed();
                }
                updateProgress(2, max);

                for (User readUser : readUsers) {
                    if (!User.checkNhi(readUser.getNhi())) {
                        readUser.setNhi((char)((int)'c' + 3*ThreadLocalRandom.current().nextInt(0, 8)) + readUser.getNhi().substring(1));
                    }
                }
                try {
                    for (int i = readUsers.size() - 1; i >= 0; i--) {
                        if (!User.checkNhi(readUsers.get(i).getNhi())) {
                            readUsers.remove(i);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Send POST request
                try {
                    WindowManager.getDataManager().getUsers().exportUsers(readUsers);
                } catch (IllegalStateException e) {
                    Debugger.error(e.getStackTrace());
                    this.failed();
                }
                updateProgress(3, max);

                // Get time taken in total (client + POST req)
                duration = (System.nanoTime() - importTimeTaken) / 1000000000.0;
                Debugger.log(readUsers.size() + " entries imported in (total) " + duration + "s");

                updateProgress(4, max);
                this.succeeded();
                return null;
            }
        };

        task.setOnSucceeded(t -> WindowManager.createAlert(Alert.AlertType.INFORMATION,
                "Import CSV", "Import successful", "").show());
        task.setOnCancelled(t -> WindowManager.createAlert(Alert.AlertType.INFORMATION,
                "Import CSV", "Import failure", "").show());
        return task;
    }

    /**
     * Runs a threaded CSV import.
     *
     * @param path The csv to import
     */
    public static void runImportCSVThread(String path) {
        Thread t = new Thread(() -> {
            // Start the timer
            long importTimeTaken = System.nanoTime();
            double duration;

            Debugger.log("importUserCSV called");
            ProfileReader<User> userReader = new UserReaderCSV();
            List<User> readUsers = userReader.getProfiles(path);

            // Get time taken to process on client side
            duration = (System.nanoTime() - importTimeTaken) / 1000000000.0;
            Debugger.log("Time taken to process on clientside: " + duration + "s");

            // Check if no users read
            if (readUsers.isEmpty()) {
                Debugger.log("CSV import, no entries read");
            }

            // Send POST request
            try {
                WindowManager.getDataManager().getUsers().exportUsers(readUsers);
            } catch (IllegalStateException e) {
                Debugger.error(e.getLocalizedMessage());
            }

            // Get time taken in total (client + POST req)
            duration = (System.nanoTime() - importTimeTaken) / 1000000000.0;
            Debugger.log(readUsers.size() + " entries imported in (total) " + duration + "s");
        });

        t.start();
    }

    /**
     * Imports a JSON file and tries to convert it in to a Cache object.
     *
     * @param path The path to the JSON file.
     * @return Returns a Cache object.
     */
    public static Cache importCache(String path){
        File inputFile = new File(path);
        try {
            if (!inputFile.exists() && !inputFile.createNewFile()) {
                throw new IOException();
            }
        } catch (IOException e) {
            Debugger.error("Failed to create file: " + path);
            return null;
        }
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
            Debugger.log("Opened cache file successfully.");
            importedCache.purgeEntriesOlderThan(Duration.ofDays(7));
            return importedCache;
        } catch (IOException e) {
            Debugger.error("IOException on " + path + ": Check your inputs and permissions!");
            Debugger.error(e.getLocalizedMessage());
        } catch (JsonSyntaxException | DateTimeException e1) {
            Debugger.error("Invalid syntax in cache file.");
        } catch (NullPointerException e2) {
            Debugger.log("Cache file was empty.");
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
    }
}