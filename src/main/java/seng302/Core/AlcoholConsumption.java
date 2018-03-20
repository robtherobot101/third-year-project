package seng302.Core;

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
