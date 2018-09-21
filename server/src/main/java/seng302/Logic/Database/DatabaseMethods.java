package seng302.Logic.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract class which all SQL communication classes should extend from
 */
public abstract class DatabaseMethods {

    protected void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            try {
                if (closeable != null) closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
