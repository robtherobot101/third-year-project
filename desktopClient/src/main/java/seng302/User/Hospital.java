package seng302.User;

public class Hospital {

    private String name;
    private String address;
    private String region;
    private String city;
    private String country;
    private double latitude;
    private double longitude;

    public Hospital(String name, String address, String region, String city, String country, double latitude, double longitude){
        this.name = name;
        this.address = address;
        this.region = region;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getRegion() {
        return region;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
