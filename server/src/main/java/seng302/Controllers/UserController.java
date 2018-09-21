package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import seng302.Logic.Database.GeneralUser;
import seng302.Logic.SaltHash;
import seng302.Logic.Database.ProfileUtils;
import seng302.Model.Attribute.Organ;
import seng302.Model.PhotoStruct;
import seng302.Model.User;
import seng302.Model.UserCSVStorer;
import seng302.NotificationManager.PushAPI;
import seng302.Server;
import spark.Request;
import spark.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Integer.max;

public class UserController {
    private GeneralUser model;

    /**
     * Class to handle all user processing eg. adding and editing users
     */
    public UserController() {
        model = new GeneralUser();
    }


    /**
     * method to get all users
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return JSON object containing all users and their information
     */
    public String getUsers(Request request, Response response) {
        Map<String, String> params = new HashMap<String, String>();
        List<String> possibleParams = new ArrayList<String>(Arrays.asList(
                "name","password","userType","age","gender","region","country","organ",
                "startIndex","count"
        ));
        for(String param:possibleParams){
            if(request.queryParams(param) != null){
                params.put(param,request.queryParams(param));
            }
        }
        System.out.println("Params: "+params);
        List<User> queriedUsers;
        try {
            queriedUsers = model.getUsers(params);
            queriedUsers.sort((o1, o2) -> {
                List<String> tokens;
                if(request.queryParams("name")!=null){
                    tokens = Arrays.asList(request.queryParams("name").trim().split(" "));
                }else{
                    tokens = Arrays.asList();
                }
                Integer o1Score = scoreUserOnSearch(o1, tokens);
                Integer o2Score = scoreUserOnSearch(o2, tokens);

                int scoreComparison = o2Score.compareTo(o1Score);
                if (scoreComparison == 0) {
                    return o1.getName().compareTo(o2.getName());
                }
                return scoreComparison;
            });
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedUsers = gson.toJson(queriedUsers);

        response.type("application/json");
        response.status(200);
        return serialQueriedUsers;
    }


    /**
     * Returns the number of entries in the USERS table.
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String the number of users currently registered
     */
    public String countUsers(Request request, Response response) {
        Integer count;
        try {
            count = model.countUsers();
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        response.status(200);
        return count.toString();
    }

    /**
     * Returns a score for a user based on how well their name matches the given search tokens.
     * Every token needs to entirely match all or some of one of the user's names starting at the beginning of each, otherwise
     * the lowest possible score, zero, is returned.
     * <p>
     * For example, the tokens {"abc","def","ghi"} would match a user with the name "adcd defg ghij", so some positive integer
     * would be returned. But for a user with the name "abc def", zero would be returned as one token is unmatched. Likewise,
     * a user with the name "abw d ghi" would score zero because the tokens "abc" and "def" are un-matched.
     * <p>
     * Matches on different parts of a name add different amounts to the total score. Last name matches contribute the most to the total score,
     * followed by, preferred last name, preferred first name, preferred middle names, first name, and middle names in descending order.
     *
     * @param user         The user whose name will be be scored
     * @param searchTokens The search tokens which will be compared with the given user's name
     * @return An integer score
     */
    public static int scoreUserOnSearch(User user, List<String> searchTokens) {
        List<String> tokens = new ArrayList<>(searchTokens);

        if (!allTokensMatched(user, tokens)) {
            return 0;
        }
        int score = 0;
        String[] names = user.getNameArray();
        String[] prefNames = user.getPreferredNameArray();
        // Last name
        score += scoreNames(names, tokens, max(names.length - 1, 1), names.length, 6);

        if (!Arrays.equals(names, prefNames)) {
            // Preferred last name
            score += scoreNames(prefNames, tokens, max(prefNames.length - 1, 1), prefNames.length, 5);

            // Preferred first name
            score += scoreNames(prefNames, tokens, 0, 1, 4);

            // Preferred middle names
            score += scoreNames(prefNames, tokens, 1, prefNames.length - 1, 3);
        }

        //first name
        score += scoreNames(names, tokens, 0, 1, 2);

        //middle names
        score += scoreNames(names, tokens, 1, names.length - 1, 1);

        return score;
    }

    /**
     * Returns true if all given tokens match at least one name from the given user's name array
     * or preferred name array. For a token to match a name, the beginning of each must be the same.
     *
     * @param user         The use whose names will be checked against the tokens
     * @param searchTokens The tokens
     * @return True if all tokens match at least one name, otherwise false
     */
    public static boolean allTokensMatched(User user, List<String> searchTokens) {
        List<String> tokens = new ArrayList<>(searchTokens);
        for (String name : user.getNameArray()) {
            for (String token : new ArrayList<>(tokens)) {
                if (lengthMatchedScore(name, token) > 0) {
                    tokens.remove(token);
                }
            }
        }

        for (String name : user.getPreferredNameArray()) {
            for (String token : new ArrayList<>(tokens)) {
                if (lengthMatchedScore(name, token) > 0) {
                    tokens.remove(token);
                }
            }
        }
        return tokens.isEmpty();
    }

    /**
     * Calculates a score based on the number of names between from and to which match at least one of the given tokens.
     * For each name which matches at least one token, the value of weight is added to the total score.
     * <p>
     * For a token to match a name, both must begin the same and there must be no unmatched characters in the
     * token. For example, the token "dani" would not match the name "dan", but would match "daniel".
     *
     * @param names  The names which the tokens will attempt to match
     * @param tokens The list of tokens which will compared against the names
     * @param from   The index of the first name to try
     * @param to     One less than the last index which will be tried
     * @param weight The weight which will be awarded for each matched name
     * @return An integer score
     */
    public static int scoreNames(String[] names, List<String> tokens, int from, int to, int weight) {
        if (names.length >= to && to > from) {
            String[] middleNames = Arrays.copyOfRange(names, from, to);
            int score = 0;
            for (String middleName : middleNames) {
                if (nameMatchesOneOf(middleName, tokens)) {
                    score += weight;
                }
            }
            return score;
        }
        return 0;
    }

    /**
     * Returns true if at least of of the given tokens matches the given name.
     * For a token to match a name, both must begin the same and there must be no unmatched characters in the
     * token.
     * <p>
     * For example, the token "dani" would not match the name "dan", but would match "daniel"
     *
     * @param name   The name which is compared with each token until a match is found
     * @param tokens The list of tokens to try against the name
     * @return True if a match was found, otherwise false
     */
    public static boolean nameMatchesOneOf(String name, List<String> tokens) {
        for (String token : tokens) {
            if (lengthMatchedScore(name, token) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the length of the longest matched common substring starting from the
     * beginning of string and searchTerm as long as the entire searchTerm is matched. If there
     * are unmatched characters in the searchTerm, zero is returned
     *
     * @param string     The string which is being searched
     * @param searchTerm The term which is being looked for
     * @return The length of the longest substring if the searchTerm is matched entirely
     */
    public static int lengthMatchedScore(String string, String searchTerm) {
        string = string.toLowerCase();
        searchTerm = searchTerm.toLowerCase();
        int i;
        if (searchTerm.length() > string.length()) {
            return 0;
        }
        for (i = 0; i < searchTerm.length(); i++) {
            if (searchTerm.charAt(i) != string.charAt(i)) {
                return 0;
            }
        }
        return i;
    }

    /**
     * Checks for the validity of the request ID, and returns a user obj
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid user object if the user exists otherwise return null
     */
    private User queryUser(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        User queriedUser;
        try {
            queriedUser = model.getUserFromId(requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedUser == null) {
            Server.getInstance().log.warn(String.format("No user of ID: %d found", requestedUserId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedUser;
    }

    /**
     * method to process the add user request. parses the input to be in a better format
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String output whether the request was successful or not
     */
    public String addUser(Request request, Response response) {
        Gson gson = new Gson();
        User receivedUser;

        // Attempt to executeFile received JSON
        try {
            receivedUser = gson.fromJson(request.body(), User.class);
        } catch (JsonSyntaxException jse) {
            Server.getInstance().log.warn(String.format("Malformed JSON:\n%s", request.body()));
            response.status(400);
            return "Bad Request";
        }

        if (receivedUser == null) {
            Server.getInstance().log.warn("Empty request body");
            response.status(400);
            return "Missing user Body";
        } else {
            if (checkNhi(receivedUser.getNhi())) {
                try {
                    receivedUser.setPassword(SaltHash.createHash(receivedUser.getPassword()));
                    model.insertUser(receivedUser);
                    response.status(201);
                    return "Success";
                } catch (SQLException e) {
                    Server.getInstance().log.error(e.getMessage());
                    response.status(500);
                    return "Internal Server Error";
                }
            } else {
                Server.getInstance().log.error("Invalid nhi in user update: " + receivedUser.getNhi());
                response.status(400);
                return "Malformed NHI supplied";
            }
        }
    }

    /**
     * method to process the add user request. parses the input to be in a better format
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String output whether the request was successful or not
     */
    public String importUsers(Request request, Response response) {
        Gson gson = new Gson();
        List<User> receivedUsers;
        System.out.println("IMPORTING USERS");
        // Attempt to executeFile received JSON
        try {
             receivedUsers = gson.fromJson(request.body(), UserCSVStorer.class).getUsers();
        } catch (JsonSyntaxException jse) {
            Server.getInstance().log.warn(String.format("Malformed JSON:\n%s", request.body()));
            response.status(400);
            return "Bad Request";
        }
        System.out.println("Got " + receivedUsers.size() + " entries");
        if (receivedUsers.size() > 0) {
            for (User user: receivedUsers) {
                if (!checkNhi(user.getNhi())) {
                    Server.getInstance().log.error("Invalid nhi in imported user: " + user.getNhi());
                    response.status(400);
                    return "Malformed NHI supplied";
                }
            }
        }
        try {
            model.importUsers(receivedUsers);
        } catch (SQLException e) {
            e.printStackTrace();
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return "Internal Server Error";
        }
        response.status(200);
        return "OK";
    }

    /**
     * method to handle the getting of a specific user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return JSON object containing the requested user information
     */
    public String getUser(Request request, Response response) {
        User queriedUser = queryUser(request, response);

        if (queriedUser == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedUser = gson.toJson(queriedUser);

        response.type("application/json");
        response.status(200);
        return serialQueriedUser;
    }

    /**
     * method to process the editing of a specific user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the editing of the user was successful or not
     */
    public String editUser(Request request, Response response) {
        User queriedUser = queryUser(request, response);

        if (queriedUser == null) {
            return response.body();
        }

        Gson gson = new Gson();
        User receivedUser = gson.fromJson(request.body(), User.class);
        if (receivedUser == null) {
            response.status(400);
            return "Missing user Body";
        } else {
            if (checkNhi(receivedUser.getNhi())) {
                try {
                    String token = request.headers("token");
                    ProfileUtils profileUtils = new ProfileUtils();
                    int accessLevel = profileUtils.checkToken(token);
                    receivedUser.setPassword(SaltHash.createHash(receivedUser.getPassword()));
                    model.patchEntireUser(receivedUser, Integer.parseInt(request.params(":id")), accessLevel >= 1); //this version patches all user information
                    response.status(201);
                    PushAPI.getInstance().sendTextNotification((int)queriedUser.getId(), "Details updated.", "Your details have been updated.");
                    return "USER SUCCESSFULLY UPDATED";
                } catch (SQLException e) {

                    e.printStackTrace();
                    Server.getInstance().log.error(e.getMessage());
                    response.status(500);
                    return "Internal Server Error";
                }
            } else {
                Server.getInstance().log.error("Invalid nhi in user update: " + receivedUser.getNhi());
                response.status(400);
                return "Malformed NHI supplied";
            }
        }
    }

    /**
     * method to handle the deletion of a specific user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether or not the user was deleted successfully
     */
    public String deleteUser(Request request, Response response) {
        User queriedUser = queryUser(request, response);

        if (queriedUser == null) {
            return response.body();
        }

        try {
            model.removeUser(queriedUser);
            response.status(201);
            return "USER DELETED";
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return "Internal Server Error";
        }
    }


    public String getUserPhoto(Request request, Response response) throws URISyntaxException {
        User queriedUser = queryUser(request, response);

        if (queriedUser == null){
            return response.body();
        }

        String path = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        String filepath = path + "/home/serverImages/user/" + queriedUser.getId() + ".png";

        File file = new File(filepath);
        if (!file.isFile()){
            response.status(404);
            return "Photo does not exist.";
        }


        // BufferedImage has no type, we can choose what the file type is
        try {
            BufferedImage bImage = ImageIO.read(file);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", byteOutputStream);
            byte[] byteArrayImage = byteOutputStream.toByteArray();
            byteOutputStream.close();
            String image = Base64.getEncoder().encodeToString(byteArrayImage);
            response.status(200);
            return image;
        } catch (IOException e) {
            return "Internal Server Error";
        }




    }

    public String editUserPhoto(Request request, Response response){
        User queriedUser = queryUser(request, response);

        if (queriedUser == null){
            return response.body();
        }

        Gson gson = new Gson();
        PhotoStruct receivedPhotoStruct;

        // Attempt to executeFile received JSON into our simple photo structure
        try {
            receivedPhotoStruct = gson.fromJson(request.body(), PhotoStruct.class);
        } catch (JsonSyntaxException jse) {
            Server.getInstance().log.warn(String.format("Malformed JSON:\n%s", request.body()));
            response.status(400);
            return "Bad Request";
        }

        // Process this base64 string into a photo
        String encodedImage = receivedPhotoStruct.getData();
        if (encodedImage == null) {
            response.status(400);
            return "Missing Image";
        } else {
            try {
                //Decode the string to a byte array
                byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(encodedImage);

                // BufferedImage has no type, we can choose what the file type is
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

                // Ensure directory exists
                String path = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
                Files.createDirectories(Paths.get(path + "/home/serverImages/user/"));

                // Set filepath
                String filepath = path + "/home/serverImages/user/" + queriedUser.getId() + ".png";

                // Write the file
                File outputfile = new File(filepath);
                ImageIO.write(img, "png", outputfile);

                PushAPI.getInstance().sendTextNotification((int)queriedUser.getId(), "Profile photo changed.",
                        "Your profile photo has been changed.");
                return "PHOTO SUCCESSFULLY SAVED";
            }  catch (IOException e) {
                System.out.println(e);
                return "Internal Server Error";
            } catch (URISyntaxException el) {
                el.printStackTrace();
                return "URI Error";
            }
        }
    }


    public String deleteUserPhoto(Request request, Response response) throws URISyntaxException {
        User queriedUser = queryUser(request, response);
        if (queriedUser == null){
            return response.body();
        }

        //Find filepath in DB
        String path = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        String filepath = path + "/home/serverImages/user/" + queriedUser.getId() + ".png";

        //Delete file from storage
        File file = new File(filepath);
        boolean deleted = false;
        if (file.exists()){
            deleted = file.delete();
        }

        //Update DB
        if (deleted){
            //update
            PushAPI.getInstance().sendTextNotification((int)queriedUser.getId(), "Profile photo deleted.",
                    "Your profile photo has been deleted.");
            return "Photo successfully deleted";
        } else {
            //update
            return "Internal server error";
        }
    }

    /**
     * Checks an NHI is of the correct format:
     *      Starts with 3rd, 6th, 9th... letter of the alphabet
     *      Is of the format cde1234
     * @param nhi A National Health Index number
     */
    private boolean checkNhi(String nhi){
        nhi = nhi.toLowerCase();
        // Character 'c' has ASCII value 99
        if ((int) nhi.charAt(0) % 3 == 0) {
            return Pattern.matches("[a-z][a-z][a-z]\\d\\d\\d\\d", nhi);
        }
        else return false;
    }

}
