package seng302.data.interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.User.Admin;

import java.util.Collection;

public interface AdminsDAO {

    void insertAdmin(Admin admin, String token) throws HttpResponseException;

    void updateAdminDetails(Admin admin, String token) throws HttpResponseException;

    void updateAccount(long id, String username, String password, String token) throws HttpResponseException;

    Collection<Admin> getAllAdmins(String token) throws HttpResponseException;

    void removeAdmin(long id, String token) throws HttpResponseException;

    Admin getAdmin(long id, String token) throws HttpResponseException;
}
