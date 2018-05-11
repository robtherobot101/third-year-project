package seng302.Generic;

import seng302.User.User;

import java.sql.*;
import java.util.Arrays;

public class Database {

    private String testDatabase = "seng302-2018-team300-test";
    private String username = "seng302-team300";
    private String password = "WeldonAside5766";
    private String url = "jdbc:mysql://mysql2.csse.canterbury.ac.nz/";
    private String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    private Connection connection;


    public void insertUser(User user) throws SQLException {
        String insert = "INSERT INTO USER(first_name, middle_names, last_name, creation_time, last_modified, username," +
                " email, password, date_of_birth) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, user.getNameArray()[0]);
        statement.setString(2, user.getNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null);
        statement.setString(3, user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null);
        statement.setTimestamp(4, java.sql.Timestamp.valueOf(user.getCreationTime()));
        statement.setTimestamp(5, java.sql.Timestamp.valueOf(user.getCreationTime()));
        statement.setString(6, user.getUsername());
        statement.setString(7, user.getEmail());
        //TODO Hash the password
        statement.setString(8, user.getPassword());
        //TODO
        statement.setDate(9, java.sql.Date.valueOf(user.getDateOfBirth()));
        System.out.println("New rows added: " + statement.executeUpdate());

    }

    public boolean checkUniqueUser(String item) throws SQLException{
        String query = "SELECT * FROM USER WHERE USER.username = ? OR USER.email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, item);
        statement.setString(2, item);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        query = "SELECT * FROM CLINICIAN WHERE CLINICIAN.username = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, item);
        resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        query = "SELECT * FROM ADMIN WHERE ADMIN.username = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, item);
        resultSet = statement.executeQuery();
        if(resultSet.next()) {
            return false;
        }
        return true;
    }

    public void insertClinician() {

    }

    public void insertAdmin() {

    }

    public void connectToDatabase() {
        try{
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(
                    url + testDatabase, username, password);
            System.out.println("Connected to test database");
            //Statement stmt=con.createStatement();
            //ResultSet rs=stmt.executeQuery("SELECT * FROM USER");
            //System.out.println("Users:");

//            while(rs.next())
//                System.out.println(rs.getString(1));
//            con.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}