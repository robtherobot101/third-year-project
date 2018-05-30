package seng302.ProfileReader.Users;

import seng302.User.User;

import java.util.List;

public interface UserReader {
    List<User> getProfiles(String path);
}
