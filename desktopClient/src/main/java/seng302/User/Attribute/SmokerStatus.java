package seng302.User.Attribute;

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

    /**
     * Converts a string to its corresponding smoker status enum value. Throws an IllegalArgumentException if the string does not match any smoker status.
     *
     * @param text The string to convert
     * @return The smoker status corresponding to the input string
     */
    public static SmokerStatus parse(String text) {
        for (SmokerStatus smokerStatus : SmokerStatus.values()) {
            if (smokerStatus.toString().equalsIgnoreCase(text)) {
                return smokerStatus;
            }
        }
        throw new IllegalArgumentException("Smoker Status not recognised");
    }

}
