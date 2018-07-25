package seng302.Generic;

public class Country {
    private String countryName;
    private Boolean valid;

    public Country(String name, int validInt){
        countryName = name;
        if (validInt == 0) {
            valid = false;
        } else {
            valid = true;
        }
    }

    public String getCountryName() {
        return countryName;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}