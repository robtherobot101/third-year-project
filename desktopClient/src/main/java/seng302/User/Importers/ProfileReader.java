package seng302.User.Importers;

import java.util.List;

/**
 * Can read in profiles from files of type T.
 * @param <T> The type of user to import
 */
public interface ProfileReader<T> {
    List<T> getProfiles(String path);
}
