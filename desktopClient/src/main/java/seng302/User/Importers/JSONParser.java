package seng302.User.Importers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import seng302.User.Admin;
import seng302.User.User;

import java.io.*;
import java.lang.reflect.Type;
import seng302.generic.Debugger;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JSONParser<T> {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Type type;

    public JSONParser(Type importType) {
        type = importType;
    }

    public Path checkPath(String rawPath) {
        File inputFile = new File(rawPath);

        Path filePath;
        try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            Debugger.log("Invalid file path");
            return null;
        }

        // Check file type is JSON
        String extension = "";
        int i = rawPath.lastIndexOf('.');
        if (i > 0) {
            extension = rawPath.substring(i+1);
        }
        assert(extension.equals("json"));
        return filePath;
    }

    public List<T> readJson(Path filePath) {
        try (InputStream in = Files.newInputStream(filePath); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            List<T> importedProfiles = JSONParser.gson.fromJson(reader, type);
            Debugger.log(String.format("Imported list successfully. (%d profiles)", importedProfiles.size()));
            return importedProfiles;
        } catch (IOException e) {
            Debugger.error("IOException on " + filePath.toString() + ": Check your inputs and permissions!");
        } catch (JsonSyntaxException | DateTimeException e1) {
            Debugger.error("Invalid syntax in input file.");
        } catch (NullPointerException e2) {
            Debugger.error("Input file was empty.");
        }
        return null;
    }
}
