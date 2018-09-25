package seng302.User.Importers;

import seng302.User.Admin;

import java.nio.file.Path;
import java.util.List;

public class AdminReaderJSON implements ProfileReader<Admin> {

    /**
     * gets admin profiles from a json file
     * @param path the path to the json file
     * @return returns the imported profiles
     */
    public List<Admin> getProfiles(String path) {
        Path filePath = JSONParser.checkPath(path);
        if (filePath == null) {
            return null;
        }
        return JSONParser.readJson(filePath);
    }
}
