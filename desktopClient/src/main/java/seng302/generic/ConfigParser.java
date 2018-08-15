package seng302.generic;

import javafx.scene.control.Alert;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Creates or loads a config file into a Properties object
 */
public class ConfigParser {

    /**
     * The filename of the configuration file
     */
    private static final String filename = "client_config.txt";

    /**
     * The required parameters for the config file.
     */
    private static final String[] parameters = {"server"};

    /**
     * A map containg the proprties
     */
    private Properties properties = new Properties();

    public ConfigParser() {
        File file = new File(filename);
        try {
            // If the config file does not exist, create one with the default config.
            if (!file.exists()) {
                Files.copy(getClass().getResourceAsStream("/" + filename), file.toPath());
            }
            properties.load(new FileInputStream(file));
            // Recreate the config file if the current one is corrupt
            for (String parameter : parameters) {
                if (!properties.containsKey(parameter)) {
                    Debugger.log("Invalid config file, backing up and reverting to default");
                    File backup = new File(filename + ".bak");
                    backup.delete();
                    Files.copy(file.toPath(), backup.toPath());
                    file.delete();
                    Files.copy(getClass().getResourceAsStream("/" + filename), file.toPath());
                    break;
                }
            }
            properties.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Could not find or create config file");
            a.showAndWait();
            Debugger.error(e.getMessage());
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "An error occurred while loading configuration file.");
            a.showAndWait();
            Debugger.error(e.getMessage());
        }

    }

    /**
     * Get an unmodifiable map of the properties provided
     * @return An unmodifiable Map of properties
     */
    public Map<Object, Object> getConfig(){
        return Collections.unmodifiableMap(properties);
    }

}
