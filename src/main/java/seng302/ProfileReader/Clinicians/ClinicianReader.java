package seng302.ProfileReader.Clinicians;

import seng302.User.Clinician;

import java.util.List;

public interface ClinicianReader {
    List<Clinician> getProfiles(String path);
}
