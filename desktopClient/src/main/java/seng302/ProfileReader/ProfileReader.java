package seng302.ProfileReader;

import java.util.List;

public interface ProfileReader<T> {
    List<T> getProfiles(String path);
}
