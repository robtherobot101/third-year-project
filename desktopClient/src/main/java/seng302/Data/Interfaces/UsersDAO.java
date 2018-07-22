package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.Generic.APIResponse;
import seng302.User.User;

import java.util.List;
import java.util.Map;

public interface UsersDAO {
    int getUserId(String username) throws HttpResponseException;

    void insertUser(User user) throws HttpResponseException;

    void updateWaitingListItems(User user) throws HttpResponseException;

    //Uses API server for updating attributes
    void updateUserOrgans(User user) throws HttpResponseException;

    void updateUser(User user) throws HttpResponseException;

    void updateUserProcedures(User user) throws HttpResponseException;

    void updateUserDiseases(User user) throws HttpResponseException;

    APIResponse getUsers(Map<String, String> searchMap) throws HttpResponseException;

    User getUserFromId(int id) throws HttpResponseException;

    // Now uses API server!
    List<User> getAllUsers() throws HttpResponseException;

    void removeUser(User user) throws HttpResponseException;
}
