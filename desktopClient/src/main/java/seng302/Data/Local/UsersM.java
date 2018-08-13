package seng302.Data.Local;

import javafx.scene.image.Image;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Generic.Debugger;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.util.*;

import static java.lang.Integer.max;

public class UsersM implements UsersDAO {

    private List<User> users;

    public UsersM() {
        this.users = new ArrayList<>();
    }

    @Override
    public int getUserId(String username, String token) {
        return 0;
    }

    /**
     * inserts a new user
     * @param user the user to insert
     */
    @Override
    public void insertUser(User user) {
        long nextUserId = 0;
        for (User u : users) {
            if (u.getId() > nextUserId) {
                nextUserId = u.getId();
            }
        }
        user.setId(nextUserId + 1);
        users.add(user);
    }

    @Override
    public void exportUsers(List<User> userList) {
        // TODO I have no idea how this works help - Mr Buzz
        Debugger.error("Not implemented");
    }

    /**
     * updates a user
     * @param User the user to update
     * @param token the users token
     */
    @Override
    public void updateUser(User User, String token) {
        removeUser(User.getId(), null);
        users.add(User);
    }


    /**
     * Searches through all the users using the given search term and returns the results which match.
     * The search term is broken into tokens separated by spaces. Each token must match at least one
     * part of the user's given or preferred names.
     * <p>
     * For a token to match a name, both must begin the same and there must be no unmatched characters in the
     * token. For example, the token "dani" would not match the name "dan", but would match "daniel".
     * <p>
     * The results are returned sorted by a score according to which names were matched.
     * See scoreUserOnSearch(User, List(String))
     * If two users are ranked the same, they're sorted alphabetically
     *
     * @param term The search term which will be broken into space separated tokens
     * @return A sorted list of results
     */
    public ArrayList<User> getUsersByNameAlternative(String term) {
        if (term.equals("")) {
            ArrayList<User> sorted = new ArrayList<>(users);
            sorted.sort(Comparator.comparing(User::getName));
            return sorted;
        }
        String[] t = term.split(" ", -1);
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(t));
        if (tokens.contains("")) {
            tokens.remove("");
        }
        ArrayList<User> matched;
        matched = new ArrayList<>();
        for (User user : users) {
            if (scoreUserOnSearch(user, tokens) > 0) {
                matched.add(user);
            }
        }
        matched.sort((o1, o2) -> {
            Integer o1Score = scoreUserOnSearch(o1, tokens);
            Integer o2Score = scoreUserOnSearch(o2, tokens);

            int scoreComparison = o1Score.compareTo(o2Score);
            if (scoreComparison == 0) {
                return o1.getName().compareTo(o2.getName());
            }

            System.out.println("{");
            System.out.println("Name:" + o1.getName() + ", score: " + o1Score);
            System.out.println("Name:" + o2.getName() + ", score: " + o2Score);
            System.out.println("Score comparison: " + scoreComparison);
            System.out.println("}");

            return scoreComparison;
        });
        System.out.println(matched.get(0).getName());

        Collections.reverse(matched);
        System.out.println(matched.get(0).getName());
        return matched;
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
    public int scoreUserOnSearch(User user, List<String> searchTokens) {
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
    public int scoreNames(String[] names, List<String> tokens, int from, int to, int weight) {
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
    public boolean nameMatchesOneOf(String name, List<String> tokens) {
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
    public int lengthMatchedScore(String string, String searchTerm) {
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
     * Returns true if all given tokens match at least one name from the given user's name array
     * or preferred name array. For a token to match a name, the beginning of each must be the same.
     *
     * @param user         The use whose names will be checked against the tokens
     * @param searchTokens The tokens
     * @return True if all tokens match at least one name, otherwise false
     */
    public boolean allTokensMatched(User user, List<String> searchTokens) {
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
     * Used for searching, takes a hashmap of keyvalue pairs and searches the DB for them.
     * eg. "age", "10" returns all users aged 10.
     *
     * @param searchMap The hashmap with associated key value pairs
     * @param token the users token
     * @return a JSON array of users.
     */
    @Override
    public List<User> queryUsers(Map<String, String> searchMap, String token) {
        List<User> queriedUsers = new ArrayList<>(users);

        int startIndex;
        int count;

        if(searchMap.containsKey("startIndex")) {
            startIndex = Integer.parseInt(searchMap.get("startIndex"));
        } else {
            startIndex = 0;
        }

        if(searchMap.containsKey("count")) {
            count = Integer.parseInt(searchMap.get("count"));
        } else {
            count = 100;
        }

        if(searchMap.containsKey("name")) {
            queriedUsers.retainAll(getUsersByNameAlternative(searchMap.get("name")));
        }

        if(searchMap.containsKey("password")) {
            List<User> usersWithSamePass = new ArrayList<>();
            for(User u : users) {
                if(u.getPassword().equals(searchMap.get("password"))) {
                    usersWithSamePass.add(u);
                }
            }
            queriedUsers.retainAll(usersWithSamePass);
        }

        if(searchMap.containsKey("userType")) {
            List<User> usersOfSameType = new ArrayList<>();
            for(User u : users) {
                if(searchMap.get("userType").toLowerCase().equals("neither") && !u.isDonor() && !u.isReceiver()) {
                    usersOfSameType.add(u);
                } else if(searchMap.get("userType").toLowerCase().equals("donor") && u.isDonor() && !u.isReceiver()) {
                    usersOfSameType.add(u);
                } else if(searchMap.get("userType").toLowerCase().equals("receiver") && !u.isDonor() && u.isReceiver()) {
                    usersOfSameType.add(u);
                }
            }
            queriedUsers.retainAll(usersOfSameType);
        }


        if(searchMap.containsKey("age")) {
            List<User> usersOfSameAge = new ArrayList<>();
            for(User u : users) {
                if(String.valueOf((int) u.getAgeDouble()).equals(searchMap.get("age"))) {
                    usersOfSameAge.add(u);
                }
            }
            queriedUsers.retainAll(usersOfSameAge);
        }

        if(searchMap.containsKey("gender")) {
            List<User> usersOfSameGender = new ArrayList<>();
            for(User u : users) {
                if(searchMap.get("gender").toLowerCase().equals("male") && u.getGender()== Gender.MALE) {
                    usersOfSameGender.add(u);
                } else if(searchMap.get("gender").toLowerCase().equals("female") && u.getGender()== Gender.FEMALE) {
                    usersOfSameGender.add(u);
                } else if(searchMap.get("gender").toLowerCase().equals("other") && u.getGender()== Gender.NONBINARY) {
                    usersOfSameGender.add(u);
                }
            }
            queriedUsers.retainAll(usersOfSameGender);
        }

        if(searchMap.containsKey("region")) {
            List<User> usersOfSameRegion = new ArrayList<>();
            for(User u : users) {
                if(searchMap.get("region").toLowerCase().equals(u.getRegion().toLowerCase())) {
                    usersOfSameRegion.add(u);
                }
            }
            queriedUsers.retainAll(usersOfSameRegion);
        }

        if(searchMap.containsKey("organ")) {
            List<User> usersOfWithSameOrgan = new ArrayList<>();
            for(User u : users) {
                for(Organ o : u.getOrgans()) {
                    if(searchMap.get("organ").toLowerCase().equals(o.name().toLowerCase())) {
                        usersOfWithSameOrgan.add(u);
                        break;
                    }
                }
            }
            queriedUsers.retainAll(usersOfWithSameOrgan);
        }

        return queriedUsers.subList(startIndex, Math.min(startIndex + count, queriedUsers.size()));
    }


    @Override
    public List<User> getAllUsers(String token) {
        return users;
    }

    /**
     * gets a specific user
     * @param id the users id
     * @param token the users token
     * @return the users profile
     */
    @Override
    public User getUser(long id, String token) {
        for(User a : users) {
            if(a.getId() == id) {
                return a;
            }
        }
        Debugger.log("User with id: " + id + " not found. Returning null.");
        return null;
    }

    /**
     * removes a specific user
     * @param id the user id
     */
    @Override
    public Image getUserPhoto(long id, String token) {
        return null;
    }

    @Override
    public void updateUserPhoto(long id, String image) {}

    @Override
    public void deleteUserPhoto(long id) {}

    @Override
    public void removeUser(long id, String token) {
        for(User u : users) {
            if(u.getId() == id) {
                users.remove(u);
                break;
            }
        }
    }

    @Override
    public int count(String token){
        return users.size();
    }
}
