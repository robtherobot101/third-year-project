package seng302.ProfileReader.Users;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import seng302.Generic.Debugger;
import seng302.ProfileReader.JSONParser;
import seng302.User.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

public class UserReaderJSON implements UserReader {
    public List<User> getProfiles(String path) {
        File inputFile = new File(path);

        Path filePath;
        try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            System.out.println("Invalid file path.");
            return null;
        }
        Type type;
        List<User> importedUsers = null;

        try (InputStream in = Files.newInputStream(filePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            type = new TypeToken<ArrayList<User>>() {
            }.getType();

            Debugger.log("Opened user file successfully.");
            importedUsers = JSONParser.gson.fromJson(reader, type);
            Debugger.log("Imported list successfully.");

        } catch (IOException e) {
            System.out.println("IOException on " + path + ": Check your inputs and permissions!");
        } catch (JsonSyntaxException | DateTimeException e1) {
            System.out.println("Invalid syntax in input file.");
        } catch (NullPointerException e2) {
            System.out.println("Input file was empty.");
        }
        return importedUsers;
    }
}
