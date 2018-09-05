package seng302.User.Attribute;

/**
 * An enum representing the different organ types.
 */
public enum Organ {
    BONE("bone-marrow"), TISSUE("connective-tissue"),  CORNEA("cornea"), HEART("heart"), INTESTINE("intestine"), KIDNEY("kidney"), LIVER("liver"), LUNG("lung"),
    EAR("middle-ear"), PANCREAS("pancreas"),  SKIN("skin");

    private String organType;

    Organ(String organ) {
        this.organType = organ;
    }

    @Override
    public String toString() {
        return organType;
    }


    /**
     * Converts a string to its corresponding organ enum value. Throws an IllegalArgumentException if the string does not match any organ.
     *
     * @param text The string to convert
     * @return The organ corresponding to the input string
     */
    public static Organ parse(String text) {
        for (Organ organ : Organ.values()) {
            if (organ.toString().equalsIgnoreCase(text)) {
                return organ;
            }
        }
        throw new IllegalArgumentException("Organ not recognised");
    }
}


