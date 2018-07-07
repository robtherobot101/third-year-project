package seng302.GUI.Controllers.User;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.GridPane;
import seng302.User.History;
import seng302.User.User;

public class UserHistoryController extends UserTabController implements Initializable {
    @FXML
    private Label userHistoryLabel;
    @FXML
    private TreeTableView<String> historyTreeTableView;
    @FXML
    private GridPane historyGridPane;
    @FXML
    private TreeTableColumn<String, String> dateTimeColumn, actionColumn;

    private User currentUser;

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Populates the history table based on the action history of the current user.
     * Gets the user history from the History.getUserHistory() function.
     * Sorts these into tree nodes based on new sessions.
     */
    public void populateTable() {
        userHistoryLabel.setText("History of actions for " + currentUser.getPreferredName());
        String[][] userHistory = History.getUserHistory(currentUser.getId());
        ArrayList<TreeItem<String>> treeItems = new ArrayList<>();
        if (userHistory[0][0] != null) {
            TreeItem<String> sessionNode = new TreeItem<>("Session 1 on " + userHistory[0][0].substring(0, userHistory[0][0].length() - 1));
            TreeItem<String> outerItem1 = new TreeItem<>("Create at " + userHistory[0][1]);
            TreeItem<String> outerItem2 = new TreeItem<>("Login at " + userHistory[0][1]);
            sessionNode.getChildren().add(outerItem1);
            sessionNode.getChildren().add(outerItem2);
            treeItems.add(sessionNode);

            int sessionNumber = 2;
            for (int i = 2; i < userHistory.length; i++) {
                if (!(userHistory[i][4] == null) && !(userHistory[i][4].equals("create"))) {
                    switch (userHistory[i][4]) {
                        case "update":
                        case "undo":
                        case "redo":
                        case "medications":
                        case "procedures":
                        case "diseases":
                        case "logout":
                        case "quit":
                            sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4]
                                .substring(1) + " at " + userHistory[i][1]));
                            break;
                        case "updateAccountSettings":
                            sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4]
                                .substring(1, 6) +
                                " " + userHistory[i][4].substring(6, 13) + " at " + userHistory[i][1]));
                            break;

                        case "waitinglist":
                            sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4]
                                .substring(1, 7) + " " + userHistory[i][4].substring(7) + " modified " + " at " + userHistory[i][1]));
                            break;

                        case "modifyUser":
                            sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4]
                                .substring(1, 6) +
                                " " + userHistory[i][4].substring(6, 10) + " at " + userHistory[i][1]));
                            break;
                        case "login":
                            sessionNode = new TreeItem<>("Session " + sessionNumber + " on " + userHistory[i][0].substring(0, userHistory[i][0]
                                .length() -
                                1));
                            treeItems.add(sessionNode);
                            sessionNode.getChildren().add(new TreeItem<>("Login at " + userHistory[i][1]));
                            sessionNumber++;
                            break;
                        case "view":
                            sessionNode = new TreeItem<>("Session " + sessionNumber + " on " + userHistory[i][0].substring(0, userHistory[i][0]
                                .length() -
                                1));
                            treeItems.add(sessionNode);
                            sessionNode.getChildren().add(new TreeItem<>("View at " + userHistory[i][1]));
                            sessionNumber++;
                            break;
                    }
                }
            }
        }
        final TreeItem<String> root = new TreeItem<>("User History");
        root.setExpanded(true);
        root.getChildren().setAll(treeItems);

        //Defining cell content
        dateTimeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
            new ReadOnlyStringWrapper(p.getValue().getValue()));

        actionColumn.setCellValueFactory(param -> {
            String userName = currentUser.getName(), toCheck = param.getValue().getValue().substring(0, 12);
            if (toCheck.equals("Update Account")) {
                return new ReadOnlyStringWrapper("Updated account settings for user " + userName + ".");
            }
            switch (toCheck.substring(0, 6)) {
                case "Create":
                    return new ReadOnlyStringWrapper("Created a new user profile with name " + userName + ".");
                case "Update":
                    return new ReadOnlyStringWrapper("Updated user attributes for user " + userName + ".");
                case "Logout":
                    return new ReadOnlyStringWrapper("User with id: " + currentUser.getId() + " logged out successfully.");
            }
            if (toCheck.substring(0, 5).equals("Login")) {
                return new ReadOnlyStringWrapper("User with id: " + currentUser.getId() + " logged in successfully.");
            }
            switch (toCheck.substring(0, 4)) {
                case "Undo":
                    return new ReadOnlyStringWrapper("Reversed last action.");
                case "Redo":
                    return new ReadOnlyStringWrapper("Reversed last undo.");
                case "Quit":
                    return new ReadOnlyStringWrapper("Quit the application.");
                case "View":
                    return new ReadOnlyStringWrapper("-Clinician- Viewed user " + userName + " .");
            }
            if (toCheck.equals("Modify User")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s attributes.");
            }
            if (toCheck.substring(0, 11).equals("Medications")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s medications.");
            }
            if (toCheck.substring(0, 12).equals("Waiting list")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s waiting list.");
            }
            if (toCheck.substring(0, 8).equals("Diseases")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s diseases.");
            }
            if (toCheck.substring(0, 10).equals("Procedures")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s procedures.");
            }
            return null;
        });

        //Creating a tree table view
        historyTreeTableView.setRoot(root);
        historyTreeTableView.setShowRoot(true);
    }

    @Override
    public void redo() {

    }

    @Override
    public void undo() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
