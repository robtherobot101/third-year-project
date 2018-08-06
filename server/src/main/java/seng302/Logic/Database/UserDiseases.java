package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Disease;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDiseases {

    public ArrayList<Disease> getAllDiseases(int userId) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Disease> allDiseases = new ArrayList<>();
            String query = "SELECT * FROM DISEASE WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allDiseases.add(getDiseaseFromResultSet(resultSet));
            }
            return allDiseases;
        }
    }

    public void insertDisease(Disease disease, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insertDiseasesQuery = "INSERT INTO DISEASE (name, diagnosis_date, is_cured, is_chronic, user_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertDiseasesStatement = connection.prepareStatement(insertDiseasesQuery);

            insertDiseasesStatement.setString(1, disease.getName());
            insertDiseasesStatement.setDate(2, java.sql.Date.valueOf(disease.getDiagnosisDate()));
            insertDiseasesStatement.setBoolean(3, disease.isCured());
            insertDiseasesStatement.setBoolean(4, disease.isChronic());
            insertDiseasesStatement.setInt(5, userId);

            System.out.println("Inserting new disease  -> Successful -> Rows Added: " + insertDiseasesStatement.executeUpdate());
        }
    }

    public Disease getDiseaseFromId(int diseaseId, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM DISEASE id = id;
            String query = "SELECT * FROM DISEASE WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, diseaseId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a indication that fields arent empty
                return getDiseaseFromResultSet(resultSet);
            }
        }
    }

    public void updateDisease(Disease disease, int diseaseId, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "UPDATE DISEASE SET name = ?, diagnosis_date = ?, is_cured = ?, is_chronic = ? WHERE user_id = ? AND id = ?";
            PreparedStatement statement = connection.prepareStatement(update);

            statement.setString(1, disease.getName());
            statement.setDate(2, java.sql.Date.valueOf(disease.getDiagnosisDate()));
            statement.setBoolean(3, disease.isCured());
            statement.setBoolean(4, disease.isChronic());
            statement.setInt(5, userId);
            statement.setInt(6, diseaseId);
            System.out.println("Update Disease - ID: " + diseaseId + " USERID: " + userId + " -> Successful -> Rows Updated: " + statement.executeUpdate());
        }
    }

    public void removeDisease(int userId, int diseaseId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM DISEASE WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setInt(1, diseaseId);
            statement.setInt(2, userId);
            System.out.println("Deletion of Disease - ID: " + diseaseId + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    public Disease getDiseaseFromResultSet(ResultSet diseasesResultSet) throws SQLException {
        return new Disease(
                diseasesResultSet.getString("name"),
                diseasesResultSet.getDate("diagnosis_date").toLocalDate(),
                diseasesResultSet.getBoolean("is_chronic"),
                diseasesResultSet.getBoolean("is_cured"),
                diseasesResultSet.getInt("id")
        );
    }

    /**
     * Replace a user's diseases on the database with a new set of diseases.
     *
     * @param newDiseases The list of diseases to replace the old one with
     * @param userId The id of the user to replace diseases of
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllDiseases(List<Disease> newDiseases, int userId) throws SQLException {
        List<Disease> oldDiseases = getAllDiseases(userId);

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
                        updateDisease(oldDiseases.get(i), oldDiseases.get(i).getId(), userId);
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
            removeDisease(userId, disease.getId());
        }

        //Upload all new medications
        for (Disease disease: newDiseases) {
            insertDisease(disease, userId);
        }
    }
}
