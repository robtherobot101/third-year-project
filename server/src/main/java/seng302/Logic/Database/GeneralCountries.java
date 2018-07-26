package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneralCountries {

    /**
     * Gets all the countries from the database and if they are valid or not
     * @return returns a list of all the countries
     * @throws SQLException Throws if the database cannot be reached
     */
    public ArrayList<Country> getCountries() throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Country> countries = new ArrayList<>();
            String query = "SELECT * FROM COUNTRIES";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                countries.add(new Country(resultSet.getString("country"), resultSet.getInt("valid")));
            }
            return countries;
        }
    }

    /**
     * updates all the countries in the database
     * @param countries the list of countries for the database to be updated to
     * @throws SQLException throws if the database cannot be reached
     */
    public void patchCounties(List<Country> countries) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "DELETE FROM COUNTRIES";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
            for (Country country : countries) {
                query = "INSERT INTO `COUNTRIES`(`country`, `valid`) VALUES ('" + country.getCountryName() + "',";
                if (country.getValid()) {
                    query += "1)";
                } else {
                    query += "0)";
                }
                PreparedStatement insertStatement = connection.prepareStatement(query);
                insertStatement.executeUpdate();
            }
        }
    }
}
