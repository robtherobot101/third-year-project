package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.*;
import seng302.Model.*;
import seng302.Model.Medication.Medication;
import seng302.Server;

import java.sql.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class GeneralUser extends DatabaseMethods {

    /**
     * Update a user's attributes, medications, procedures, diseases, organ donations, waiting list items, and history.
     *
     * @param user The user to update
     * @param userId The id of the user to update
     * @param canEditClinicianAttributes Whether the user has access to edit medications procedures etc
     * @throws SQLException If there is errors communicating with the database
     */
    public void patchEntireUser(User user, int userId, boolean canEditClinicianAttributes) throws SQLException {
        updateUserAttributes(user, userId);

        new UserDonations().updateAllDonations(new HashSet<Organ>(user.getOrgans()), userId, user.getDateOfDeath());

        new UserHistory().updateHistory(user.getUserHistory(), userId);

        if (!canEditClinicianAttributes) {
            return;
        }

        List<Medication> newMedications = new ArrayList<>();
        newMedications.addAll(user.getCurrentMedications());
        newMedications.addAll(user.getHistoricMedications());
        new UserMedications().updateAllMedications(newMedications, userId);

        List<Procedure> newProcedures = new ArrayList<>();
        newProcedures.addAll(user.getPendingProcedures());
        newProcedures.addAll(user.getPreviousProcedures());
        new UserProcedures().updateAllProcedures(newProcedures, userId);

        List<Disease> newDiseases = new ArrayList<>();
        newDiseases.addAll(user.getCuredDiseases());
        newDiseases.addAll(user.getCurrentDiseases());
        new UserDiseases().updateAllDiseases(newDiseases, userId);

        new UserWaitingList().updateAllWaitingListItems(new ArrayList<>(user.getWaitingListItems()), userId);
    }

    /**
     * Returns a List of Users based on the params given. Check the endpoint definition on the wiki
     * for an explanation of these
     * @param params The given params
     * @return The List of matched users
     * @throws SQLException If there are issues working with the database
     */
    public List<User> getUsers(Map<String,String> params) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // TODO Sort the users before taking the sublist
            ArrayList<User> users = new ArrayList<>();

            String query = buildUserQuery(params);
            System.out.println(query);
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(getUserFromResultSet(resultSet));
            }
            return users;
        }
        finally {
            close();
        }
    }


    /**
     * Builds a query string by putting together filters
     * @param params The query parameters which are defined on the wiki
     * @return The query String
     */
    public String buildUserQuery(Map<String,String> params){
        boolean hasWhereClause = false;
        for(String param:params.keySet()){
            if(!param.equals("count") && !param.equals("startIndex")){
                hasWhereClause = true;
            }
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM USER");
        if (hasWhereClause) {
            queryBuilder.append(" WHERE ");

            String nameFilter = nameFilter(params);
            String passwordFilter = matchFilter(params, "password", true);
            String userTypeFilter = userTypeFilter(params);
            String ageFilter = ageFilter(params);
            String genderFilter = matchFilter(params, "gender", false);
            String regionFilter = matchFilter(params, "region", false);
            String countryFilter = matchFilter(params, "country", false);
            String organFilter = organFilter(params);

            List<String> filters = new ArrayList<String>();
            filters.addAll(Arrays.asList(
                    nameFilter,passwordFilter,userTypeFilter,ageFilter,genderFilter,regionFilter,countryFilter,organFilter
            ));

            filters.removeIf((String filter) -> filter.equals(""));

            queryBuilder.append(String.join(" AND ",filters));
        }

        int startIndex = 0;
        if (params.containsKey("startIndex")) {
            startIndex = Integer.parseInt(params.get("startIndex"));
        }

        int count = 20;
        if (params.containsKey("count")) {
            count = Integer.parseInt(params.get("count"));
        }

        queryBuilder.append(" LIMIT ");
        queryBuilder.append(startIndex);
        queryBuilder.append(",");
        queryBuilder.append(startIndex+count);

        return queryBuilder.toString();
    }

    /**
     * Constructs a portion of an SQL statement which finds users by name based off of the query params.
     * @param params The query parameters
     * @return A string containing part of the query string
     */
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

    /**
     * Constructs a portion of an SQL statement which finds users by matching the field defined by paramName.
     * If caseSensitive is false the matches do not need to be of the same case.
     * @param params The query params
     * @param paramName The name of the field to match
     * @param caseSensitive Whether or not to match case-sensitive
     * @return A string containing part of the query string
     */
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

    /**
     * Constructs a portion of an SQL statement which matches users by age.
     * @param params The query params
     * @return A string containing part of the query string
     */
    public String ageFilter(Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        if(params.containsKey("age")){
            sb.append("DATEDIFF(NOW(),date_of_birth)/365.25 LIKE \'" + params.get("age")+".%\'");
        }
        return sb.toString();
    }

    /**
     * Constructs a portion of an SQL statement which matches users by whether or not they are donating the Organ defined in the params.
     * @param params The query params
     * @return A string containing part of the query string
     */
    public String organFilter(Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        if(params.containsKey("organ")){
            sb.append("EXISTS (SELECT * FROM DONATION_LIST_ITEM" +
                    " WHERE DONATION_LIST_ITEM.user_id = USER.id" +
                            " AND DONATION_LIST_ITEM.name = \'" + params.get("organ") + "\'" +
                    ")");
        }
        return sb.toString();
    }

    /**
     * Constructs a portion of an SQL statement which matches users by which type of user they are ('neither', 'donor', 'receiver', or 'both').
     * @param params The query params
     * @return A string containing part of the query string
     */
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

    /**
     * Constructs part of an SQL statement which matches users by whether or not they are a 'donor'
     * @return A string containing part of the query string
     */
    public String isDonorFilter(){
        return "EXISTS (SELECT * FROM DONATION_LIST_ITEM" +
                " WHERE DONATION_LIST_ITEM.user_id = USER.id)";
    }

    /**
     * Constructs part of an SQL statement which matches users by whether or not they are a 'receiver'
     * @return A string containing part of the query string
     */
    public String isReceiverFilter(){
        return "EXISTS (SELECT * FROM WAITING_LIST_ITEM" +
                " WHERE WAITING_LIST_ITEM.user_id = USER.id)";
    }


    /**
     * Returns the user whose id matches the given id
     * @param id The given id
     * @return The matched user
     * @throws SQLException If there is a problem working with the database.
     */
    public User getUserFromId(int id) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM USER id = id;
            String query = "SELECT * FROM USER WHERE id = ?";
            statement = connection.prepareStatement(query);

            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new user Object with the fields from the database
                return getUserFromResultSet(resultSet);
            }
        }
        finally {
            close();
        }
    }

    private String createUserStatement(User user) {
        StringBuilder sb = new StringBuilder();

            String firstName = user.getNameArray()[0];
            String middleNames = user.getNameArray().length > 2 ?
            String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null;
            String lastName = user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null;
            String preferredName = user.getPreferredNameArray()[0];
            String preferredMiddleNames = user.getPreferredNameArray().length > 2 ?
                    String.join(",", Arrays.copyOfRange(user.getPreferredNameArray(), 1, user.getPreferredNameArray().length - 1)) : null;
            String preferredLastName = user.getPreferredNameArray().length > 1 ? user.getPreferredNameArray()[user.getPreferredNameArray().length - 1] : null;
            LocalDateTime creationTime = user.getCreationTime();
            LocalDateTime lastModified = user.getCreationTime();
            String username = user.getUsername();
            String email = user.getEmail();
            String password = user.getPassword();
            String dateOfBirth = java.sql.Date.valueOf(user.getDateOfBirth()).toString();

        return   MessageFormat.format("INSERT INTO USER(first_name, middle_names, last_name, preferred_name, preferred_middle_names, preferred_last_name, creation_time, last_modified, username," +
                " email, password, date_of_birth) VALUES(\"{0}\", {1}, {2}, \"{3}\", {4}, {5}, \"{6}\", \"{7}\", \"{8}\", \"{9}\", \"{10}\", \"{11}\")",
                firstName, middleNames == null ? null : "\"" + middleNames +"\"", lastName == null ? null : "\"" + lastName + "\"", preferredName, preferredMiddleNames == null ? null : "\"" + preferredMiddleNames + "\"",
                preferredLastName == null ? null : "\"" + preferredLastName + "\"", creationTime, lastModified, username, email, password, dateOfBirth);
    }

    /**
     * Inserts the given user into the database.
     * @param user The given user which will be inserted.
     * @throws SQLException If there is a problem working with the database.
     */
    public void insertUser(User user) throws SQLException{
        User fromDb;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
//            String insert = "INSERT INTO USER(first_name, middle_names, last_name, preferred_name, preferred_middle_names, preferred_last_name, creation_time, last_modified, username," +
//                    " email, password, date_of_birth) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//            PreparedStatement statement = connection.prepareStatement(insert);
//            statement.setString(1, user.getNameArray()[0]);
//            statement.setString(2, user.getNameArray().length > 2 ?
//                    String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null);
//            statement.setString(3, user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null);
//            statement.setString(4, user.getPreferredNameArray()[0]);
//            statement.setString(5, user.getPreferredNameArray().length > 2 ?
//                    String.join(",", Arrays.copyOfRange(user.getPreferredNameArray(), 1, user.getPreferredNameArray().length - 1)) : null);
//            statement.setString(6, user.getPreferredNameArray().length > 1 ? user.getPreferredNameArray()[user.getPreferredNameArray().length - 1] : null);
//            statement.setTimestamp(7, java.sql.Timestamp.valueOf(user.getCreationTime()));
//            statement.setTimestamp(8, java.sql.Timestamp.valueOf(user.getCreationTime()));
//            statement.setString(9, user.getUsername());
//            statement.setString(10, user.getEmail());
//            statement.setString(11, user.getPassword());
//            statement.setDate(12, java.sql.Date.valueOf(user.getDateOfBirth()));
            statement = connection.prepareStatement(createUserStatement(user));
            System.out.println("Inserting new user -> Successful -> Rows Added: " + statement.executeUpdate());

            statement = connection.prepareStatement("SELECT * FROM USER WHERE (username = ? OR email = ?) AND password = ?");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new SQLException("Could not fetch user directly after insertion.");
            } else {
                GeneralUser generalUser = new GeneralUser();
                fromDb = generalUser.getUserFromResultSet(resultSet);
            }
        }
        finally {
            close();
        }
        patchEntireUser(user, (int)fromDb.getId(), false);

    }


    private String getSingleUserStatement(User user) {
        return String.format("INSERT INTO USER(first_name, middle_names, last_name, preferred_name, preferred_middle_names, preferred_last_name, creation_time, last_modified, username," +
                " email, password, date_of_birth) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                user.getNameArray()[0],
                (user.getNameArray().length > 2 ?
                        String.join(",", Arrays.copyOfRange(user.getNameArray(), 1, user.getNameArray().length - 1)) : null),
                (user.getNameArray().length > 1 ? user.getNameArray()[user.getNameArray().length - 1] : null),
                (user.getPreferredNameArray()[0]),
                (user.getPreferredNameArray().length > 2 ?
                        String.join(",", Arrays.copyOfRange(user.getPreferredNameArray(), 1, user.getPreferredNameArray().length - 1)) : null),
                (user.getPreferredNameArray().length > 1 ? user.getPreferredNameArray()[user.getPreferredNameArray().length - 1] : null),
                (java.sql.Timestamp.valueOf(user.getCreationTime())),
                (java.sql.Timestamp.valueOf(user.getCreationTime())),
                (user.getUsername()),
                (user.getEmail()),
                (user.getPassword()),
                (java.sql.Date.valueOf(user.getDateOfBirth()))
        );
    }

    /**
     * Inserts the given users into the database.
     * @param userList The given users which will be inserted.
     * @throws SQLException If there is a problem working with the database.
     */
    public void insertUsers(List<User> userList) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            stmt = connection.createStatement();
            connection.setAutoCommit(false);

            for (User foundUser: userList) {

                stmt.addBatch(getSingleUserStatement(foundUser));
            }

            try {
                // Batch is ready, execute it to insert the data
                stmt.executeBatch();
                connection.commit();
            } catch (BatchUpdateException e) {
                System.out.println("Error message: " + e.getMessage());

                int failedInserts = 0;
                for (int i = 0; i < e.getUpdateCounts().length; i++ ) {
                    if (e.getUpdateCounts()[i] == Statement.EXECUTE_FAILED) {
                        failedInserts++;
                        Server.getInstance().log.debug("Failed to execute record at: " + i);
                    }
                }
                Server.getInstance().log.debug("Total failed inserts: " + failedInserts);
            }

        }
        finally {
            close();
        }
    }

    /**
     * Returns the id of the user whose username matches the one given.
     * @param username The givcen username
     * @return the id of the matched user.
     * @throws SQLException If there is a problem working with the database.
     */
    public int getIdFromUser(String username) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT id FROM USER WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        }
        finally {
            close();
        }
    }

    /**
     * Takes a resultSet, pulls out a user instance, and returns it.
     * @param resultSet The given resultSet
     * @return The user
     * @throws SQLException If there is a problem working with the database.
     */
    public User getUserFromResultSet(ResultSet resultSet) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {

            User user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("middle_names") != null ? resultSet.getString("middle_names").split(",") : null,
                    resultSet.getString("last_name"),
                    resultSet.getDate("date_of_birth").toLocalDate(),
                    resultSet.getTimestamp("date_of_death") != null ? resultSet.getTimestamp("date_of_death").toLocalDateTime() : null,
                    resultSet.getString("gender") != null ? Gender.parse(resultSet.getString("gender")) : null,
                    resultSet.getDouble("height"),
                    resultSet.getDouble("weight"),
                    resultSet.getString("blood_type") != null ? BloodType.parse(resultSet.getString("blood_type")) : null,
                    resultSet.getString("region"),
                    resultSet.getString("current_address"),
                    resultSet.getString("username"),
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getString("country"),
                    resultSet.getString("cityOfDeath"),
                    resultSet.getString("regionOfDeath"),
                    resultSet.getString("countryOfDeath"),
                    resultSet.getString("profile_image_type"));
            user.setLastModifiedForDatabase(resultSet.getTimestamp("last_modified").toLocalDateTime());
            user.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());

            int userId = (int)user.getId();


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
            user.getOrgans().addAll(new UserDonations().getAllUserDonations(userId));

            //Get all the medications for the given user
            List<Medication> medications = new UserMedications().getAllMedications(userId);

            for (Medication medication : medications) {

                if (medication.getHistory().size() > 0 && medication.getHistory().get(medication.getHistory().size() - 1).contains("Stopped taking")) { //Medication is historic
                    user.getHistoricMedications().add(medication);
                } else { //Medication is Current
                    user.getCurrentMedications().add(medication);
                }
            }


            //Get all the procedures for the given user
            List<Procedure> procedures = new UserProcedures().getAllProcedures(userId);

            for(Procedure procedure : procedures) {
                if (procedure.getDate().isAfter(LocalDate.now())) {
                    user.getPendingProcedures().add(procedure);
                } else {
                    user.getPreviousProcedures().add(procedure);
                }
            }

            //Get all the diseases for the given user
            List<Disease> diseases = new UserDiseases().getAllDiseases(userId);
            for (Disease disease : diseases) {
                if (disease.isCured()) {
                    user.getCuredDiseases().add(disease);
                } else {
                    user.getCurrentDiseases().add(disease);
                }
            }

            //Get all waiting list items from database
            List<WaitingListItem> waitingListItems = new UserWaitingList().getAllUserWaitingListItems(userId);
            user.getWaitingListItems().addAll(waitingListItems);


            //Get all history items from database
            List<HistoryItem> historyItems = new UserHistory().getAllHistoryItems(userId);
            user.getUserHistory().addAll(historyItems);

            return user;
        }
    }

    /**
     * Updates the attributes of the user on the database whose id matches the one given with the attributes of the given user.
     * @param user The given user
     * @param userId The given id
     * @throws SQLException If there is a problem working with the database.
     */
    public void updateUserAttributes(User user, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {

            //Attributes update
            String update = "UPDATE USER SET first_name = ?, middle_names = ?, last_name = ?, preferred_name = ?," +
                    " preferred_middle_names = ?, preferred_last_name = ?, current_address = ?, " +
                    "region = ?, date_of_birth = ?, date_of_death = ?, height = ?, weight = ?, blood_pressure = ?, " +
                    "gender = ?, gender_identity = ?, blood_type = ?, smoker_status = ?, alcohol_consumption = ?, username = ?, " +
                    "email = ?, password = ?, country = ? , cityOfDeath = ?, regionOfDeath = ?, countryOfDeath = ? " +
                    "WHERE id = ?";
            statement = connection.prepareStatement(update);
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
            statement.setTimestamp(10, user.getDateOfDeath() != null ? java.sql.Timestamp.valueOf(user.getDateOfDeath()) : null);
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
            statement.setString(22, user.getCountry());
            statement.setString(23, user.getCityOfDeath());
            statement.setString(24, user.getRegionOfDeath());
            statement.setString(25, user.getCountryOfDeath());
            statement.setInt(26, userId);
            System.out.println("Update user Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());
        }
        finally {
            close();
        }
    }

    /**
     * removes the user from the database whoe ID matches that of the user given
     * @param user The given user
     * @throws SQLException If there is a problem working with the database.
     */
    public void removeUser(User user) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM USER WHERE id = ?";
            statement = connection.prepareStatement(update);
            statement.setLong(1, user.getId());
            System.out.println("Deletion of user: " + user.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
        finally {
            close();
        }
    }

    /**
     * Returns the number of Users in the database.
     * @return The number of Users in the database
     * @throws SQLException If there is a problem working with the database.
     */
    public int countUsers() throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "SELECT count(*) AS count FROM USER";
            statement = connection.prepareStatement(update);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("count");
        }
        finally {
            close();
        }
    }

    /**
     * gets a list of user that are waiting for th given organ
     * @param organ the organ to match with
     * @return returns a list of users
     * @throws SQLException If there is a problem working with the database.
     */
    public List<User> getMatchingUsers(DonatableOrgan organ) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()){
            ArrayList<User> possibleMatches = new ArrayList<>();
            String query = "SELECT * FROM USER JOIN WAITING_LIST_ITEM ON WAITING_LIST_ITEM.user_id = USER.id WHERE WAITING_LIST_ITEM.organ_type = ? AND USER.date_of_death is NULL AND WAITING_LIST_ITEM.deregistered_code = 0";
            statement = connection.prepareStatement(query);
            statement.setString(1, organ.getOrganType().toString());
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                possibleMatches.add(getUserFromResultSet(resultSet));
            }
            return possibleMatches;
        }
        finally {
            close();
        }
    }

    public void importUsers(List<User> users) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            for (User user : users) {
                stmt.addBatch(createUserStatement(user));
            }
            stmt.executeBatch();
        }
        finally {
            close();
        }
    }
}
