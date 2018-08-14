package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;
import seng302.Model.DonatableOrgan;
import seng302.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrgansDatabase {

    /**
     * gets all the organs from the database
     * @return returns a list of all the organs in the database
     * @throws SQLException throws if cannot connect to the database
     */
    public List<DonatableOrgan> getAllDonatableOrgans() throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            List<DonatableOrgan> allOrgans = new ArrayList<>();
            String query = "SELECT * FROM DONATION_LIST_ITEM WHERE timeOfDeath IS NOT NULL and expired=0";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                allOrgans.add(getOrganFromResultSet(resultSet));
            }
            return allOrgans;
        }
    }


    /**
     * gets all the organs from the database
     * @return returns a list of all the organs in the database
     * @throws SQLException throws if cannot connect to the database
     */
    public List<DonatableOrgan> queryOrgans(Map<String, String> params) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            List<DonatableOrgan> allOrgans = new ArrayList<>();
            String query = buildOrganQuery(params);
            System.out.println(query);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                allOrgans.add(getOrganFromResultSet(resultSet));
            }
            return allOrgans;
        }
    }

    private String buildOrganQuery(Map<String, String> params) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM DONATION_LIST_ITEM JOIN USER ON DONATION_LIST_ITEM.user_id = USER.id WHERE timeOfDeath IS NOT NULL ");
        if(params.keySet().contains("userRegion")) {
            queryBuilder.append("AND USER.regionOfDeath = '").append(params.get("userRegion")).append("' ");
        }

        if(params.keySet().contains("organ")) {
            queryBuilder.append("AND DONATION_LIST_ITEM.name = '").append(params.get("organ")).append("' ");
        }
        return queryBuilder.toString();
    }

    /**
     * inserts a new organ into the database
     * @param donatableOrgan the donatable organ to insert into the database
     * @throws SQLException throws if cannot connect to the database
     */
    public void insertOrgan(DonatableOrgan donatableOrgan) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()){
            String query = "INSERT INTO DONATION_LIST_ITEM (name, timeOfDeath, user_id) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, donatableOrgan.getOrganType().toString());
            statement.setLong(2, donatableOrgan.getTimeOfDeath().atZone(ZoneId.systemDefault()).toEpochSecond());
            statement.setLong(3, donatableOrgan.getDonorId());

            System.out.println("Inserting new organ  -> Successful -> Rows Added: " + statement.executeUpdate());
        }
    }

    /**
     * removes a given DonatableOrgan from the database
     * @param donatableOrgan the donatableOgran to remove
     * @throws SQLException throws if cannot connect to database
     */
    public void removeOrgan(DonatableOrgan donatableOrgan) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()){
            String query = "DELETE FROM DONATION_LIST_ITEM WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, donatableOrgan.getId());

            System.out.println("Deletion of Organ - organType: "
                    + donatableOrgan.getOrganType().toString() +
                    " donor: " + donatableOrgan.getDonorId() +
                    " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    /**
     * updates a the date of death of donatable organ in the database to the new date of death of the donatable organ
     * @param donatableOrgan the donatable organ to update
     * @throws SQLException throws if cannot connect to database
     */
    public void updateOrgan(DonatableOrgan donatableOrgan) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()){
            String query = "UPDATE DONATION_LIST_ITEM SET timeOfDeath = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, donatableOrgan.getTimeOfDeath().atZone(ZoneId.systemDefault()).toEpochSecond());
            statement.setInt(2, donatableOrgan.getId());

            System.out.println("Update of Organ - organType: "
                    + donatableOrgan.getOrganType().toString() +
                    " donor: " + donatableOrgan.getDonorId() +
                    " dateOfDeath: "+ donatableOrgan.getTimeOfDeath() +
                    " -> Successful -> Rows Updated: " + statement.executeUpdate());
        }
    }

    /**
     * gets a DonatableOrgan object from a result set
     * @param organResultSet the result set to get the Donatable organ from
     * @return returns a donatable organ
     * @throws SQLException throws if cannot reach the resultSet
     */
    private DonatableOrgan getOrganFromResultSet(ResultSet organResultSet) throws SQLException{
        boolean expired = true;
        if (organResultSet.getInt("expired") == 0){
            expired = false;
        }
        return new DonatableOrgan(
                //organResultSet.getTimestamp("timeOfDeath") != null ? organResultSet.getTimestamp("timeOfDeath" ).toLocalDateTime() : null,
                LocalDateTime.ofEpochSecond(organResultSet.getLong("timeOfDeath"),0, ZoneOffset.ofHours(+12)),
                Organ.parse(organResultSet.getString("name")),
                organResultSet.getLong("user_id"),
                organResultSet.getInt("id"),
                expired);
    }
}
