package seng302;

import org.junit.Test;
import seng302.ProfileReader.ProfileReader;
import seng302.ProfileReader.UserReaderCSV;
import seng302.User.User;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataImportTest {

    @Test
    public void testValidCSV() {
        ProfileReader<User> userProfileReader = new UserReaderCSV();
        List<User> readUsers = userProfileReader.getProfiles("doc/examples/SENG302_Project_Profiles v3.csv");
        assertEquals(11300, readUsers.size());
    }

    @Test
    public void testInvalidPathCSV() {
        ProfileReader<User> userProfileReader = new UserReaderCSV();
        List<User> readUsers = userProfileReader.getProfiles("doc/exam.csv");
        assertNull(readUsers);
    }
}