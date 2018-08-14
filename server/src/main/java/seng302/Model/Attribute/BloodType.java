package seng302.Model.Attribute;

/**
 * This enum represents the different blood types.
 */
public enum BloodType {
    A_NEG("A-"), A_POS("A+"), B_NEG("B-"), B_POS("B+"), AB_NEG("AB-"), AB_POS("AB+"), O_NEG("O-"), O_POS("O+");

    private String type;

    /**
     * constructor method to create a new blood type attribute
     * input can be of type: A-, A+, B-, B+, AB-, AB+, O-, or O+
     * @param type String the type of blood a user has
     */
    BloodType(String type) { this.type = type; }

    public String toString() { return type; }

    /**
     * Converts a string to its corresponding BloodType enum value. Throws an IllegalArgumentException if the string does not match any BloodType.
     *
     * @param text The string to convert
     * @return The BloodType corresponding to the input string
     */
    public static BloodType parse(String text) {
        for (BloodType bloodType : BloodType.values()) {
            if (bloodType.toString().equalsIgnoreCase(text)) {
                return bloodType;
            }
        }
        throw new IllegalArgumentException("Blood type not recognised.");
    }
}
