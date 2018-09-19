package seng302.Logic.Database;

import javafx.scene.chart.PieChart;
import seng302.Config.DatabaseConfiguration;
import seng302.Config.SqlFileParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Administration extends DatabaseMethods {


    /**
     * method to call to the database to check if it is online
     *
     * @throws SQLException when the connection to te database has an error
     */
    public void status() throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT staff_id FROM ADMIN WHERE staff_id = 1";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            resultSet.next();
        } finally {
            close(resultSet, statement);
        }
    }


    /**
     * method to resample the database with default entries
     *
     * @throws SQLException when the connection to te database has an error
     * @throws IOException  when the resource name has an error
     */
    public void resample() throws SQLException, IOException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            SqlFileParser.executeFile(connection, getClass().getResourceAsStream("/resample.sql"));
        }
    }

    /**
     * method to empty the database tables with no values
     *
     * @throws SQLException when the connection to te database has an error
     * @throws IOException  when the resource name has an error
     */
    public void reset() throws SQLException, IOException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            dropAllTables();
            SqlFileParser.executeFile(connection, getClass().getResourceAsStream("/reset.sql"));
        }
    }

    /**
     * Drops all tables in the database
     *
     * @throws SQLException When something goes wrong
     * @throws IOException  This should never occur
     */
    private void dropAllTables() throws SQLException, IOException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // Get statements to delete each table
            statement = connection.prepareStatement("SELECT concat('DROP TABLE IF EXISTS ', table_name, ';') " +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = ?;");
            statement.setString(1, DatabaseConfiguration.getInstance().getDatabaseName());
            resultSet = statement.executeQuery();
            // Combine the statements into a single batch
            StringBuilder sb = new StringBuilder("SET FOREIGN_KEY_CHECKS = 0; \n");
            while (resultSet.next()) {
                sb.append(resultSet.getString(1)).append("\n");
            }
            sb.append("SET FOREIGN_KEY_CHECKS = 1;");
            SqlFileParser.executeFile(connection, new ByteArrayInputStream(sb.toString().getBytes()));
        } finally {
            close(resultSet, statement);
        }
    }
}
