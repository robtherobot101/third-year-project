package seng302.Model.Attribute;

/**
 * This enum represents the different types of smoker status.
 */
public enum AlcoholConsumption {
    NONE("None"), LOW("Low"), AVERAGE("Average"), HIGH("High"), VERYHIGH("Very High"), ALCOHOLIC("Alcoholic");

    private String type;

    /**
     * construction method to add the alcohol consumption attribute to a user
     * input can be of type: None, Low, Average, High, Very High, or Alcoholic
     * @param type String the alcohol consumption of a user
     */
    AlcoholConsumption(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }

    /**
     * Converts a string to its corresponding alcohol consumption enum value. Throws an IllegalArgumentException if the string does not match any alcohol consumption.
     *
     * @param text The string to convert
     * @return The alcohol consumption corresponding to the input string
     */
    public static AlcoholConsumption parse(String text) {
        for (AlcoholConsumption alcoholConsumption : AlcoholConsumption.values()) {
            if (alcoholConsumption.toString().equalsIgnoreCase(text)) {
                return alcoholConsumption;
            }
        }
        throw new IllegalArgumentException("Alcohol Consumption not recognised");
    }
}
