package seng302.User.Attribute;

/**
 * This enum represents the different types of possible logins.
 */
public enum ProfileType {
    USER("User"), CLINICIAN("Clinician"), ADMIN("Admin");

    private String type;

    /**
     * method to create a new enum for the currently logged User
     *
     * @param type the type of User logged
     */
    ProfileType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }

    /**
     * Converts a string to its corresponding organ enum value. Throws an IllegalArgumentException if the string does not match any profile type.
     *
     * @param text The string to convert
     * @return The profile type corresponding to the input string
     */
    public static ProfileType parse(String text) {
        for (ProfileType profileType : ProfileType.values()) {
            if (profileType.toString().equalsIgnoreCase(text)) {
                return profileType;
            }
        }
        throw new IllegalArgumentException("Profile type not recognised");
    }
}
