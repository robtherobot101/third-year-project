package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Config.SqlFileParser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Administration {

    /**
     * method to call to the database to check if it is online
     * @throws SQLException when the connection to te database has an error
     */
    public void status() throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT staff_id FROM ADMIN WHERE staff_id = 1";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
        }
    }


    /**
     * method to resample the database with default entries
     * @throws SQLException when the connection to te database has an error
     * @throws IOException when the resource name has an error
     */
    public void resample() throws SQLException, IOException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            SqlFileParser.parse(connection, getClass().getResourceAsStream("/resample.sql")).executeBatch();
        }
    }

    /**
     * method to empty the database tables with no values
     * @throws SQLException when the connection to te database has an error
     * @throws IOException when the resource name has an error
     */
    public void reset() throws SQLException, IOException {

        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            SqlFileParser.parse(connection, getClass().getResourceAsStream("/reset.sql")).executeBatch();
        }
    }
}
