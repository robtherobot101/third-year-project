package seng302.Generic;

import seng302.ProfileReader.ProfileReader;
import seng302.ProfileReader.UserReaderCSV;
import seng302.User.User;

/**
 * Just a temporary class to test method calls
 */
public class Tester {
    public static void main(String[] args) {
        ProfileReader<User> userReaderCSV = new UserReaderCSV();

        userReaderCSV.getProfiles("doc/examples/SENG302_Project_Profiles v3.csv");
    }
}
