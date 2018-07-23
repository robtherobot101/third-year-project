package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.Generic.APIResponse;
import seng302.User.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UsersDAO {
    int getUserId(String username) throws HttpResponseException;

    void insertUser(User user) throws HttpResponseException;

    void updateUser(User user) throws HttpResponseException;

    List<User> queryUsers(Map<String, String> searchMap) throws HttpResponseException;

    User getUser(long id) throws HttpResponseException;

    // Now uses API server!
    Collection<User> getAllUsers() throws HttpResponseException;

    void removeUser(long id) throws HttpResponseException;
}
