package seng302.Generic;

import seng302.Data.Interfaces.AdminsDAO;
import seng302.Data.Interfaces.CliniciansDAO;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.Data.Interfaces.UsersDAO;

public class DataManager {

    private UsersDAO users;
    private CliniciansDAO clinicians;
    private AdminsDAO admins;
    private GeneralDAO general;

    DataManager(UsersDAO users, CliniciansDAO clinicians, AdminsDAO admins, GeneralDAO general) {
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
