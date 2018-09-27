package seng302.User.Importers;

import com.google.gson.reflect.TypeToken;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.User;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AdminReaderJSON implements ProfileReader<Admin> {

    /**
     * gets admin profiles from a json file
     * @param path the path to the json file
     * @return returns the imported profiles
     */
    public List<Admin> getProfiles(String path) {
        JSONParser<Admin> parser = new JSONParser<>(new TypeToken<ArrayList<Admin>>() {}.getType());
        Path filePath = parser.checkPath(path);
        if (filePath == null) {
            return null;
        }
        return parser.readJson(filePath);
    }
}
