package seng302.ProfileReader;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import seng302.Generic.Debugger;
import seng302.User.Admin;
import seng302.User.Clinician;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

public class AdminReaderJSON implements ProfileReader<Admin> {
    public List<Admin> getProfiles(String path) {
        File inputFile = new File(path);

        Path filePath;
        try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            Debugger.log("Invalid file path");
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
        List<Admin> importedProfiles = null;

        try (InputStream in = Files.newInputStream(filePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            type = new TypeToken<ArrayList<Admin>>() {
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
