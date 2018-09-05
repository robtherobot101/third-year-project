package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Config.SqlFileParser;
import seng302.Controllers.CLIController;
import seng302.Model.User;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

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
            dropAllTables();
            SqlFileParser.parse(connection, getClass().getResourceAsStream("/reset.sql")).executeBatch();
        }
    }

    /**
     * Drops all tables in the database
     * @throws SQLException When something goes wrong
     * @throws IOException This should never occur
     */
    private void dropAllTables() throws SQLException, IOException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // Get statements to delete each table
            PreparedStatement statement = connection.prepareStatement("SELECT concat('DROP TABLE IF EXISTS ', table_name, ';') " +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = ?;");
            statement.setString(1, DatabaseConfiguration.getInstance().getDatabaseName());
            ResultSet result = statement.executeQuery();
            // Combine the statements into a single batch
            StringBuilder sb = new StringBuilder("SET FOREIGN_KEY_CHECKS = 0; \n");
            while(result.next()) {
                sb.append(result.getString(1)).append("\n");
            }
            sb.append("SET FOREIGN_KEY_CHECKS = 1;");
            System.out.println(sb.toString());
            SqlFileParser.parse(connection, new ByteArrayInputStream(sb.toString().getBytes())).executeBatch();
        }
    }
}
