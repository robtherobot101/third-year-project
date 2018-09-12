package seng302.Logic;
import com.lambdaworks.crypto.SCryptUtil;

/**
 * Class for salting and hashing passwords
 */
public class SaltHash {
    /**
     * method to create a hash from a given password
     *
     * @param password the given user password to hash
     * @return String the hash of the password
     */
    public static String createHash(String password){
        String generatedSecuredPasswordHash = SCryptUtil.scrypt(password, 16, 16, 16);
        return generatedSecuredPasswordHash;
    }

    /**
     * method to check if a given string matches a given hash
     *
     * @param password the given string
     * @param hash     the given hash to check
     * @return Boolean if the password matches the hash
     */
    public static Boolean checkHash(String password, String hash){
        try {
            boolean matched = SCryptUtil.check(password, hash);
            return matched;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
