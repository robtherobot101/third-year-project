package seng302.Data.Database;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.Generic.Debugger;
import seng302.Generic.IO;
import seng302.User.User;
import seng302.User.UserCSVStorer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.*;

public class UsersDB implements UsersDAO {
    private final APIServer server;
    private String users = "users";
    private String photo = "photo";
    private String couldNotConnect = "Could not access server";

    public UsersDB(APIServer server) {
        this.server = server;
    }

    /**
     * gets a users id from the username
     * @param username the username of the user
     * @param token the users token
     * @return returns the id of the user
     */
    @Override
    public int getUserId(String username, String token) {
        for (User user : getAllUsers(token)) {
            if (user.getUsername().equals(username)) {
                return (int) user.getId();
            }
        }
        return -1;
    }

    /**
     * inserts a new user into the server
     * @param user the user to insert
     */
    @Override
    public void insertUser(User user) {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        userJson.remove("id");
        server.postRequest(userJson, new HashMap<>(), null, users);
    }

    @Override
    public void exportUsers(List<User> usersToSend) {
        // Serialise read user list to JSON string
        JsonParser jp = new JsonParser();
        UserCSVStorer csvUsers = new UserCSVStorer(usersToSend);
        JsonObject usersJson = jp.parse(new Gson().toJson(csvUsers)).getAsJsonObject();
        server.postRequest(usersJson, new HashMap<>(), "masterToken", "users/import");
    }

    /**
     * updates a user on the database
     * @param user the user to update
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public void updateUser(User user, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        APIResponse response = server.patchRequest(userJson, new HashMap<>(), token, users, String.valueOf(user.getId()));
        if(response == null) throw new HttpResponseException(0, couldNotConnect);
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * Used for searching, takes a hashmap of keyvalue pairs and searches the DB for them.
     * eg. "age", "10" returns all users aged 10.
     *
     * @param searchMap The hashmap with associated key value pairs
     * @return a JSON array of users.
     */
    @Override
    public List<User> queryUsers(Map<String, String> searchMap, String token) {
        APIResponse response =  server.getRequest(searchMap, token, users);
        if(response == null){
            return new ArrayList<>();
        }
        if(response.isValidJson()){
            JsonArray searchResults = response.getAsJsonArray();
            Type type = new TypeToken<ArrayList<User>>() {
            }.getType();
            return new Gson().fromJson(searchResults, type);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * gets a specific user from the server
     * @param id the id of the user to get
     * @param token the users token
     * @return returns a user
     */
    @Override
    public User getUser(long id, String token) {
        APIResponse response = server.getRequest(new HashMap<>(), token, users, String.valueOf(id));
        if(response == null){
            return null;
        }
        else if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonObject(), User.class);
        }
        else return null;
    }

    @Override
    public Image getUserPhoto(long id, String token) {
        APIResponse response = server.getRequest(new HashMap<>(), token, users, String.valueOf(id), photo);

        if(response == null) return getDefaultProfilePhoto();

        if (response.getStatusCode() == 404) {
            // No image uploaded, return default image
            Debugger.log("No profile photo loaded: setting default");
            return getDefaultProfilePhoto();
        }
        try {
            Debugger.log(response.getStatusCode());
            String encodedImage = response.getAsString();
            //Decode the string to a byte array
            byte[] decodedImage = Base64.getDecoder().decode(encodedImage);

            //Turn it into a buffered image
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decodedImage);
            BufferedImage bImage = ImageIO.read(byteInputStream);
            byteInputStream.close();
            return SwingFXUtils.toFXImage(bImage, null);
        } catch (Exception e) {
            Debugger.error(e);
            return getDefaultProfilePhoto();
        }
    }

    /**
     * Helper function that returns a default placeholder image for the profile photo
     * @return placeholder image
     */
    private Image getDefaultProfilePhoto() {
        File imageFile = new File(IO.getJarPath() + "/classes/icon.png");
        try {
            String imageURL = imageFile.toURI().toURL().toString();
            return new Image(imageURL);
        } catch (MalformedURLException e1) {
            return null;
        }
    }

    @Override
    public void updateUserPhoto(long id, String image) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        PhotoStruct photoStruct = new PhotoStruct(image);
        JsonObject imageJson = jp.parse(new Gson().toJson(photoStruct)).getAsJsonObject();
        APIResponse response = server.patchRequest(imageJson, new HashMap<>(), users, users, String.valueOf(id), photo);
        if(response == null) throw new HttpResponseException(0, couldNotConnect);
    }

    @Override
    public void deleteUserPhoto(long id) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), users, users, String.valueOf(id), photo);
        if(response == null) throw new HttpResponseException(0, couldNotConnect);
    }

    /**
     * get all the users from the server
     * @param token the users token
     * @return returns a list of all users
     */
    @Override
    public List<User> getAllUsers(String token) {
        APIResponse response = server.getRequest(new HashMap<>(), token, users);
        if(response == null){
            return new ArrayList<>();
        }
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<User>>(){}.getType());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * removes a user from the server
     * @param id the id of the users to remove
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public void removeUser(long id, String token) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), token, users, String.valueOf(id));
        if(response == null) throw new HttpResponseException(0, couldNotConnect);
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * gets the total number of users
     * @param token the users token
     * @return returns the number of users
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public int count(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "usercount");
        if(response == null){
            throw new HttpResponseException(0, "Could not reach server");
        }
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        return Integer.parseInt(response.getAsString());
    }
}