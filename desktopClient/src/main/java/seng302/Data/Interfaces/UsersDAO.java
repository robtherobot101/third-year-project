package seng302.Data.Interfaces;

import javafx.scene.image.Image;
import org.apache.http.client.HttpResponseException;
import seng302.User.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UsersDAO {
    int getUserId(String username, String token) throws HttpResponseException;

    void insertUser(User user) throws HttpResponseException;

    void updateUser(User user, String token) throws HttpResponseException;

    List<User> queryUsers(Map<String, String> searchMap, String token) throws HttpResponseException;

    User getUser(long id, String token) throws HttpResponseException;

    Image getUserPhoto(long id, String token);

    void updateUserPhoto(long id, String image) throws HttpResponseException;

    void deleteUserPhoto(long id) throws HttpResponseException;

    // Now uses API server!
    Collection<User> getAllUsers(String token) throws HttpResponseException;

    void removeUser(long id, String token) throws HttpResponseException;

    int count(String token) throws HttpResponseException;
}