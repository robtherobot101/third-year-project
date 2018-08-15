package seng302.User.Importers;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import seng302.generic.Debugger;
import seng302.User.User;
import seng302.User.Importers.JSONParser;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

public class UserReaderJSON implements ProfileReader<User> {

    /**
     * gets user profiles from a json file
     * @param path the path to the json file
     * @return returns the imported profiles
     */
    public List<User> getProfiles(String path) {
        File inputFile = new File(path);

        Path filePath;
        try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            Debugger.error("Invalid file path");
            return null;
        }

        // Check file type is JSON
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }
        assert(extension.equals("json"));

        Type type;
        List<User> importedProfiles = null;

        try (InputStream in = Files.newInputStream(filePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            type = new TypeToken<ArrayList<User>>() {
            }.getType();

            importedProfiles = JSONParser.gson.fromJson(reader, type);
            Debugger.log(String.format("Imported list successfully. (%d users)", importedProfiles.size()));

        } catch (IOException e) {
            Debugger.error("IOException on " + path + ": Check your inputs and permissions!");
        } catch (JsonSyntaxException | DateTimeException e1) {
            Debugger.error("Invalid syntax in input file.");
        } catch (NullPointerException e2) {
            Debugger.error("Input file was empty.");
        }
        return importedProfiles;
    }
}
