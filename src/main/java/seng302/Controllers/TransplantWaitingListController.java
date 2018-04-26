package seng302.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import seng302.Core.*;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class TransplantWaitingListController implements Initializable {

    @FXML
    private TableView transplantTable;

    @FXML
    private TableColumn organColumn;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn dateColumn;
    @FXML
    private TableColumn regionColumn;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
    private ObservableList<TransplantWaitingListItem> transplantList = FXCollections.observableArrayList();

    /**
     * returns to the clinician view
     */
    public void returnView(){
        Main.setScene(TFScene.clinician);
    }

    /**
     * Closes the application
     */
    public void close(){
        Platform.exit();
    }

    /**
     * Updates the transplant waiting list table
     */
    private void updateTransplantList() {
        for (User user : Main.users) {
            if (!user.getWaitingListItems().isEmpty()) {
                for (WaitingListItem item : user.getWaitingListItems()) {
                    try {
                        if (!(item.getOrganRegisteredDate() == null)) {
                            transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), sdf.parse(item.getOrganRegisteredDate()), item.getOrganType(), user.getId()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        transplantTable.setItems(transplantList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        organColumn.setCellValueFactory(new PropertyValueFactory<>("organ"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        transplantTable.setItems(transplantList);

        Main.setTransplantWaitingListController(this);

        updateTransplantList();
        transplantTable.setItems(transplantList);

        transplantTable.setRowFactory(new Callback<TableView<TransplantWaitingListItem>, TableRow<TransplantWaitingListItem>>(){
            @Override
            public TableRow<TransplantWaitingListItem> call(TableView<TransplantWaitingListItem> tableView) {
                final TableRow<TransplantWaitingListItem> row = new TableRow<TransplantWaitingListItem>() {
                };
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 2) {
                        Stage stage = new Stage();

                        Main.addCliniciansUserWindow(stage);
                        stage.initModality(Modality.NONE);

                        try{
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userWindow.fxml"));
                            Parent root = (Parent) loader.load();
                            UserWindowController userWindowController = loader.getController();
                            Main.setCurrentUser(Main.getUserById(row.getItem().getId()));
                            userWindowController.populateUserFields();
                            userWindowController.populateHistoryTable();
                            Main.medicationsViewForClinician();

                            Scene newScene = new Scene(root, 900, 575);
                            stage.setScene(newScene);
                            stage.show();
                            userWindowController.setAsChildWindow();
                        } catch (IOException | NullPointerException e) {
                            System.err.println("Unable to load fxml or save file.");
                            e.printStackTrace();
                            Platform.exit();
                        }
                    }
                });
                transplantTable.refresh();
                return row;

            }
        });
    }

}
