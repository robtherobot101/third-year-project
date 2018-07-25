package seng302.User.Attribute;

public enum NZRegion {
    NTL("Northland"), AUK("Auckland"),WKO("Waikato"),BOP("Bay of Plenty"),GIS("Gisborne"),HKB("Hawke's Bay"),
    TKI("Taranaki"),MWT("Manawatu-Wanganui"),WGN("Wellington"),TAS("Tasman"),NSN("Nelson"),MBH("Marlborough"),WTC("West Coast"),CAN("Canterbury"),OTA("Otago"),STL("Southland");

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
