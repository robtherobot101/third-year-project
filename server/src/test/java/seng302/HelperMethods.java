package seng302;

import seng302.Logic.Database.GeneralAdmin;
import seng302.Logic.Database.GeneralClinician;
import seng302.Logic.Database.GeneralUser;
import seng302.Model.Admin;
import seng302.Model.Clinician;
import seng302.Model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

public class HelperMethods {
    
    private static Random r = new Random();

    public static User insertUser(GeneralUser generalUser) throws SQLException {
        String[] middle = {"Middle" + r.nextInt(1000)};
        User user = new User("First" + r.nextInt(1000), middle, "Last" + r.nextInt(1000), LocalDate.of(1900 + r.nextInt(1000), 8, 4), "username5" + r.nextInt(1000), "email5@domain.com" + r.nextInt(1000), "password");
        generalUser.insertUser(user);
        user.setId(generalUser.getIdFromUser(user.getUsername()));
        return user;
    }
    
    public static Admin insertAdmin(GeneralAdmin generalAdmin)  throws SQLException {
        Admin admin = new Admin("username" + r.nextInt(1000), "password" + r.nextInt(1000), "Full Name" + r.nextInt(1000));
        generalAdmin.insertAdmin(admin);
        admin.setStaffID(generalAdmin.getAdminIdFromUsername(admin.getUsername()));
        return admin;
    }
    
    public static Clinician insertClinician(GeneralClinician generalClinician) throws SQLException {
        Clinician Clinician = new Clinician("username" + r.nextInt(1000), "password" + r.nextInt(1000), "Full Name" + r.nextInt(1000));
        generalClinician.insertClinician(Clinician);
        Clinician.setStaffID(generalClinician.getClinicianIdFromUsername(Clinician.getUsername()));
        return Clinician;
    }
}
