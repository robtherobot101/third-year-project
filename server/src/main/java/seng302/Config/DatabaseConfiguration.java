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

    private String connectDatabase;
    private String username;
    private String password;
    private String url;
    private String jdbcDriver = "com.mysql.cj.jdbc.Driver";

    /**
     * Constructor method to create a new database configuration object
     * that contains all the required information to interact with the database
     */
    private DatabaseConfiguration() {
        connectDatabase = (String) Server.getInstance().getConfig().get("database_name");
        username = (String) Server.getInstance().getConfig().get("username");
        password = (String) Server.getInstance().getConfig().get("password");
        url = "jdbc:mysql://" + (String) Server.getInstance().getConfig().get("database_address");
        if(Server.getInstance().isTesting()){
            connectDatabase = "seng302-2018-team300-test";
        }
        try {
            cpds.setDriverClass(jdbcDriver);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(url + "/" + connectDatabase + "?useUnicode=yes&characterEncoding=UTF-8");
        cpds.setUser(username);
        cpds.setPassword(password);
        cpds.setMaxPoolSize(100);
        cpds.setMaxStatementsPerConnection(10);
    }

    /**
     * Get the name of the connected database
     * @return A string of the connected database
     */
    public String getDatabaseName() {
        return connectDatabase;
    }

    /**
     * method to get the current database instance
     * @return The current database configuration object
     */
    public static DatabaseConfiguration getInstance() {
        return INSTANCE;
    }


    /**
     * Get a connection to the database from the pool
     * @return a connection to the database
     * @throws SQLException if an error occurred with communicating with the database
     */
    public Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }
}
