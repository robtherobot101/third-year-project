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

}
