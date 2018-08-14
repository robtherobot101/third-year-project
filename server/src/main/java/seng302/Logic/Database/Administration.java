package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Config.SqlFileParser;
import seng302.Controllers.CLIController;
import seng302.Model.User;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Administration {

    /**
     * method to call to the Database to check if it is online
     * @throws SQLException when the connection to te Database has an error
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
     * method to resample the Database with default entries
     * @throws SQLException when the connection to te Database has an error
     */
    public void resample() throws SQLException, IOException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            SqlFileParser.parse(connection, getClass().getResourceAsStream("/resample.sql")).executeBatch();
        }
    }

    /**
     * method to empty the Database tables with no values
     * @throws SQLException when the connection to te Database has an error
     */
    public void reset() throws SQLException, IOException {

        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            SqlFileParser.parse(connection, getClass().getResourceAsStream("/reset.sql")).executeBatch();
        }
    }
}
