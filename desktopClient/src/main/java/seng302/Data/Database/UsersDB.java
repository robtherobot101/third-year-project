package seng302.Data.Database;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.User.Photo;
import seng302.User.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
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
    public Image getUserPhoto(long id) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<String, String>(), "users", String.valueOf(id),"photo");
        //TODO add check here to check if it is a valid base64 string
        Gson gson = new Gson();
        Photo returnedImage = gson.fromJson(response.toString(), Photo.class);

        //Decode the string to a byte array
        byte[] decodedImage = Base64.getDecoder().decode(returnedImage.getData());

        //Turn it into a buffered image
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decodedImage);
        BufferedImage bImage = null;
        try {
            bImage = ImageIO.read(byteInputStream);
            byteInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image image = SwingFXUtils.toFXImage(bImage, null);

        return image;
    }

    @Override
    public void updateUserPhoto(long id, String image) throws HttpResponseException {
        Photo photoToSend = new Photo(image);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedPhoto = gson.toJson(photoToSend);
        JsonParser jp = new JsonParser();
        JsonObject imageJson = jp.parse(serialQueriedPhoto).getAsJsonObject();
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