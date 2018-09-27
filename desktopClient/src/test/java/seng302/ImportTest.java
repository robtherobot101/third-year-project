package seng302;

import org.junit.Test;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.Importers.*;
import seng302.User.User;
import seng302.User.UserCSVStorer;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ImportTest {
    @Test
    public void testCSVImport() {
        ProfileReader<User> reader = new UserReaderCSV();
        UserCSVStorer userCSVStorer = new UserCSVStorer(reader.getProfiles(Paths.get("doc/examples/small.csv").toAbsolutePath().toString()));
        assertEquals(129, userCSVStorer.getUsers().size());
    }

    @Test
    public void testJSONClinicianImport() {
        ProfileReader<Clinician> reader = new ClinicianReaderJSON();
        List<Clinician> clinicians = reader.getProfiles(Paths.get("doc/examples/testClinician.json").toAbsolutePath().toString());
        assertEquals(1, clinicians.size());
    }

    @Test
    public void testJSONUserImport() {
        ProfileReader<User> reader = new UserReaderJSON();
        List<User> users = reader.getProfiles(Paths.get("doc/examples/testUsers.json").toAbsolutePath().toString());
        assertEquals(5, users.size());
    }

    @Test
    public void testJSONAdminImport() {
        ProfileReader<Admin> reader = new AdminReaderJSON();
        List<Admin> admins = reader.getProfiles(Paths.get("doc/examples/testAdmin.json").toAbsolutePath().toString());
        assertEquals(1, admins.size());
    }
}
