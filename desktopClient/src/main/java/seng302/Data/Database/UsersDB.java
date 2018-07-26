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
import seng302.Generic.*;
import seng302.User.User;
import sun.security.ssl.Debug;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.*;

public class UsersDB implements UsersDAO {
    private final APIServer server;

    public UsersDB(APIServer server) {
        this.server = server;
    }

    @Override
    public int getUserId(String username) throws HttpResponseException {
        for (User user : getAllUsers()) {
            if (user.getUsername().equals(username)) {
                return (int) user.getId();
            }
        }
        return -1;
    }

    @Override
    public void insertUser(User user) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        userJson.remove("id");
        APIResponse response = server.postRequest(userJson, new HashMap<String, String>(), "users");
    }

    @Override
    public void updateUser(User user) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        APIResponse response = server.patchRequest(userJson, new HashMap<String, String>(), "users", String.valueOf(user.getId()));
        System.out.println(response.getStatusCode());
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
    public List<User> queryUsers(Map<String, String> searchMap) throws HttpResponseException {
        APIResponse response =  server.getRequest(searchMap, "users");
        if(response.isValidJson()){
            JsonArray searchResults = response.getAsJsonArray();
            Type type = new TypeToken<ArrayList<User>>() {
            }.getType();
            return new Gson().fromJson(searchResults, type);
        } else {
            return new ArrayList<User>();
        }
    }

    @Override
    public User getUser(long id) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "users", String.valueOf(id));
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonObject(), User.class);
        }
        return null;
    }// Now uses API server!

    @Override
    public Image getUserPhoto(long id) {
        APIResponse response = server.getRequest(new HashMap<>(), "users", String.valueOf(id), "photo");

        if (response.getStatusCode() == 404) {
            // No image uploaded, return default image
            Debugger.log("No profile photo loaded: setting default");
            return getDefaultProfilePhoto();
        }

        try {
            Debugger.log(response.getStatusCode());
            String encodedImage = response.toString();
            String base64Image = encodedImage.split(",")[1];
            //Decode the string to a byte array
            byte[] decodedImage = Base64.getDecoder().decode(base64Image);

            //Turn it into a buffered image
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decodedImage);
            BufferedImage bImage = ImageIO.read(byteInputStream);
            byteInputStream.close();
            Image image = SwingFXUtils.toFXImage(bImage, null);
            return image;
        } catch (Exception e) {
            Debugger.error(e);
            System.out.println(IO.getJarPath());
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
            Image profilePhoto = new Image(imageURL);
            return profilePhoto;
        } catch (MalformedURLException e1) {
            return null;
        }
    }

    @Override
    public void updateUserPhoto(long id, String image) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        PhotoStruct photoStruct = new PhotoStruct(image);
        JsonObject imageJson = jp.parse(new Gson().toJson(photoStruct)).getAsJsonObject();
        APIResponse response = server.patchRequest(imageJson, new HashMap<String, String>(), "users", String.valueOf(id), "photo");
    }

    @Override
    public void deleteUserPhoto(long id) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<String, String>(), "users", String.valueOf(id), "photos");
    }

    @Override
    public List<User> getAllUsers() throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "users");
        if (response.isValidJson()) {
            List<User> responses = new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<User>>() {
            }.getType());
            return responses;
        } else {
            return new ArrayList<User>();
        }
    }

    @Override
    public void removeUser(long id) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<String, String>(), "users", String.valueOf(id));
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }
}