package seng302.User.Attribute;

public enum NZRegion {
    AUK("Auckland"), BOP("Bay of Plenty"), NTL("Northland"), WKO("Waikato");

    private String region;

    NZRegion(String region) {
        this.region = region;
    }

    public String toString() {
        return region;
    }

    /**
     * Converts a string to its corresponding organ enum value. Throws an IllegalArgumentException if the string does not match any organ.
     *
     * @param text The string to convert
     * @return The organ corresponding to the input string
     */
    public static NZRegion parse(String text) {
        for (NZRegion region : NZRegion.values()) {
            if (region.toString().equalsIgnoreCase(text)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Region not recognised");
    }
}
