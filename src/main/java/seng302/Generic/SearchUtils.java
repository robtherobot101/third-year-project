package seng302.Generic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng302.User.Clinician;
import seng302.User.User;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.max;

public class SearchUtils {
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
     * Returns a list of users matching the given search term for region.
     * If every token matches at least part of the
     * beginning of a one of part of a users name, that user will be returned.
     *
     * @param term The search term containing space separated tokens
     * @return An ArrayList of users sorted by score first, and alphabetically by name second
     */
    public static ArrayList<User> getUsersByRegionAlternative(String term) {
        String[] t = term.split(" ", -1);
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(t));
        if (tokens.contains("")) {
            tokens.remove("");
        }
        ArrayList<User> matched = new ArrayList<>();
        for (User d : DataManager.users) {
            if (d.getRegion() != null) {
                boolean allTokensMatchAName = true;
                for (String token : tokens) {
                    if (!matchesAtleastOne(d.getRegion().split(" "), token)) {
                        allTokensMatchAName = false;
                    }
                }
                if (allTokensMatchAName) {
                    matched.add(d);
                }
            }

        }
        return matched;
    }

    /**
     * Returns a list of users matching the given search term for age.
     * If every token matches at least part of the
     * beginning of a one of part of a users name, that user will be returned.
     *
     * @param term The search term containing space separated tokens
     * @return An ArrayList of users sorted by score first, and alphabetically by name second
     */
    public static ArrayList<User> getUsersByAgeAlternative(String term) {
        String[] t = term.split(" ", -1);
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(t));
        if (tokens.contains("")) {
            tokens.remove("");
        }
        ArrayList<User> matched = new ArrayList<>();
        for (User d : DataManager.users) {
            if (d.getRegion() != null) {
                boolean allTokensMatchAName = true;
                for (String token : tokens) {
                    if (!matches(d.getAgeString(), token)) {
                        allTokensMatchAName = false;
                    }
                }
                if (allTokensMatchAName) {
                    matched.add(d);
                }
            }

        }
        return matched;
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
     * Returns true if the given token matches the beginning of the given string.
     * Otherwise returns false.
     * Case is ignored.
     *
     * @param string The string to test
     * @param token  The given search token
     * @return True if token and string at least start the same, otherwise false.
     */
    public static boolean matches(String string, String token) {
        string = string.toLowerCase();
        token = token.toLowerCase();
        return string.matches(token + ".*");
    }

    /**
     * Find a specific user from the user list based on their id.
     *
     * @param id The id of the user to search for
     * @return The user object or null if the user was not found
     */
    public static User getUserById(long id) {
        if (id < 0) {
            return null;
        }
        User found = null;
        for (User user : DataManager.users) {
            if (user.getId() == id) {
                found = user;
                break;
            }
        }
        return found;
    }

    /**
     * Find a specific clinician from the user list based on their staff id.
     *
     * @param id The id of the clinician to search for
     * @return The clinician object or null if the clinician was not found
     */
    public static Clinician getClinicianById(long id) {
        if (id < 0) {
            return null;
        }
        Clinician found = null;
        for (Clinician clinician : DataManager.clinicians) {
            if (clinician.getStaffID() == id) {
                found = clinician;
                break;
            }
        }
        return found;
    }

    /**
     * Find a specific user from the user list based on their name.
     *
     * @param names The names of the user to search for
     * @return The user objects that matched the input names
     */
    public static ObservableList<User> getUserByName(String[] names) {
        ObservableList<User> found = FXCollections.observableArrayList();
        if (names.length == 0) {
            return DataManager.users;
        }
        int matched;
        for (User user : DataManager.users) {
            matched = 0;
            for (String name : user.getNameArray()) {
                if (name.toLowerCase().contains(names[matched].toLowerCase())) {
                    matched++;
                    if (matched == names.length) {
                        break;
                    }
                }
            }
            if (matched == names.length) {
                found.add(user);
            }
        }
        return found;
    }

    /**
     * Returns true if the given token matches the beginning of at least one of the given names.
     * Otherwise returns false.
     *
     * @param names The list of names
     * @param token The token to test
     * @return A boolean value which denotes whether or not the token matches a name
     */
    public static boolean matchesAtleastOne(String[] names, String token) {
        for (String name : names) {
            if (matches(name, token)) {
                return true;
            }
        }
        return false;
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
    public static ArrayList<User> getUsersByNameAlternative(String term) {
        System.out.println("search: " + "'" + term + "'");
        if (term.equals("")) {
            System.out.println("Empty");
            ArrayList<User> sorted = new ArrayList<>(DataManager.users);
            sorted.sort(Comparator.comparing(User::getName));
            return sorted;
        }
        String[] t = term.split(" ", -1);
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(t));
        System.out.println("token 1: " + "'" + tokens.get(0) + "'");
        //System.out.println("token 2: " + "'"+tokens.get(1)+"'");
        if (tokens.contains("")) {
            tokens.remove("");
        }
        ArrayList<User> matched;
        matched = new ArrayList<>();
        for (User user : DataManager.users) {
            if (scoreUserOnSearch(user, tokens) > 0) {
                matched.add(user);
            }
        }
        matched.sort((o1, o2) -> {
            Integer o1Score = scoreUserOnSearch(o1, tokens);
            Integer o2Score = scoreUserOnSearch(o2, tokens);

            int scoreComparison = o2Score.compareTo(o1Score);
            if (scoreComparison == 0) {
                return o1.getName().compareTo(o2.getName());
            }
            return scoreComparison;
        });
        return matched;
    }

    /**
     * Returns the most recently added user to the program, by taking the last element in the users list.
     * Called in the history function.
     * @return the most recently added user.
     */
    public static User getLatestUser() {
        return DataManager.users.get(DataManager.users.size()-1);
    }

    public static Clinician getLatestClincian() {
        return DataManager.clinicians.get(DataManager.clinicians.size()-1);
    }
}
