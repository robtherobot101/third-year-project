package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeneralHospital extends DatabaseMethods {

    /**
     * Fetches all hospitals from the database and returns them.
     * @return a list of hospital objects
     * @throws SQLException If the database cannot be reached.
     */
    public ArrayList<Hospital> getHospitals() throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Hospital> hospitals = new ArrayList<>();
            String query = "SELECT * FROM HOSPITAL";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                hospitals.add(new Hospital(
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("region"),
                        resultSet.getString("city"),
                        resultSet.getString("country"),
                        resultSet.getDouble("latitude"),
                        resultSet.getDouble("longitude")
                        ));
            }
            return hospitals;
        }
        finally {
            close();
        }
    }
}
