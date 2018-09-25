package seng302.User.Importers;

import seng302.User.User;

import java.nio.file.Path;
import java.util.List;

public class UserReaderJSON implements ProfileReader<User> {

    /**
     * gets user profiles from a json file
     * @param path the path to the json file
     * @return returns the imported profiles
     */
    public List<User> getProfiles(String path) {
        Path filePath = JSONParser.checkPath(path);
        if (filePath == null) {
            return null;
        }
        return JSONParser.readJson(filePath);
    }
}
