package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.Country;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GeneralCountriesTest extends GenericTest {

    private GeneralCountries generalCountries = new GeneralCountries();

    @Test
    public void getCountries() throws SQLException {
        List<Country> countries = generalCountries.getCountries();
        Country country = new Country("New Zealand", 1);
        assertTrue(countries.contains(country));
    }

    @Test
    public void patchCounties() throws SQLException {
        ArrayList<Country> countries = HelperMethods.makeCountries();
        generalCountries.patchCounties(countries);
        assertEquals(countries, generalCountries.getCountries());
    }
}