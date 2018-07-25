package seng302.User.Importers;

import java.util.List;

public interface ProfileReader<T> {
    List<T> getProfiles(String path);
}
