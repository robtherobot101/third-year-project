package seng302.Config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import seng302.Server;

import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;

public class DatabaseConfiguration {

    private static final DatabaseConfiguration INSTANCE = new DatabaseConfiguration();

    private ComboPooledDataSource cpds = new ComboPooledDataSource();

    private String connectDatabase = "seng302-2018-team300-prod";
    private String username = "seng302-team300";
    private String password = "WeldonAside5766";
    private String url = "jdbc:mysql://mysql2.csse.canterbury.ac.nz";
    private String jdbcDriver = "com.mysql.cj.jdbc.Driver";

    private DatabaseConfiguration() {
        if(Server.getInstance().isTesting()){
            connectDatabase = "seng302-2018-team300-test";
        }
        try {
            cpds.setDriverClass(jdbcDriver);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(url + "/" + connectDatabase);
        cpds.setUser(username);
        cpds.setPassword(password);
        cpds.setMaxPoolSize(100);
        cpds.setMaxStatementsPerConnection(10);
    }

    public static DatabaseConfiguration getInstance() {
        return INSTANCE;
    }

    /**
     * Send an SQL update command to the database and return how many rows were affected or -1 if the command failed
     * @param sql The command to be run
     * @return The number of rows affected
     */
    public int updateSql(String sql){
        try (Connection connection = cpds.getConnection()){
            return connection.prepareStatement(sql).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Send an SQL query to the database and returns the set of results
     * @param sql The query to be executed
     * @return A ResultSet of the results
     */
    public ResultSet querySql(String sql) throws SQLException {
        Connection connection = cpds.getConnection();
        return connection.prepareStatement(sql).executeQuery();
    }

    /**
     * Get a connection to the database from the pool
     * @return a connection to the database
     */
    public Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

    public String getCurrentDatabase() {
        return "`" + this.connectDatabase + "`";
    }
}
