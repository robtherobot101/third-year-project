package seng302.Model;

import seng302.Model.Attribute.Organ;

import java.time.LocalDateTime;
import java.util.List;

public class MapObject {

    public String firstName;
    public String middleName;
    public String lastName;
    public String gender;
    public int id;
    public String currentAddress;
    public String region;
    public String cityOfDeath;
    public String regionOfDeath;
    public String countryOfDeath;
    public List<DonatableOrgan> organs;
    public LocalDateTime dateOfDeath;

    public MapObject() {

    }

    public List<DonatableOrgan> getOrgans() {
        return organs;
    }

    public void setOrgans(List<DonatableOrgan> organs) {
        this.organs = organs;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
