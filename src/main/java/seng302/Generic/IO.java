package seng302.Generic;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import seng302.User.Clinician;
import seng302.User.User;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class IO {
    private static long nextUserId = -1, nextClinicianId = -1;
    private static String jarPath, userPath, clinicianPath;
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

    public static String getUserPath() {
        return userPath;
    }

    public static String getJarPath() {
        return jarPath;
    }

    public static String getClinicianPath() {
        return clinicianPath;
    }

    public static void setJarPath(String jarPath) {
        IO.jarPath = jarPath;
    }

    /**
     * Get the unique id number for the next user or the last id number issued.
     *
     * @param increment Whether to increment the unique id counter before returning the unique id value.
     * @param user Whether to increment and return clinician or user. True for user, false for clinician.
     * @return returns either the next unique id number or the last issued id number depending on whether increment
     * was true or false
     */
    public static long getNextId(boolean increment, boolean user) {
        if (increment) {
            if (user) {
                nextUserId++;
            } else {
                nextClinicianId++;
            }
        }
        if (user) {
            return nextUserId;
        } else {
            return nextClinicianId;
        }
    }

    /**
     * Changes the next id to be issued to a new user to be correct for the current users list.
     * @param user Whether to recalculate user or clinician id
     */
    public static void recalculateNextId(boolean user) {
        if (user) {
            nextUserId = -1;
            for (User nextUser : Main.users) {
                if (nextUser.getId() > nextUserId) {
                    nextUserId = nextUser.getId();
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

    public static void setPaths() throws URISyntaxException {
        jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        userPath = jarPath + File.separatorChar + "users.json";
        clinicianPath = jarPath + File.separatorChar + "clinicians.json";
    }
}
