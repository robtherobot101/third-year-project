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

    public void insertOrgan(DonatableOrgan donatableOrgan) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()){
            String query = "INSERT INTO ORGANS (organType, dateOfDeath, donor) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, donatableOrgan.getOrganType().toString());
            statement.setLong(2, donatableOrgan.getDateOfDeath().toEpochSecond(ZoneOffset.ofHours(+12)));
            statement.setLong(3, donatableOrgan.getDonorId());

            System.out.println("Inserting new organ  -> Successful -> Rows Added: " + statement.executeUpdate());
        }
    }


    private DonatableOrgan getOrganFromResultSet(ResultSet organResultSet) throws SQLException{
        return new DonatableOrgan(
                Instant.ofEpochMilli(organResultSet.getLong("dateOfDeath")).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                Organ.parse(organResultSet.getString("organType")),
                organResultSet.getLong("donor"));
    }
}
