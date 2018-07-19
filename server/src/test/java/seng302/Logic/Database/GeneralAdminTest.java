package seng302.Logic.Database;

import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Config.DatabaseConfiguration;
import seng302.Model.Admin;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class GeneralAdminTest {

    private GeneralAdmin generalAdmin = new GeneralAdmin();


    @BeforeClass
    public void reset() throws SQLException {
        Administration administration = new Administration();
        administration.reset();
        administration.resample();
    }

    @Test
    public void getAdminFromResultSet() {
    }

    @Test
    public void insertAdmin() throws SQLException {
        Admin admin = new Admin("username", "password", "Full Name");
        generalAdmin.insertAdmin(admin);
        assertEquals(admin, generalAdmin.getAdminFromId(generalAdmin.getAdminIdFromUsername("username")));
    }

    @Test
    public void getAdminIdFromUsername() {
    }

    @Test
    public void getAdminFromId() {
    }

    @Test
    public void getAllAdmins() {
    }

    @Test
    public void removeAdmin() {
    }

    @Test
    public void updateAdminDetails() {
    }
}