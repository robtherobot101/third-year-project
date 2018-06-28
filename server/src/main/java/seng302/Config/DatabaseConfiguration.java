package seng302.Config;

import seng302.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;

public class DatabaseConfiguration {

    private String connectDatabase = "seng302-2018-team300-prod";
    private String username = "seng302-team300";
    private String password = "WeldonAside5766";
    private String url = "jdbc:mysql://mysql2.csse.canterbury.ac.nz/";
    private String jdbcDriver = "com.mysql.cj.jdbc.Driver";

    public Connection getConnection() {
        try {
            Class.forName(jdbcDriver);
            Connection connection = DriverManager.getConnection(
                    url + connectDatabase, username, password);
            Server.getInstance().log.info("Connected to " + connectDatabase + " database ");
            return connection;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCurrentDatabase() {
        return "`" + this.connectDatabase + "`";
    }
}
