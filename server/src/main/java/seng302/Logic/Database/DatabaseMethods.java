package seng302.Logic.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract class which all SQL communication classes should extend from
 */
public abstract class DatabaseMethods {

    protected PreparedStatement statement;
    protected Statement stmt;
    protected ResultSet resultSet;

    protected void close() throws SQLException {
        try{ if (resultSet != null) resultSet.close(); } catch(SQLException ignored) {}
        try{ if (statement != null) statement.close(); } catch(SQLException ignored) {}
        try{ if (stmt != null) stmt.close(); } catch(SQLException ignored) {}
    }
}
