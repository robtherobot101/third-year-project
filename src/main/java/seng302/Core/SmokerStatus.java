package seng302.Core;

/**
 * This enum represents the different types of smoker status.
 */
public enum SmokerStatus {
    NEVER("Never"), CURRENT("Current"), PAST("Past");

    private String type;

    SmokerStatus(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }

//    /**
//     * Converts a string to its corresponding BloodType enum value. Throws an IllegalArgumentException if the string does not match any BloodType.
//     *
//     * @param text The string to convert
//     * @return The BloodType corresponding to the input string
//     */
//    public static BloodType parse(String text) {
//        for (BloodType bloodType : BloodType.values()) {
//            if (bloodType.toString().equalsIgnoreCase(text)) {
//                return bloodType;
//            }
//        }
//        throw new IllegalArgumentException("Blood type not recognised.");
//    }
}
