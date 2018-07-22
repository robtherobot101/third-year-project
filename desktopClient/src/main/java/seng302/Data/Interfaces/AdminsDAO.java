package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.User.Admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface AdminsDAO {
    void insertAdmin(Admin admin) throws HttpResponseException;

    void updateAdminDetails(Admin admin) throws HttpResponseException;

    Collection<Admin> getAllAdmins() throws HttpResponseException;

    void removeAdmin(long id) throws HttpResponseException;

    Admin getAdmin(long id) throws HttpResponseException;
}
