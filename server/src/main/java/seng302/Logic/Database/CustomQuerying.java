package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;

import java.sql.Connection;

public class CustomQuerying {

    private Connection connection;
    private String currentDatabase;

    public CustomQuerying() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        connection = databaseConfiguration.getConnection();
        currentDatabase = databaseConfiguration.getCurrentDatabase();
    }

    public String executeQuery(){
        return "result";
    }

}
