package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GeneralCountries {

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

    public void patchCounties(List<Country> Countries) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Country> countries = new ArrayList<>();
            String query = "DELETE FROM COUNTRIES";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeQuery();
            for (Country country : countries) {
                query = "INSERT INTO `COUNTRIES`(`country`, `valid`) VALUES (" + country.getCountryName() + ",";
                if (country.getValid()) {
                    query += "1)";
                } else {
                    query += "0)";
                }
                PreparedStatement insertStatement = connection.prepareStatement(query);
                insertStatement.executeQuery();
            }
        }
    }
}
