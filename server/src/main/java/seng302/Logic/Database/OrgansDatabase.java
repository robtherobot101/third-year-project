package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;
import seng302.Model.DonatableOrgan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class OrgansDatabase {

    /**
     * gets all the organs from the database
     * @return returns a list of all the organs in the database
     * @throws SQLException throws if cannot connect to the database
     */
    public List<DonatableOrgan> getAllOrgans() throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            List<DonatableOrgan> allOrgans = new ArrayList<>();
            String query = "SELECT * FROM ORGANS";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                allOrgans.add(getOrganFromResultSet(resultSet));
            }
            return allOrgans;
        }
    }

    /**
     * inserts a new organ into the database
     * @param donatableOrgan the donatable organ to insert into the database
     * @throws SQLException throws if cannot connect to the database
     */
    public void insertOrgan(DonatableOrgan donatableOrgan) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()){
            String query = "INSERT INTO ORGANS (organType, dateOfDeath, donor) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, donatableOrgan.getOrganType().toString());
            statement.setLong(2, donatableOrgan.getDateOfDeath().atZone(ZoneId.systemDefault()).toEpochSecond());
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
            String query = "DELETE FROM ORGANS WHERE organType = ? AND donor = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, donatableOrgan.getOrganType().toString());
            statement.setLong(2, donatableOrgan.getDonorId());

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
            String query = "UPDATE ORGANS SET dateOfDeath = ? WHERE organType = ? AND donor = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, donatableOrgan.getDateOfDeath().atZone(ZoneId.systemDefault()).toEpochSecond());
            statement.setString(2, donatableOrgan.getOrganType().toString());
            statement.setLong(3, donatableOrgan.getDonorId());

            System.out.println("Update of Organ - organType: "
                    + donatableOrgan.getOrganType().toString() +
                    " donor: " + donatableOrgan.getDonorId() +
                    " dateOfDeath: "+ donatableOrgan.getDateOfDeath() +
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
        return new DonatableOrgan(
                Instant.ofEpochMilli(organResultSet.getLong("dateOfDeath")).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                Organ.parse(organResultSet.getString("organType")),
                organResultSet.getLong("donor"));
    }
}
