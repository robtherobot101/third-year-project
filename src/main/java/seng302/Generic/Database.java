package seng302.Generic;

import seng302.User.Admin;
import seng302.User.Attribute.*;
import seng302.User.Clinician;
import seng302.User.Medication.Medication;
import seng302.User.User;

import java.sql.*;
import java.util.ArrayList;
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

    public int getClinicianId(String username) throws SQLException{
        String query = "SELECT staff_id FROM CLINICIAN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("staff_id");
    }

    public int getAdminId(String username) throws SQLException{
        String query = "SELECT staff_id FROM ADMIN WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("staff_id");
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

    public void updateUserProcedures(User user) throws SQLException {
        int userId = getUserId(user.getUsername());
        System.out.println(userId);

        //Procedure Updates
        //First get rid of all the users procedures in the table
        String deleteProceduresQuery = "DELETE FROM PROCEDURE WHERE user_id = ?";
        PreparedStatement deleteProceduresStatement = connection.prepareStatement(deleteProceduresQuery);
        deleteProceduresStatement.setInt(1, userId);
        System.out.println("Procedure rows deleted: " + deleteProceduresStatement.executeUpdate());


        int totalAdded = 0;
        //Then repopulate it with the new updated procedures
        ArrayList<Procedure> allProcedures = new ArrayList<>();
        allProcedures.addAll(user.getPendingProcedures());
        allProcedures.addAll(user.getPendingProcedures());
        for (Procedure procedure: allProcedures) {
            String insertProceduresQuery = "INSERT INTO PROCEDURE (summary, description, date, organs_affected, user_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertProceduresStatement = connection.prepareStatement(insertProceduresQuery);

            insertProceduresStatement.setString(1, procedure.getSummary());
            insertProceduresStatement.setString(2, procedure.getDescription());
            insertProceduresStatement.setDate(3, java.sql.Date.valueOf(procedure.getDate()));
            insertProceduresStatement.setString(4, "ORGANS AFFECTED");
            insertProceduresStatement.setInt(5, userId);

            totalAdded += insertProceduresStatement.executeUpdate();
        }

        System.out.println("Update User Procedures -> Successful -> Rows Updated: " + totalAdded);

    }

    public void updateUserDiseases(User user) throws SQLException {
        int userId = getUserId(user.getUsername());
        System.out.println(userId);

        //Disease Updates
        //First get rid of all the users diseases in the table
        String deleteDiseasesQuery = "DELETE FROM DISEASE WHERE user_id = ?";
        PreparedStatement deleteDiseasesStatement = connection.prepareStatement(deleteDiseasesQuery);
        deleteDiseasesStatement.setInt(1, userId);
        System.out.println("Disease rows deleted: " + deleteDiseasesStatement.executeUpdate());


        int totalAdded = 0;
        //Then repopulate it with the new updated diseases
        ArrayList<Disease> allDiseases = new ArrayList<>();
        allDiseases.addAll(user.getCurrentDiseases());
        allDiseases.addAll(user.getCuredDiseases());
        for (Disease disease: allDiseases) {
            String insertDiseasesQuery = "INSERT INTO DISEASE (name, diagnosis_date, is_cured, is_chronic, user_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertDiseasesStatement = connection.prepareStatement(insertDiseasesQuery);

            insertDiseasesStatement.setString(1, disease.getName());
            insertDiseasesStatement.setDate(2, java.sql.Date.valueOf(disease.getDiagnosisDate()));
            insertDiseasesStatement.setBoolean(3, disease.isCured());
            insertDiseasesStatement.setBoolean(4, disease.isChronic());
            insertDiseasesStatement.setInt(5, userId);

            totalAdded += insertDiseasesStatement.executeUpdate();
        }

        System.out.println("Update User Diseases -> Successful -> Rows Updated: " + totalAdded);

    }

    public void updateUserMedications(User user) throws SQLException {
        int userId = getUserId(user.getUsername());
        System.out.println(userId);

        //Procedure Updates
        //First get rid of all the users medications in the table
        String deleteMedicationsQuery = "DELETE FROM MEDICATION WHERE user_id = ?";
        PreparedStatement deleteMedicationsStatement = connection.prepareStatement(deleteMedicationsQuery);
        deleteMedicationsStatement.setInt(1, userId);
        System.out.println("Medication rows deleted: " + deleteMedicationsStatement.executeUpdate());


        int totalAdded = 0;
        //Then repopulate it with the new updated medications
        ArrayList<Medication> allMedications = new ArrayList<>();
        allMedications.addAll(user.getCurrentMedications());
        allMedications.addAll(user.getHistoricMedications());
        for (Medication medication: allMedications) {
            String insertMedicationsQuery = "INSERT INTO MEDICATION (name, active_ingredients, history, user_id) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement insertMedicationsStatement = connection.prepareStatement(insertMedicationsQuery);

            String activeIngredientsString = String.join(",", medication.getActiveIngredients());
            String historyString = String.join(",", medication.getHistory());

            insertMedicationsStatement.setString(1, medication.getName());
            insertMedicationsStatement.setString(2, activeIngredientsString);
            insertMedicationsStatement.setString(3, historyString);
            insertMedicationsStatement.setInt(4, userId);

            totalAdded += insertMedicationsStatement.executeUpdate();
        }

        System.out.println("Update User Medications -> Successful -> Rows Updated: " + totalAdded);

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

    public void insertClinician(Clinician clinician) throws SQLException {
        String insert = "INSERT INTO CLINICIAN(username, password, name, work_address, region) " +
                "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, clinician.getUsername());
        statement.setString(2, clinician.getPassword());
        statement.setString(3, clinician.getName());
        statement.setString(4, clinician.getWorkAddress());
        statement.setString(5, clinician.getRegion());
        System.out.println("Inserting new Clinician -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void updateClinicianDetails(Clinician clinician) throws SQLException {
        String update = "UPDATE CLINICIAN SET name = ?, work_address = ?, region = ? WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, clinician.getName());
        statement.setString(2, clinician.getWorkAddress());
        statement.setString(3, clinician.getRegion());
        statement.setString(4, clinician.getUsername());
        System.out.println("Update Clinician Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());

    }

    public void updateClinicianAccountSettings(Clinician clinician, int clinicianId) throws SQLException {
        String update = "UPDATE CLINICIAN SET username = ?, password = ? WHERE staff_id = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, clinician.getUsername());
        statement.setString(2, clinician.getPassword());
        statement.setInt(3, clinicianId);
        System.out.println("Update Clinician Account Settings -> Successful -> Rows Updated: " + statement.executeUpdate());
    }

    public void insertAdmin(Admin admin) throws SQLException {
        String insert = "INSERT INTO ADMIN(username, password, name, work_address, region) " +
                "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, admin.getUsername());
        statement.setString(2, admin.getPassword());
        statement.setString(3, admin.getName());
        statement.setString(4, admin.getWorkAddress());
        statement.setString(5, admin.getRegion());
        System.out.println("Inserting new Admin -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public void updateAdminDetails(Admin admin) throws SQLException {
        String update = "UPDATE ADMIN SET name = ?, work_address = ? WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, admin.getName());
        statement.setString(2, admin.getWorkAddress());
       // statement.setString(3, admin.getRegion()); -- No Region for an Admin!
        statement.setString(3, admin.getUsername());
        System.out.println("Update Admin Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());

    }


    public User loginUser(String usernameEmail, String password) throws SQLException {
        //First needs to do a search to see if there is a unique user with the given inputs
        // SELECT * FROM USER WHERE username = usernameEmail OR email = usernameEmail AND password = password
        String query = "SELECT * FROM USER WHERE (username = ? OR email = ?) AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, usernameEmail);
        statement.setString(2, usernameEmail);
        statement.setString(3, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new User Object with the fields from the database
            User user = new User(
                    resultSet.getString("first_name"),
                    resultSet.getString("middle_names").split(","),
                    resultSet.getString("last_name"),
                    resultSet.getDate("date_of_birth").toLocalDate(),
                    resultSet.getDate("date_of_death") != null ? resultSet.getDate("date_of_death").toLocalDate() : null,
                    resultSet.getString("gender") != null ? Gender.parse(resultSet.getString("gender")) : null,
                    resultSet.getDouble("height"),
                    resultSet.getDouble("weight"),
                    resultSet.getString("blood_type") != null ? BloodType.parse(resultSet.getString("blood_type")) : null,
                    resultSet.getString("region"),
                    resultSet.getString("current_address"),
                    resultSet.getString("username"),
                    resultSet.getString("email"),
                    resultSet.getString("password"));
            user.setLastModifiedForDatabase(resultSet.getTimestamp("last_modified").toLocalDateTime());
            user.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());

            if(resultSet.getString("blood_pressure") != null) {
                user.setBloodPressure(resultSet.getString("blood_pressure"));
            } else {
                user.setBloodPressure("");
            }

            if(resultSet.getString("smoker_status") != null) {
                user.setSmokerStatus(SmokerStatus.parse(resultSet.getString("smoker_status")));
            } else {
                user.setSmokerStatus(null);
            }

            if(resultSet.getString("alcohol_consumption") != null) {
                user.setAlcoholConsumption(AlcoholConsumption.parse(resultSet.getString("alcohol_consumption")));
            } else {
                user.setAlcoholConsumption(null);
            }

            //Get all the organs for the given user

            int userId = getUserId(resultSet.getString("username"));
            //TODO - Potentially set the local value of the user's id to this ??

            String organsQuery = "SELECT * FROM DONATION_LIST_ITEM WHERE user_id = ?";
            PreparedStatement organsStatement = connection.prepareStatement(organsQuery);
            organsStatement.setInt(1, userId);
            ResultSet organsResultSet = organsStatement.executeQuery();

            while(organsResultSet.next()) {
                System.out.println(organsResultSet.getString("name"));
                user.getOrgans().add(Organ.parse(organsResultSet.getString("name")));
            }

            //Get all the medications for the given user

            String medicationsQuery = "SELECT * FROM MEDICATION WHERE user_id = ?";
            PreparedStatement medicationsStatement = connection.prepareStatement(medicationsQuery);
            medicationsStatement.setInt(1, userId);
            ResultSet medicationsResultSet = medicationsStatement.executeQuery();

            while(medicationsResultSet.next()) {
                String[] activeIngredients = medicationsResultSet.getString("active_ingredients").split(",");
                String[] historyStringList = medicationsResultSet.getString("history").split(",");

                ArrayList<String> historyList = new ArrayList<>();
                //Iterate through the history list to get associated values together
                int counter = 1;
                String historyItem = "";
                for(int i = 0; i < historyStringList.length; i++) {
                    if(counter % 2 == 1) {
                        historyItem += historyStringList[i] + ",";
                        counter++;
                    } else {
                        historyItem += historyStringList[i];
                        historyList.add(historyItem);
                        historyItem = "";
                        counter++;
                    }

                }

                for(String s : historyList){
                    System.out.println(s);
                    System.out.println("break");
                }

                if(historyList.get(historyList.size() - 1).contains("Stopped taking")) { //Medication is historic
                    System.out.println("Historic");
                    user.getHistoricMedications().add(new Medication(
                            medicationsResultSet.getString("name"),
                            activeIngredients,
                            historyList
                    ));
                } else { //Medication is Current
                    System.out.println("Current");
                    user.getCurrentMedications().add(new Medication(
                            medicationsResultSet.getString("name"),
                            activeIngredients,
                            historyList
                    ));
                }
            }


            //Get all the procedures for the given user

            //Get all the diseases for the given user

            return user;
        }

    }

    public Clinician loginClinician(String usernameEmail, String password) throws SQLException{
        //First needs to do a search to see if there is a unique clinician with the given inputs
        String query = "SELECT * FROM CLINICIAN WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, usernameEmail);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new Clinican Object with the fields from the database
            Clinician clinician = new Clinician(
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("name")
            );
            clinician.setWorkAddress(resultSet.getString("work_address"));
            clinician.setRegion(resultSet.getString("region"));

            return clinician;
        }

    }

    public Admin loginAdmin(String usernameEmail, String password) throws SQLException {
        //First needs to do a search to see if there is a unique admin with the given inputs
        String query = "SELECT * FROM ADMIN WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, usernameEmail);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new Admin Object with the fields from the database
            Admin admin = new Admin(
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("name")
            );
            admin.setWorkAddress(resultSet.getString("work_address"));
            admin.setRegion(resultSet.getString("region"));

            return admin;
        }

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