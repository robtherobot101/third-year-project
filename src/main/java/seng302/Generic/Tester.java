package seng302.Generic;

import seng302.ProfileReader.Users.UserReader;
import seng302.ProfileReader.Users.UserReaderCSV;
import seng302.ProfileReader.Users.UserReaderJSON;
import seng302.User.User;

import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Just a temporary class to test method calls
 */
public class Tester {
    public static void main(String[] args) {
        UserReader userReaderCSV = new UserReaderCSV();
        UserReader userReaderJSON = new UserReaderJSON();

        List<User> usersList = userReaderJSON.getProfiles("target/users.json");
        /*for (User currentUser: usersList) {
            System.out.println(currentUser);
        }*/
        userReaderCSV.getProfiles("doc/examples/SENG302_Project_Profiles v3.csv");
    }
}
