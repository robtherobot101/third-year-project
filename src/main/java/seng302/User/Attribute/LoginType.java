package seng302.User.Attribute;

/**
 * This enum represents the different types of possible logins.
 */
public enum LoginType {
    USER("User"), CLINICIAN("Clinician"), ADMIN("Admin");

    private String type;

    LoginType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
