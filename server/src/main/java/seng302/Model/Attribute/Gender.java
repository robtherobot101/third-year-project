package seng302.Model.Attribute;

/**
 * This enum represents the most common genders.
 */
public enum Gender {
    NONBINARY("Non-Binary"), FEMALE("Female"), MALE("Male");

    private String gender;

    /**
     * constructor method to add the gender attribute to a User
     * input can be of type: Non-Binary, Female, or Male
     * @param gender String the gender of a User
     */
    Gender(String gender) { this.gender = gender; }

    public String toString() {
        return gender;
    }

    /**
     * Converts a string to its corresponding Gender enum value. Throws an IllegalArgumentException if the string does not match any Gender.
     *
     * @param text The string to convert
     * @return The Gender corresponding to the input string
     */
    public static Gender parse(String text) {
        for (Gender gender : Gender.values()) {
            if (gender.toString().equalsIgnoreCase(text)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Gender not recognised.");
    }
}
