package seng302.data.interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.User.Admin;

import java.util.Collection;

public interface AdminsDAO {

    /**
     * inserts an admin into the database
     * @param admin Admin the admin to insert
     * @param token String the access token to use
     * @throws HttpResponseException catch connection errors
     */
    void insertAdmin(Admin admin, String token) throws HttpResponseException;

    /**
     * updates an admins details on the database
     * @param admin Admin the admin to insert
     * @param token String the access token to use
     * @throws HttpResponseException catch connection errors
     */
    void updateAdminDetails(Admin admin, String token) throws HttpResponseException;

    /**
     * update admin account with given details
     * @param id the admin id
     * @param username the new username
     * @param password the new password
     * @param token the access token
     * @throws HttpResponseException catch connection errors
     */
    void updateAccount(long id, String username, String password, String token) throws HttpResponseException;

    /**
     * get all the admins currently in the database
     * @param token the access token
     * @return Collection the admins in the database as Admin objects
     * @throws HttpResponseException catch connection errors
     */
    Collection<Admin> getAllAdmins(String token) throws HttpResponseException;

    /**
     * method to remove an admin from the database
     * @param id the id of the admin
     * @param token the access token
     * @throws HttpResponseException catch connection errors
     */
    void removeAdmin(long id, String token) throws HttpResponseException;

    /**
     * method to get a specific admin from the database
     * @param id the id of the admin
     * @param token the access token
     * @return Admin the specific admin requested
     * @throws HttpResponseException catch connection errors
     */
    Admin getAdmin(long id, String token) throws HttpResponseException;
}
