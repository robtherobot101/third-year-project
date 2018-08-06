package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Medication.Medication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMedications {

    public ArrayList<Medication> getAllMedications(int userId) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Medication> allMedications = new ArrayList<>();
            String query = "SELECT * FROM MEDICATION WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allMedications.add(getMedicationFromResultSet(resultSet));
            }
            return allMedications;
        }
    }

    public void insertMedication(Medication medication, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insertMedicationsQuery = "INSERT INTO MEDICATION (name, active_ingredients, history, user_id) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement insertMedicationsStatement = connection.prepareStatement(insertMedicationsQuery);

            String activeIngredientsString = String.join(",", medication.getActiveIngredients());
            String historyString = String.join(",", medication.getHistory());

            insertMedicationsStatement.setString(1, medication.getName());
            insertMedicationsStatement.setString(2, activeIngredientsString);
            insertMedicationsStatement.setString(3, historyString);
            insertMedicationsStatement.setInt(4, userId);

            System.out.println("Inserting new medication -> Successful -> Rows Added: " + insertMedicationsStatement.executeUpdate());
        }
    }

    public Medication getMedicationFromId(int medicationId, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM MEDICATION id = id;
            String query = "SELECT * FROM MEDICATION WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, medicationId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a indication that fields arent empty
                return getMedicationFromResultSet(resultSet);
            }
        }

    }

    public void updateMedication(Medication medication, int medicationId, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "UPDATE MEDICATION SET name = ?, active_ingredients = ?, history = ? WHERE user_id = ? AND id = ?";
            PreparedStatement statement = connection.prepareStatement(update);

            statement.setString(1, medication.getName());
            statement.setString(2, String.join(",", medication.getActiveIngredients()));
            statement.setString(3, String.join(",", medication.getHistory()));
            statement.setInt(4, userId);
            statement.setInt(5, medicationId);
            System.out.println("Update Medication - ID: " + medicationId + " USERID: " + userId + " -> Successful -> Rows Updated: " + statement.executeUpdate());
        }
    }

    public void removeMedication(int userId, int medicationId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM MEDICATION WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setInt(1, medicationId);
            statement.setInt(2, userId);
            System.out.println("Deletion of Medication - ID: " + medicationId + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    public Medication getMedicationFromResultSet(ResultSet medicationsResultSet) throws SQLException {
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

        return new Medication(
                medicationsResultSet.getString("name"),
                activeIngredients,
                historyList,
                medicationsResultSet.getInt("id")
        );
    }

    /**
     * Replace a user's medications on the database with a new set of medications.
     *
     * @param newMedications The list of medications to replace the old one with
     * @param userId The id of the user to replace medications of
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllMedications(List<Medication> newMedications, int userId) throws SQLException {
        List<Medication> oldMedications = getAllMedications(userId);

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
                        updateMedication(oldMedications.get(i), oldMedications.get(i).getId(), userId);
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
            removeMedication(userId, medication.getId());
        }

        //Upload all new medications
        for (Medication medication: newMedications) {
            insertMedication(medication, userId);
        }
    }
}
