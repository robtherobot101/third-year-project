package seng302.ProfileReader.Users;

import com.opencsv.CSVReader;
import com.opencsv.bean.AbstractCSVToBean;
import seng302.Generic.Debugger;
import seng302.User.Attribute.Gender;
import seng302.User.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserReaderCSV implements UserReader {

    private CSVReader reader;
    private List<User> readUsers;

    public List<User> getProfiles(String path) {
        Debugger.log("UserReaderCSV: getProfiles called");

        try {
            reader = new CSVReader(new FileReader(path));
        } catch (FileNotFoundException fnfe) {
            Debugger.log(fnfe);
        }
        int entryCount = 0;
        String[] nextLine;
        readUsers = new ArrayList<>();
        try {
            while ((nextLine = reader.readNext()) != null) {
                entryCount++;

                // Parse all single profile details, not all are used
                String NHI = nextLine[0];
                String firstName = nextLine[1];
                String lastNames = nextLine[2];
                String rawDateOfBirth = nextLine[3];
                String rawDateOfDeath = nextLine[4];
                String rawBirthGender = nextLine[5];
                String rawIdentityGender = nextLine[6];
                String bloodType = nextLine[7];
                int height = Integer.parseInt(nextLine[8]);
                int weight = Integer.parseInt(nextLine[9]);
                int streetNumber = Integer.parseInt(nextLine[10]);
                String streetName = nextLine[11];
                String suburb = nextLine[12];
                String city = nextLine[13];
                String region = nextLine[14];
                int zipCode = Integer.parseInt(nextLine[15]);
                String country = nextLine[16];
                String homePhone = nextLine[17];
                String mobilePhone = nextLine[18];
                String email = nextLine[19];

                // Convert raw dates to LocalDates
                DateTimeFormatter csvDateFormat = DateTimeFormatter.ofPattern("MM/DD/yyyy");
                LocalDate dateOfBirth = LocalDate.parse(rawDateOfBirth, csvDateFormat);
                if (rawDateOfDeath.isEmpty()) {
                    Debugger.log(NHI + ": profile is dead");
                    LocalDate dateOfDeath = null;
                } else {
                    LocalDate dateOfDeath = LocalDate.parse(rawDateOfDeath, csvDateFormat);
                }

                // Convert raw genders to enum
                Gender birthGender = Gender.parse(rawBirthGender);
                Gender identityGender = Gender.parse(rawIdentityGender);

                // Finally create the user profile
                User readUser = new User(firstName, null, lastNames, dateOfBirth);
            }
            System.out.println(entryCount + " entries");
        } catch (IOException ioe) {
            Debugger.log(ioe);
        }
        return readUsers;
    }
}
