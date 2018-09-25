package seng302.gui.controllers.user;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import seng302.User.HistoryItem;
import seng302.User.User;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserHistoryController extends UserTabController implements Initializable {
    @FXML
    private Label userHistoryLabel;
    @FXML
    private TreeTableView<Object> historyTreeTableView;
    @FXML
    private TreeTableColumn<Object, String> dateColumn, actionColumn;

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Sets which user's history is to be displayed in this history table.
     *
     * @param user The user to display
     */
    public void setCurrentUser(User user) {
        currentUser = user;
        populateTable();
    }

    /**
     * Populates the history table based on the action history of the current user.
     * Gets the user history from the History.getUserHistory() function.
     * Sorts these into tree nodes based on the day that the actions occured.
     */
    public void populateTable() {
        userHistoryLabel.setText("History of actions for " + currentUser.getPreferredName());

        currentUser.sortHistory();
        Map<LocalDate, List<HistoryItem>> sortedByDate = new LinkedHashMap<>();
        for (HistoryItem historyItem: currentUser.getUserHistory()) {
            LocalDate date = historyItem.getDate();
            if (!sortedByDate.containsKey(date)) {
                sortedByDate.put(date, new ArrayList<>());
            }
            sortedByDate.get(date).add(historyItem);
        }

        TreeItem<Object> root = new TreeItem<>();
        for (LocalDate date: sortedByDate.keySet()) {
            TreeItem<Object> newItem = new TreeItem<>(date);
            for (HistoryItem historyItem: sortedByDate.get(date)) {
                newItem.getChildren().add(new TreeItem<>(historyItem));
            }
            root.getChildren().add(newItem);
        }
        dateColumn.setCellValueFactory(
                param -> {
                    if (param.getValue().getValue() instanceof LocalDate) {
                        LocalDate date = (LocalDate)param.getValue().getValue();
                        return new ReadOnlyStringWrapper(date.format(User.dateFormat));
                    } else {
                        HistoryItem item = (HistoryItem) param.getValue().getValue();
                        return new ReadOnlyStringWrapper(item.getDateTime().format(timeFormat));
                    }
                }
        );
        actionColumn.setCellValueFactory(
                param -> {
                    if (param.getValue().getValue() instanceof LocalDate) {
                        return new ReadOnlyStringWrapper("");
                    } else {
                        HistoryItem item = (HistoryItem) param.getValue().getValue();
                        return new ReadOnlyStringWrapper(item.getAction() + ": " + item.getDescription());
                    }
                }
        );

        historyTreeTableView.setRoot(root);
    }

    @Override
    public void redo() {

    }

    @Override
    public void undo() {

    }

    /**
     * Sets the history table to not display the top level root node.
     *
     * @param location Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        historyTreeTableView.setShowRoot(false);
    }
}
