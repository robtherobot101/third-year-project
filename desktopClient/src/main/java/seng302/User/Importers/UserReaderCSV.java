package seng302.User.Importers;

import com.opencsv.CSVReader;
import seng302.generic.Debugger;
import seng302.User.Attribute.BloodType;
import seng302.User.Attribute.Gender;
import seng302.User.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class UserReaderCSV implements ProfileReader<User> {

    private CSVReader reader;
    private List<User> readUsers;

    /**
     * gets user profiles from a csv file
     * @param path the path to the csv file
     * @return returns the imported profiles
     */
    public List<User> getProfiles(String path) {
        Debugger.log("getProfiles called");


        // Initialise reader and check path
        try {
            reader = new CSVReader(new FileReader(path));
        } catch (FileNotFoundException fnfe) {
            Debugger.error("Invalid file path");
            return null;
        }



        // Check file type is CSV
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }
        assert(extension.equals("csv"));



        int entryCount = 0;
        String[] nextLine;
        readUsers = new ArrayList<>();
        try {
            boolean headerLine = true;
            while ((nextLine = reader.readNext()) != null) {
                if (headerLine) {
                    headerLine = false;
                    continue;
                }
                entryCount++;

                // Parse all single profile details, not all are used
                String NHI = nextLine[0];
                String firstName = nextLine[1];
                String lastNames = nextLine[2];
                String rawDateOfBirth = nextLine[3];
                String rawDateOfDeath = nextLine[4];
                String rawBirthGender = nextLine[5];
                String rawIdentityGender = nextLine[6];
                String rawBloodType = nextLine[7];
                int height = Integer.parseInt(nextLine[8]);
                int weight = Integer.parseInt(nextLine[9]);
                String streetNumber = nextLine[10];
                String streetName = nextLine[11];
                String suburb = nextLine[12];
                String city = nextLine[13];
                String region = nextLine[14];
                int zipCode = Integer.parseInt(nextLine[15]);
                String country = nextLine[16];
                String birth_country = nextLine[17];
                String homePhone = nextLine[18];
                String mobilePhone = nextLine[19];
                String email = nextLine[20];
                String password = "password";

                // Convert raw dates to LocalDates
                LocalDate dateOfBirth = null;
                LocalDateTime dateOfDeath = null;

                // Form a list of possible formats to iterate through
                List<DateTimeFormatter> dateFormats = new ArrayList<>();
                dateFormats.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                dateFormats.add(DateTimeFormatter.ofPattern("M/dd/yyyy"));

                for (DateTimeFormatter df : dateFormats) {
                    try {
                        dateOfBirth = LocalDate.parse(rawDateOfBirth, df);
                    } catch (DateTimeParseException e) {
                        // Do nothing, try another date formatter
                        e.getMessage();
                    }
                }

                if (!rawDateOfDeath.isEmpty()) {
                    for (DateTimeFormatter df : dateFormats) {
                        try {
                            dateOfDeath = LocalDateTime.parse(rawDateOfDeath, df);
                        } catch (DateTimeParseException e) {
                            // Do nothing, try another date formatter
                            e.getMessage();
                        }
                    }
                }

                // Convert raw genders to enum
                Gender birthGender = Gender.parse(rawBirthGender);
                Gender identityGender = Gender.parse(rawIdentityGender);

                // Convert raw blood types to enum
                BloodType bloodType = BloodType.parse(rawBloodType);

                // Process address
                String address = String.format("%s %s, %s", streetNumber, streetName, suburb);

                // Finally create the user profile
                User readUser = new User(firstName, lastNames, dateOfBirth, dateOfDeath,
                        birthGender, identityGender, bloodType, height, weight, address, region, city,
                        zipCode, country, homePhone, mobilePhone, email);
                readUsers.add(readUser);
            }

        } catch (IOException ioe) {
            Debugger.error(ioe);
        }
        return readUsers;
    }
}