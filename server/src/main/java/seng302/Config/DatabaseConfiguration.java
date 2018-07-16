package seng302.Config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import seng302.Server;

import java.beans.PropertyVetoException;
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
        try {
            cpds.setDriverClass(jdbcDriver);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(url + "/" + connectDatabase);
        cpds.setUser(username);
        cpds.setPassword(password);
    }

    public static DatabaseConfiguration getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() {
        try {
            return cpds.getConnection();
//            Class.forName(jdbcDriver);
//            Connection connection = DriverManager.getConnection(
//                    url + connectDatabase, username, password);
//            Server.getInstance().log.info("Connected to " + connectDatabase + " database ");
//            return connection;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCurrentDatabase() {
        return "`" + this.connectDatabase + "`";
    }
}
