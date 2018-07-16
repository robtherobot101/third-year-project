package seng302.Logic.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.AlcoholConsumption;
import seng302.Model.Attribute.BloodType;
import seng302.Model.Attribute.Gender;
import seng302.Model.Attribute.Organ;
import seng302.Model.Attribute.SmokerStatus;
import seng302.Model.Disease;
import seng302.Model.Medication.Medication;
import seng302.Model.Procedure;
import seng302.Model.User;
import seng302.Model.WaitingListItem;

public class GeneralUser {

    private Connection connection;
    private String currentDatabase;

    public GeneralUser() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        connection = databaseConfiguration.getConnection();
        currentDatabase = databaseConfiguration.getCurrentDatabase();
    }

    /**
     * Update a user's attributes, medications, procedures, diseases, organ donations, waiting list items, and history.
     *
     * @param user The user to update
     * @param userId The id of the user to update
     * @throws SQLException If there is errors communicating with the database
     */
    public void patchEntireUser(User user, int userId) throws SQLException {
        updateUserAttributes(user, userId);

        List<Medication> newMedications = new ArrayList<>();
        newMedications.addAll(user.getCurrentMedications());
        newMedications.addAll(user.getHistoricMedications());
        updateAllMedications(newMedications, userId);

        List<Procedure> newProcedures = new ArrayList<>();
        newProcedures.addAll(user.getPendingProcedures());
        newProcedures.addAll(user.getPreviousProcedures());
        updateAllProcedures(newProcedures, userId);

        List<Disease> newDiseases = new ArrayList<>();
        newDiseases.addAll(user.getCuredDiseases());
        newDiseases.addAll(user.getCurrentDiseases());
        updateAllDiseases(newDiseases, userId);

        Set<Organ> newDonations = new HashSet<>(user.getOrgans());
        UserDonations userDonations = new UserDonations();
        userDonations.removeAllUserDonations(userId);
        for (Organ organ: newDonations) {
            userDonations.insertDonation(organ, userId);
        }

        List<WaitingListItem> newWaitingListItems = new ArrayList<>(user.getWaitingListItems());
        updateWaitingListItems(newWaitingListItems, userId);

        UserHistory userHistory = new UserHistory();
        int currentLength = userHistory.getAllHistoryItems(userId).size();
        //TODO add history
    }

    /**
     * Replace a user's medications on the database with a new set of medications.
     *
     * @param newMedications The list of medications to replace the old one with
     * @param userId The id of the user to replace medications of
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllMedications(List<Medication> newMedications, int userId) throws SQLException {
        UserMedications userMedications = new UserMedications();
        List<Medication> oldMedications = userMedications.getAllMedications(userId);

        //Remove all medications that are already on the database
        for (int i = oldMedications.size() - 1; i >= 0; i--) {
            Medication found = null;
            for (Medication newMedication: newMedications) {
                if (newMedication.equals(oldMedications.get(i))) {
                    found = newMedication;
                    break;
                }
            }
            if (found == null) {
                //Patch edited medications
                for (Medication newMedication: newMedications) {
                    if (newMedication.getId() == oldMedications.get(i).getId()) {
                        userMedications.updateMedication(oldMedications.get(i), oldMedications.get(i).getId(), userId);
                        found = newMedication;
                        break;
                    }
                }
            }
            if (found != null) {
                newMedications.remove(found);
                oldMedications.remove(i);
            }
        }

        //Delete all medications from the database that are no longer up to date
        for (Medication medication: oldMedications) {
            userMedications.removeMedication(userId, medication.getId());
        }

        //Upload all new medications
        for (Medication medication: newMedications) {
            userMedications.insertMedication(medication, userId);
        }
    }

    /**
     * Replace a user's procedures on the database with a new set of procedures.
     *
     * @param newProcedures The list of procedures to replace the old one with
     * @param userId The id of the user to replace procedures of
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllProcedures(List<Procedure> newProcedures, int userId) throws SQLException {
        UserProcedures userProcedures = new UserProcedures();
        List<Procedure> oldProcedures = userProcedures.getAllProcedures(userId);

        //Remove all procedures that are already on the database
        for (int i = oldProcedures.size() - 1; i >= 0; i--) {
            Procedure found = null;
            for (Procedure newProcedure: newProcedures) {
                if (newProcedure.equals(oldProcedures.get(i))) {
                    found = newProcedure;
                    break;
                }
            }
            if (found == null) {
                //Patch edited medications
                for (Procedure newProcedure: newProcedures) {
                    if (newProcedure.getId() == oldProcedures.get(i).getId()) {
                        userProcedures.updateProcedure(oldProcedures.get(i), oldProcedures.get(i).getId(), userId);
                        found = newProcedure;
                        break;
                    }
                }
            }
            if (found != null) {
                newProcedures.remove(found);
                oldProcedures.remove(i);
            }
        }

        //Delete all medications from the database that are no longer up to date
        for (Procedure procedure: oldProcedures) {
            userProcedures.removeProcedure(userId, procedure.getId());
        }

        //Upload all new medications
        for (Procedure procedure: newProcedures) {
            userProcedures.insertProcedure(procedure, userId);
        }
    }

    /**
     * Replace a user's diseases on the database with a new set of diseases.
     *
     * @param newDiseases The list of diseases to replace the old one with
     * @param userId The id of the user to replace diseases of
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllDiseases(List<Disease> newDiseases, int userId) throws SQLException {
        UserDiseases userDiseases = new UserDiseases();
        List<Disease> oldDiseases = userDiseases.getAllDiseases(userId);

        //Remove all procedures that are already on the database
        for (int i = oldDiseases.size() - 1; i >= 0; i--) {
            Disease found = null;
            for (Disease newDisease: newDiseases) {
                if (newDisease.equals(oldDiseases.get(i))) {
                    found = newDisease;
                    break;
                }
            }
            if (found == null) {
                //Patch edited medications
                for (Disease newDisease: newDiseases) {
                    if (newDisease.getId() == oldDiseases.get(i).getId()) {
                        userDiseases.updateDisease(oldDiseases.get(i), oldDiseases.get(i).getId(), userId);
                        found = newDisease;
                        break;
                    }
                }
            }
            if (found != null) {
                newDiseases.remove(found);
                oldDiseases.remove(i);
            }
        }

        //Delete all medications from the database that are no longer up to date
        for (Disease disease: oldDiseases) {
            userDiseases.removeDisease(userId, disease.getId());
        }

        //Upload all new medications
        for (Disease disease: newDiseases) {
            userDiseases.insertDisease(disease, userId);
        }
    }

    /**
     * Replace a user's waiting list items on the database with a new set of waiting list items.
     *
     * @param newWaitingListItems The list of waiting list items to replace the old one with
     * @param userId The id of the user to replace waiting list items of
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateWaitingListItems(List<WaitingListItem> newWaitingListItems, int userId) throws SQLException {
        UserWaitingList userWaitingList = new UserWaitingList();
        List<WaitingListItem> oldWaitingListItems = userWaitingList.getAllUserWaitingListItems(userId);

        //Remove all procedures that are already on the database
        for (int i = oldWaitingListItems.size() - 1; i >= 0; i--) {
            WaitingListItem found = null;
            for (WaitingListItem newWaitingListItem: newWaitingListItems) {
                if (newWaitingListItem.equals(oldWaitingListItems.get(i))) {
                    found = newWaitingListItem;
                    break;
                }
            }
            if (found == null) {
                //Patch edited medications
                for (WaitingListItem newWaitingListItem: newWaitingListItems) {
                    if (newWaitingListItem.getId() == oldWaitingListItems.get(i).getId()) {
                        userWaitingList.updateWaitingListItem(oldWaitingListItems.get(i), oldWaitingListItems.get(i).getId(), userId);
                        found = newWaitingListItem;
                        break;
                    }
                }
            }
            if (found != null) {
                newWaitingListItems.remove(found);
                oldWaitingListItems.remove(i);
            }
        }

        //Delete all medications from the database that are no longer up to date
        for (WaitingListItem waitingListItem: oldWaitingListItems) {
            userWaitingList.removeWaitingListItem(userId, waitingListItem.getId());
        }

        //Upload all new medications
        for (WaitingListItem waitingListItem: newWaitingListItems) {
            userWaitingList.insertWaitingListItem(waitingListItem, userId);
        }
    }

    public List<User> getUsers(Map<String,String> params) throws SQLException{
        // TODO Sort the users before taking the sublist
        ArrayList<User> users = new ArrayList<>();

        String query = buildUserQuery(params);
        System.out.println(query);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            users.add(getUserFromResultSet(resultSet));
        }

        int startIndex = 0;
        if(params.containsKey("startIndex")){
            startIndex = Integer.parseInt(params.get("startIndex"));
        }

        int count = 100;
        if(params.containsKey("count")){
            count = Integer.parseInt(params.get("count"));
        }
        int toIndex = Math.min(startIndex+count, users.size());
        if(startIndex > toIndex){
            return new ArrayList<User>();
        }
        return users.subList(startIndex, Math.min(startIndex+count, users.size()));
    }


    public String buildUserQuery(Map<String,String> params){
        boolean hasWhereClause = false;
        for(String param:params.keySet()){
            if(!param.equals("count") && !param.equals("startIndex")){
                hasWhereClause = true;
            }
        }
        if(!hasWhereClause) {
            return "SELECT * FROM " + currentDatabase + ".USER";
        }

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT * FROM " + currentDatabase + ".USER WHERE ");


        String nameFilter = nameFilter(params);
        String passwordFilter = matchFilter(params, "password", true);

        String userTypeFilter = userTypeFilter(params);

        String ageFilter = ageFilter(params);

        String genderFilter = matchFilter(params, "gender", false);

        String regionFilter = matchFilter(params, "region", false);

        String organFilter = organFilter(params);

        List<String> filters = new ArrayList<String>();
        filters.addAll(Arrays.asList(
                nameFilter,passwordFilter,userTypeFilter,ageFilter,genderFilter,regionFilter,organFilter

        ));

        filters.removeIf((String filter) -> filter.equals(""));

        queryBuilder.append(String.join(" AND ",filters));
        return queryBuilder.toString();
    }

    public String nameFilter(Map<String, String> params){
        List<String> tokenFilters = new ArrayList<>();
        if(params.containsKey("name")){
            for(String token : params.get("name").trim().split(" ")){
                StringBuilder tokenFilter = new StringBuilder();
                tokenFilter.append("(");
                tokenFilter.append("last_name LIKE \'" + token+"%\'" + " OR ");
                tokenFilter.append("middle_names LIKE \'" + token+"%\'" + " OR middle_names LIKE \'" + "% "+token+"%\'" + " OR ");
                tokenFilter.append("first_name LIKE \'" + token+"%\'" + " OR ");

                tokenFilter.append("preferred_name LIKE \'" + token+"%\'" + " OR ");
                tokenFilter.append("preferred_middle_names LIKE \'" + token+"%\'" + " OR preferred_middle_names LIKE \'" + "% "+token+"%\'" + " OR ");
                tokenFilter.append("preferred_last_name LIKE \'" + token+"%\'");
                tokenFilter.append(")");
                tokenFilters.add(tokenFilter.toString());
            }
        }
        tokenFilters.removeIf((String filter) -> filter.equals(""));
        return String.join(" AND ", tokenFilters);
    }
    public String matchFilter(Map<String,String> params, String paramName, Boolean caseSensitive){
        StringBuilder sb = new StringBuilder();
        if(caseSensitive){
            if(params.containsKey(paramName)){
                sb.append(paramName + " = " + params.get(paramName));
            }
        }else{
            if(params.containsKey(paramName)){
                sb.append("LOWER(" + paramName.toLowerCase() + ") = \'" + params.get(paramName).toLowerCase() + "\'");
            }
        }
        return sb.toString();
    }

    public String ageFilter(Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        if(params.containsKey("age")){
            sb.append("DATEDIFF(NOW(),date_of_birth)/365.25 LIKE \'" + params.get("age")+".%\'");
        }
        return sb.toString();
    }

    public String organFilter(Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        if(params.containsKey("organ")){
            sb.append("EXISTS (SELECT * FROM " +
                            currentDatabase + ".DONATION_LIST_ITEM" +
                    " WHERE " +

                            currentDatabase + ".DONATION_LIST_ITEM.user_id = " + currentDatabase + ".USER.id" +
                            " AND " +
                            currentDatabase + ".DONATION_LIST_ITEM.name = \'" + params.get("organ") + "\'" +
                    ")");
        }
        return sb.toString();
    }

    public String userTypeFilter(Map<String, String> params){
        if(params.containsKey("userType")) {
            switch (params.get("userType").toLowerCase()){
                case "neither": return "NOT " + isDonorFilter() + " AND NOT " + isReceiverFilter();
                case "donor":   return isDonorFilter() + " AND NOT " + isReceiverFilter();
                case "receiver":   return "NOT " + isDonorFilter() + " AND " + isReceiverFilter();
                case "both":   return isDonorFilter() + " AND " + isReceiverFilter();
            }
        }
        return "";
    }

    public String isDonorFilter(){
        return "EXISTS (SELECT * FROM " +
                currentDatabase + ".DONATION_LIST_ITEM" +
                " WHERE " +
                currentDatabase + ".DONATION_LIST_ITEM.user_id = " + currentDatabase + ".USER.id)";
    }

    public String isReceiverFilter(){
        return "EXISTS (SELECT * FROM " +
                currentDatabase + ".WAITING_LIST_ITEM" +
                " WHERE " +
                currentDatabase + ".WAITING_LIST_ITEM.user_id = " + currentDatabase + ".USER.id)";
    }


    public User getUserFromId(int id) throws SQLException {
        // SELECT * FROM USER id = id;
        String query = "SELECT * FROM " + currentDatabase + ".USER WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new User Object with the fields from the database
            return getUserFromResultSet(resultSet);
        }
    }

    public void insertUser(User user) throws SQLException{

        String insert = "INSERT INTO " + currentDatabase + ".USER(first_name, middle_names, last_name, preferred_name, preferred_middle_names, preferred_last_name, creation_time, last_modified, username," +
                " email, password, date_of_birth) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, user.getNameArray()[0]);
        statement.setString(2, user.getNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null);
        statement.setString(3, user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null);
        statement.setString(4, user.getPreferredNameArray()[0]);
        statement.setString(5, user.getPreferredNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getPreferredNameArray(), 1, user.getPreferredNameArray().length - 1)) : null);
        statement.setString(6, user.getPreferredNameArray().length > 1 ? user.getPreferredNameArray()[user.getPreferredNameArray().length - 1] : null);
        statement.setTimestamp(7, java.sql.Timestamp.valueOf(user.getCreationTime()));
        statement.setTimestamp(8, java.sql.Timestamp.valueOf(user.getCreationTime()));
        statement.setString(9, user.getUsername());
        statement.setString(10, user.getEmail());
        statement.setString(11, user.getPassword());
        statement.setDate(12, java.sql.Date.valueOf(user.getDateOfBirth()));
        System.out.println("Inserting new user -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public int getIdFromUser(String username) throws SQLException{

        String query = "SELECT id FROM " + currentDatabase + ".USER WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");

    }

    public User getUserFromResultSet(ResultSet resultSet) throws SQLException{


        User user = new User(
                resultSet.getString("first_name"),
                resultSet.getString("middle_names") != null ? resultSet.getString("middle_names").split(",") : null,
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


        String preferredNameString = "";
        preferredNameString += resultSet.getString("preferred_name") + " ";
        if (resultSet.getString("preferred_middle_names") != null) {
            for (String middleName : resultSet.getString("preferred_middle_names").split(",")) {
                preferredNameString += middleName + " ";
            }
        }
        preferredNameString += resultSet.getString("preferred_last_name");

        user.setPreferredNameArray(preferredNameString.split(" "));


        user.setGenderIdentity(resultSet.getString("gender_identity") != null ? Gender.parse(resultSet.getString("gender_identity")) : null);

        if (resultSet.getString("blood_pressure") != null) {
            user.setBloodPressure(resultSet.getString("blood_pressure"));
        } else {
            user.setBloodPressure("");
        }

        if (resultSet.getString("smoker_status") != null) {
            user.setSmokerStatus(SmokerStatus.parse(resultSet.getString("smoker_status")));
        } else {
            user.setSmokerStatus(null);
        }

        if (resultSet.getString("alcohol_consumption") != null) {
            user.setAlcoholConsumption(AlcoholConsumption.parse(resultSet.getString("alcohol_consumption")));
        } else {
            user.setAlcoholConsumption(null);
        }

        //Get all the organs for the given user

        int userId = getIdFromUser(resultSet.getString("username"));
        user.setId(userId);

        String organsQuery = "SELECT * FROM " + currentDatabase + ".DONATION_LIST_ITEM WHERE user_id = ?";
        PreparedStatement organsStatement = connection.prepareStatement(organsQuery);

        organsStatement.setInt(1, userId);
        ResultSet organsResultSet = organsStatement.executeQuery();

        while (organsResultSet.next()) {
            user.getOrgans().add(Organ.parse(organsResultSet.getString("name")));
        }

        //Get all the medications for the given user

        String medicationsQuery = "SELECT * FROM " + currentDatabase + ".MEDICATION WHERE user_id = ?";
        PreparedStatement medicationsStatement = connection.prepareStatement(medicationsQuery);

        medicationsStatement.setInt(1, userId);
        ResultSet medicationsResultSet = medicationsStatement.executeQuery();

        while (medicationsResultSet.next()) {
            String[] activeIngredients = medicationsResultSet.getString("active_ingredients").split(",");
            String[] historyStringList = medicationsResultSet.getString("history").split(",");

            ArrayList<String> historyList = new ArrayList<>();
            //Iterate through the history list to get associated values together
            int counter = 1;
            String historyItem = "";
            for (int i = 0; i < historyStringList.length; i++) {
                if (counter % 2 == 1) {
                    historyItem += historyStringList[i] + ",";
                    counter++;
                } else {
                    historyItem += historyStringList[i];
                    historyList.add(historyItem);
                    historyItem = "";
                    counter++;
                }

            }


            if (historyList.get(historyList.size() - 1).contains("Stopped taking")) { //Medication is historic
                user.getHistoricMedications().add(new Medication(
                        medicationsResultSet.getString("name"),
                        activeIngredients,
                        historyList,
                        medicationsResultSet.getInt("id")
                ));
            } else { //Medication is Current
                user.getCurrentMedications().add(new Medication(
                        medicationsResultSet.getString("name"),
                        activeIngredients,
                        historyList,
                        medicationsResultSet.getInt("id")
                ));
            }
        }


        //Get all the procedures for the given user

        String proceduresQuery = "SELECT * FROM " + currentDatabase + ".PROCEDURES WHERE user_id = ?";
        PreparedStatement proceduresStatement = connection.prepareStatement(proceduresQuery);

        proceduresStatement.setInt(1, userId);
        ResultSet proceduresResultSet = proceduresStatement.executeQuery();

        while (proceduresResultSet.next()) {

            if (proceduresResultSet.getDate("date").toLocalDate().isAfter(LocalDate.now())) {
                ArrayList<Organ> procedureOrgans = new ArrayList<>();
                for (String organ : proceduresResultSet.getString("organs_affected").split(",")) {
                    procedureOrgans.add(Organ.parse(organ));
                }
                user.getPendingProcedures().add(new Procedure(
                        proceduresResultSet.getString("summary"),
                        proceduresResultSet.getString("description"),
                        proceduresResultSet.getDate("date").toLocalDate(),
                        procedureOrgans,
                        proceduresResultSet.getInt("id")
                ));
            } else {
                ArrayList<Organ> procedureOrgans = new ArrayList<>();
                for (String organ : proceduresResultSet.getString("organs_affected").split(",")) {
                    procedureOrgans.add(Organ.parse(organ));
                }
                user.getPreviousProcedures().add(new Procedure(
                        proceduresResultSet.getString("summary"),
                        proceduresResultSet.getString("description"),
                        proceduresResultSet.getDate("date").toLocalDate(),
                        procedureOrgans,
                        proceduresResultSet.getInt("id")
                ));
            }

        }

        //Get all the diseases for the given user

        String diseasesQuery = "SELECT * FROM " + currentDatabase + ".DISEASE WHERE user_id = ?";
        PreparedStatement diseasesStatement = connection.prepareStatement(diseasesQuery);

        diseasesStatement.setInt(1, userId);
        ResultSet diseasesResultSet = diseasesStatement.executeQuery();

        while (diseasesResultSet.next()) {

            if (diseasesResultSet.getBoolean("is_cured")) {
                user.getCuredDiseases().add(new Disease(
                        diseasesResultSet.getString("name"),
                        diseasesResultSet.getDate("diagnosis_date").toLocalDate(),
                        diseasesResultSet.getBoolean("is_chronic"),
                        diseasesResultSet.getBoolean("is_cured"),
                        diseasesResultSet.getInt("id")
                ));
            } else {
                user.getCurrentDiseases().add(new Disease(
                        diseasesResultSet.getString("name"),
                        diseasesResultSet.getDate("diagnosis_date").toLocalDate(),
                        diseasesResultSet.getBoolean("is_chronic"),
                        diseasesResultSet.getBoolean("is_cured"),
                        diseasesResultSet.getInt("id")
                ));
            }

        }

        //Get all waiting list items from database
        String waitingListQuery = "SELECT * FROM " + currentDatabase + ".WAITING_LIST_ITEM WHERE user_id = ?";
        PreparedStatement waitingListStatement = connection.prepareStatement(waitingListQuery);
        waitingListStatement.setInt(1, userId);
        ResultSet waitingListResultSet = waitingListStatement.executeQuery();


        while (waitingListResultSet.next()) {
            Organ organ = Organ.parse(waitingListResultSet.getString("organ_type"));
            LocalDate registeredDate = waitingListResultSet.getDate("organ_registered_date").toLocalDate();
            LocalDate deregisteredDate = waitingListResultSet.getDate("organ_deregistered_date") != null ? waitingListResultSet.getDate("organ_deregistered_date").toLocalDate() : null;
            int waitinguserId = waitingListResultSet.getInt("user_id");
            int deregisteredCode = waitingListResultSet.getInt("deregistered_code");
            int waitingListId = waitingListResultSet.getInt("id");

            user.getWaitingListItems().add(new WaitingListItem(organ, registeredDate, waitingListId, waitinguserId, deregisteredDate, waitingListId));
        }
        return user;
    }

    public void updateUserAttributes(User user, int userId) throws SQLException {
        //Attributes update
        String update = "UPDATE " + currentDatabase + ".USER SET first_name = ?, middle_names = ?, last_name = ?, preferred_name = ?," +
                " preferred_middle_names = ?, preferred_last_name = ?, current_address = ?, " +
                "region = ?, date_of_birth = ?, date_of_death = ?, height = ?, weight = ?, blood_pressure = ?, " +
                "gender = ?, gender_identity = ?, blood_type = ?, smoker_status = ?, alcohol_consumption = ?, username = ?, email = ?, password = ? " +
                "WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getNameArray()[0]);
        statement.setString(2, user.getNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null);
        statement.setString(3, user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null);

        statement.setString(4, user.getPreferredNameArray()[0]);
        statement.setString(5, user.getPreferredNameArray().length > 2 ?
                String.join(",", Arrays.copyOfRange(user.getPreferredNameArray(), 1, user.getPreferredNameArray().length - 1)) : null);
        statement.setString(6, user.getPreferredNameArray().length > 1 ? user.getPreferredNameArray()[user.getPreferredNameArray().length - 1] : null);

        statement.setString(7, user.getCurrentAddress());
        statement.setString(8, user.getRegion());
        statement.setDate(9, java.sql.Date.valueOf(user.getDateOfBirth()));
        statement.setDate(10, user.getDateOfDeath() != null ? java.sql.Date.valueOf(user.getDateOfDeath()) : null);
        statement.setDouble(11, user.getHeight());
        statement.setDouble(12, user.getWeight());
        statement.setString(13, user.getBloodPressure());
        statement.setString(14, user.getGender() != null ? user.getGender().toString() : null);
        statement.setString(15, user.getGenderIdentity() != null ? user.getGenderIdentity().toString() : null);
        statement.setString(16, user.getBloodType() != null ? user.getBloodType().toString() : null);
        statement.setString(17, user.getSmokerStatus() != null ? user.getSmokerStatus().toString() : null);
        statement.setString(18, user.getAlcoholConsumption() != null ? user.getAlcoholConsumption().toString() : null);
        statement.setString(19, user.getUsername());
        statement.setString(20, user.getEmail());
        statement.setString(21, user.getPassword());
        statement.setInt(22, userId);
        System.out.println("Update User Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());


/*        int userId = getUserId(user.getUsername());

        //Organ Updates
        //First get rid of all the users organs in the table
        String deleteOrgansQuery = "DELETE FROM " + currentDatabase + ".DONATION_LIST_ITEM WHERE user_id = ?";
        PreparedStatement deleteOrgansStatement = connection.prepareStatement(deleteOrgansQuery);
        deleteOrgansStatement.setInt(1, userId);
        System.out.println("Organ rows deleted: " + deleteOrgansStatement.executeUpdate());

        int totalAdded = 0;
        //Then repopulate it with the new updated organs
        for (Organ organ: user.getOrgans()) {
            String insertOrgansQuery = "INSERT INTO " + currentDatabase + ".DONATION_LIST_ITEM (name, user_id) VALUES (?, ?)";
            PreparedStatement insertOrgansStatement = connection.prepareStatement(insertOrgansQuery);
            insertOrgansStatement.setString(1, organ.toString());
            insertOrgansStatement.setInt(2, userId);
            totalAdded += insertOrgansStatement.executeUpdate();
        }
        System.out.println("Update User Organ Donations -> Successful -> Rows Updated: " + totalAdded);*/
    }

    public void removeUser(User user) throws SQLException {
        String update = "DELETE FROM " + currentDatabase + ".USER WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setString(1, user.getUsername());
        System.out.println("Deletion of User: " + user.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }
}
