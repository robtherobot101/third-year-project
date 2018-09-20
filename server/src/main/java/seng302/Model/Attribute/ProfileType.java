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

    /**
     * Get the access level of a profile type.
     *
     * @return The access level
     */
    public int getAccessLevel() {
        switch (this) {
            case USER:
                return 0;
            case CLINICIAN:
                return 1;
            case ADMIN:
                return 2;
                default:
                    return 0;
        }
    }

    /**
     * Converts a database access level integer to a profile type.
     *
     * @param accessLevel The database access level
     * @return The profile type
     */
    public static ProfileType fromAccessLevel(int accessLevel) {
        switch (accessLevel) {
            case 0:
                return USER;
            case 1:
                return CLINICIAN;
            case 2:
                return ADMIN;
            default:
                throw new IllegalArgumentException("Invalid access level");
        }
    }

    public String toString() {
        return type;
    }
}
