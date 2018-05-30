package seng302.ProfileReader.Admins;

import seng302.User.Admin;
import seng302.User.Clinician;

import java.util.List;

public interface AdminReader {
    List<Admin> getProfiles(String path);
}
