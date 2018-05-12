package seng302.Generic;

import seng302.User.Attribute.Organ;
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


    public int getUserId(String username) throws SQLException{
        String query = "SELECT id FROM USER WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");
    }

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
        statement.setDate(9, java.sql.Date.valueOf(user.getDateOfBirth()));
        System.out.println("Inserting new user -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void updateUserAccountSettings(User user, int userId) throws SQLException {
        String update = "UPDATE USER SET username = ?, email = ?, password = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setInt(4, userId);
        System.out.println("Update User Account Settings -> Successful -> Rows Updated: " + statement.executeUpdate());

    }

    public void updateUserAttributesAndOrgans(User user) throws SQLException {
        //Attributes update
        //TODO Need to update for changes in the attributes pane
        String update = "UPDATE USER SET first_name = ?, middle_names = ?, last_name = ?, current_address = ?, " +
                "region = ?, date_of_birth = ?, date_of_death = ?, height = ?, weight = ?, blood_pressure = ?, " +
                "gender = ?, blood_type = ?, smoker_status = ?, alcohol_consumption = ?  WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getNameArray()[0]);
        statement.setString(2, user.getNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null);
        statement.setString(3, user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null);
        statement.setString(4, user.getCurrentAddress());
        statement.setString(5, user.getRegion());
        statement.setDate(6, java.sql.Date.valueOf(user.getDateOfBirth()));
        statement.setDate(7, user.getDateOfDeath() != null ? java.sql.Date.valueOf(user.getDateOfDeath()) : null);
        statement.setDouble(8, user.getHeight());
        statement.setDouble(9, user.getWeight());
        statement.setString(10, user.getBloodPressure());
        statement.setString(11, user.getGender() != null ? user.getGender().toString() : null);
        statement.setString(12, user.getBloodType() != null ? user.getBloodType().toString() : null);
        statement.setString(13, user.getSmokerStatus() != null ? user.getSmokerStatus().toString() : null);
        statement.setString(14, user.getAlcoholConsumption() != null ? user.getAlcoholConsumption().toString() : null);
        statement.setString(15, user.getUsername());
        System.out.println("Update User Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());


        int userId = getUserId(user.getUsername());
        System.out.println(userId);

        //Organ Updates
        //First get rid of all the users organs in the table
        String deleteOrgansQuery = "DELETE FROM DONATION_LIST_ITEM WHERE user_id = ?";
        PreparedStatement deleteOrgansStatement = connection.prepareStatement(deleteOrgansQuery);
        deleteOrgansStatement.setInt(1, userId);
        System.out.println("Organ rows deleted: " + deleteOrgansStatement.executeUpdate());

        int totalAdded = 0;
        //Then repopulate it with the new updated organs
        for (Organ organ: user.getOrgans()) {
            String insertOrgansQuery = "INSERT INTO DONATION_LIST_ITEM (name, user_id) VALUES (?, ?)";
            PreparedStatement insertOrgansStatement = connection.prepareStatement(insertOrgansQuery);
            insertOrgansStatement.setString(1, organ.toString());
            insertOrgansStatement.setInt(2, userId);
            totalAdded += insertOrgansStatement.executeUpdate();
        }
        System.out.println("Update User Organ Donations -> Successful -> Rows Updated: " + totalAdded);
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

    public void login(String usernameEmail, String password) {


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