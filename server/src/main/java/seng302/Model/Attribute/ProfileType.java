package seng302.Model.Attribute;

/**
 * This enum represents the different types of possible logins.
 */
public enum ProfileType {
    USER("Model"), CLINICIAN("clinician"), ADMIN("admin");

    private String type;

    /**
     * method to create a new enum for the currently logged user
     * input can be of type: Model, clinician, or admin
     * @param type the type of user logged
     */
    ProfileType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
