package seng302.Logic.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract class which all SQL communication classes should extend from
 */
public abstract class DatabaseMethods {

    protected PreparedStatement statement;
    protected ResultSet resultSet;

    void close() throws SQLException {
        try{ if (resultSet != null) resultSet.close(); } catch(SQLException ignored) {}
        try{ if (statement != null) statement.close(); } catch(SQLException ignored) {}
    }
}
