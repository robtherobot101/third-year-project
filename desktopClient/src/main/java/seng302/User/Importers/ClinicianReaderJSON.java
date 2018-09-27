package seng302.User.Importers;

import com.google.gson.reflect.TypeToken;
import seng302.User.Clinician;
import seng302.User.User;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClinicianReaderJSON implements ProfileReader<Clinician> {

    /**
     * gets clinician profiles from a json file
     * @param path the path to the json file
     * @return returns the imported profiles
     */
    public List<Clinician> getProfiles(String path) {
        JSONParser<Clinician> parser = new JSONParser<>(new TypeToken<ArrayList<Clinician>>() {}.getType());
        Path filePath = parser.checkPath(path);
        if (filePath == null) {
            return null;
        }
        return parser.readJson(filePath);
    }
}
