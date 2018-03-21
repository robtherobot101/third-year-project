package seng302.Core;

/**
 * This enum represents the different types of smoker status.
 */
public enum AlcoholConsumption {
    NONE("None"), LOW("Low"), AVERAGE("Average"), HIGH("High"), VERYHIGH("Very High"), ALCOHOLIC("Alcoholic");

    private String type;

    AlcoholConsumption(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
