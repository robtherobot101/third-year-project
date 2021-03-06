package seng302.data;

import seng302.User.Admin;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResampleData {
    /**
     * the local data to resample
     * @return returns all the users
     */
    public static List<User> users() {
        List<User> allUsers = new ArrayList<>();
        User user1 = new User("Andy", new String[]{"Robert"}, "French", LocalDate.now(), "andy", "andy@andy.com", "ccc1115", "andrew");
        allUsers.add(user1);
        User user2 = new User("Buzz", new String[]{"Buzzy"}, "Knight", LocalDate.now(), "buzz", "buzz@buzz.com", "fak2986", "drowssap");
        allUsers.add(user2);
        User user3 = new User("James", new String[]{"Mozza"}, "Morritt", LocalDate.now(), "mozza", "mozza@mozza.com", "ils1407", "mozza");
        allUsers.add(user3);
        User user4 = new User("Jono", new String[]{"Zilla"}, "Hills", LocalDate.now(), "jonozilla", "zilla@zilla.com", "lsh9538", "zilla");
        allUsers.add(user4);
        User user5 = new User("James", new String[]{"Mackas"}, "Mackay", LocalDate.now(), "mackas", "mackas@mackas.com", "ona0619", "mackas");
        allUsers.add(user5);
        User user6 = new User("Nicky", new String[]{"The Dark Horse"}, "Zohrab-Henricks", LocalDate.now(), "nicky", "nicky@nicky.com", "rdj1490", "nicky");
        allUsers.add(user6);
        User user7 = new User("Kyran", new String[]{"Playing Fortnite"}, "Stagg", LocalDate.now(), "kyran", "kyran@kyran.com", "uqo1661",  "fortnite");
        allUsers.add(user7);
        User user8 = new User("Andrew", new String[]{"Daveo"}, "Davidson", LocalDate.now(), "andrew", "andrew@andrew.com", "xke6962", "andrew");
        allUsers.add(user8);
        for(int i = 0; i < allUsers.size(); i++) {
            allUsers.get(i).setId((long)i+1);
        }
        return allUsers;
    }

    /**
     * adds the default clinician to the local data
     * @return returns the clinicians
     */
    public static List<Clinician> clinicians() {
        return Arrays.asList(new Clinician("default", "default","default clinician",ProfileType.CLINICIAN,1));
    }

    /**
     * adds the default admin to the local data
     * @return returns the admin
     */
    public static List<Admin> admins() {
        Admin defaultAdmin = new Admin("admin", "default","default admin");
        defaultAdmin.setStaffID(1);
        return Arrays.asList(defaultAdmin);
    }
}
