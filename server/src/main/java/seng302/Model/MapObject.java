package seng302.Model;

public class MapObject {

    public String name;
    public int userId;
    public long timeOfDeath;
    public String currentAddress;
    public String region;
    public String cityOfDeath;
    public String regionOfDeath;
    public String countryOfDeath;

    public MapObject(String name, int userId, long timeOfDeath, String currentAddress,
                     String region, String cityOfDeath, String regionOfDeath, String countryOfDeath) {
        this.name = name;
        this.userId = userId;
        this.timeOfDeath = timeOfDeath;
        this.currentAddress = currentAddress;
        this.region = region;
        this.cityOfDeath = cityOfDeath;
        this.regionOfDeath = regionOfDeath;
        this.countryOfDeath = countryOfDeath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setTimeOfDeath(long timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCityOfDeath() {
        return cityOfDeath;
    }

    public void setCityOfDeath(String cityOfDeath) {
        this.cityOfDeath = cityOfDeath;
    }

    public String getRegionOfDeath() {
        return regionOfDeath;
    }

    public void setRegionOfDeath(String regionOfDeath) {
        this.regionOfDeath = regionOfDeath;
    }

    public String getCountryOfDeath() {
        return countryOfDeath;
    }

    public void setCountryOfDeath(String countryOfDeath) {
        this.countryOfDeath = countryOfDeath;
    }


}
