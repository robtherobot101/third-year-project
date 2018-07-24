package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
}
