package seng302.User.Importers;

import seng302.User.Clinician;

import java.nio.file.Path;
import java.util.List;

public class ClinicianReaderJSON implements ProfileReader<Clinician> {

    /**
     * gets clinician profiles from a json file
     * @param path the path to the json file
     * @return returns the imported profiles
     */
    public List<Clinician> getProfiles(String path) {
        Path filePath = JSONParser.checkPath(path);
        if (filePath == null) {
            return null;
        }
        return JSONParser.readJson(filePath);
    }
}
