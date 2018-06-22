package seng302.Model.Attribute;

/**
 * This enum represents the different types of possible logins.
 */
public enum ProfileType {
    USER("Model"), CLINICIAN("Clinician"), ADMIN("Admin");

    private String type;

    /**
     * method to create a new enum for the currently logged user
     *
     * @param type the type of user logged
     */
    ProfileType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
