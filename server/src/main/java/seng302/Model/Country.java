package seng302.Model;

import java.util.Objects;

public class Country {
    private String countryName;
    private Boolean valid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(countryName, country.countryName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(countryName);
    }

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