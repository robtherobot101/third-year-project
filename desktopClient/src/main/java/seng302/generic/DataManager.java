package seng302.generic;

import seng302.data.interfaces.AdminsDAO;
import seng302.data.interfaces.CliniciansDAO;
import seng302.data.interfaces.GeneralDAO;
import seng302.data.interfaces.UsersDAO;

public class DataManager {

    private UsersDAO users;
    private CliniciansDAO clinicians;
    private AdminsDAO admins;
    private GeneralDAO general;

    public DataManager(UsersDAO users, CliniciansDAO clinicians, AdminsDAO admins, GeneralDAO general) {
        this.users = users;
        this.clinicians = clinicians;
        this.admins = admins;
        this.general = general;
    }


    public UsersDAO getUsers() {
        return users;
    }

    public CliniciansDAO getClinicians() {
        return clinicians;
    }

    public AdminsDAO getAdmins() {
        return admins;
    }

    public GeneralDAO getGeneral() {
        return general;
    }
}
