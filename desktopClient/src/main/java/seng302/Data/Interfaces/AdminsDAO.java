package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.User.Admin;

import java.util.ArrayList;

public interface AdminsDAO {
    void insertAdmin(Admin admin) throws HttpResponseException;

    void updateAdminDetails(Admin admin) throws HttpResponseException;

    ArrayList<Admin> getAllAdmins() throws HttpResponseException;

    void removeAdmin(Admin admin) throws HttpResponseException;
}
