package seng302.gui.controllers.clinician;

import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.http.client.HttpResponseException;
import org.json.JSONObject;
import seng302.User.Attribute.NZRegion;
import seng302.User.Attribute.Organ;
import seng302.User.DonatableOrgan;
import seng302.User.Hospital;
import seng302.User.OrganTransfer;
import seng302.User.User;
import seng302.generic.Country;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.gui.StatusIndicator;
import seng302.gui.TitleBar;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class ClinicianTransferOrgansController implements Initializable{
    @FXML
    TableView transferTable;

    @FXML
    TableColumn organColumn;

    @FXML
    TableColumn regionColumn;

    @FXML
    TableColumn hospitalColumn;

    @FXML
    TableColumn receiverColumn;

    @FXML
    TableColumn countdownColumn;

    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    private Timer time = new Timer(true);
    private TimerTask tick;
    private String token;
    private boolean focused;
    private boolean updated;
    private boolean autoRefresh;


    private ObservableList<OrganTransfer> transfers = FXCollections.observableArrayList();



    public boolean hasToken() {
        return token != null;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Sets the initial time left values for all list items.
     * @param transferList A list of transfers to be updated.
     */
    public void setInitTimeLeft(List<OrganTransfer> transferList){
        for (OrganTransfer transfer : transferList){
            LocalDateTime now = LocalDateTime.now();

            LocalDateTime arrivalTime = transfer.getArrivalTime();
            Duration timeLeft = Duration.between(now, arrivalTime);
            transfer.setTimeLeft(timeLeft);
        }
    }

    public void addDetails(OrganTransfer transfer) {
        try{
            List<Hospital> hospitals = WindowManager.getDataManager().getGeneral().getHospitals(token);

            User receiver = WindowManager.getDataManager().getUsers()
                    .getUser(transfer.getReceiverId(), token);
            transfer.setReceiverName(receiver.getName());
            for(Hospital hospital : hospitals) {
                if(hospital.getRegion().equals(receiver.getRegion())) {
                    transfer.setHospitalName(hospital.getName());
                    transfer.setDestinationRegion(hospital.getRegion());
                }
            }
        } catch(HttpResponseException e) {
            Debugger.error("Failed to update list of organ transfers.");
        }
    }

    public void updateOrgans() {
        System.out.println("Got here");
        try{
            List<OrganTransfer> transferList = WindowManager.getDataManager().getGeneral().getAllOrganTransfers(token);
            System.out.println("Number of transfers: " + transferList.size());
            for(OrganTransfer organTransfer : transferList) {
                addDetails(organTransfer);
            }

            setInitTimeLeft(transferList);

            transfers.clear();
            transfers.addAll(transferList);
            transferTable.refresh();
        } catch(HttpResponseException e) {
            Debugger.error("Failed to update list of organ transfers.");
        }
    }

    /**
     * Creates a timer which ticks every second and updates each organ object, counting down their expiry time by 1 second.
     * This timer runs in a background thread, and with only 1 timer running SHOULD be real time reliable.
     */
    private void initTimer(){
        int delay = 1000;
        int period = 1000;
        Debugger.log("Initializing timer...");
        tick = new TimerTask() {
            public void run() {
                for (OrganTransfer organ : transfers){
                    if (organ.getTimeLeft().compareTo(Duration.ZERO) > 0){
                        //for each item set timeLeft -1
                        organ.tickTimeLeft();
                    } else { //Organ has hit 0 so should be removed
                        autoRefresh = true;
                    }
                }

                if (autoRefresh){
                    refreshTable();
                }
                transferTable.refresh();
                autoRefresh = false;
            }

        };

        time.scheduleAtFixedRate(tick, delay, period);
    }

    /**
     * Refreshes the available organs table and displays a label on a successful update.
     * Helps reduce server load by removing the need to create an auto update.
     */
    public void refreshTable(){
        updated = false;
        WindowManager.updateTransferOrgans();
    }

    /**
     * Initilizes the gui display with the correct content in the table.
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WindowManager.setClinicianTransferOrgansController(this);

        organColumn.setCellValueFactory(new PropertyValueFactory<>("organType"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("destinationRegion"));
        hospitalColumn.setCellValueFactory(new PropertyValueFactory<>("hospitalName"));
        receiverColumn.setCellValueFactory(new PropertyValueFactory<>("receiverName"));
        countdownColumn.setCellValueFactory(new PropertyValueFactory<>("timeLeft"));

        countdownColumn.setCellFactory(param -> new TableCell<DonatableOrgan, Duration>() {

            @Override
            protected void updateItem(Duration item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Long millis = item.toMillis();
                    setText(String.format("%dd %02d:%02d:%02d", TimeUnit.MILLISECONDS.toDays(millis),
                            TimeUnit.MILLISECONDS.toHours(millis) % TimeUnit.DAYS.toHours(1),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)));

                }
            }
        });

        transferTable.setItems(transfers);

    }

    /**
     * Starts the timer when the organs tab is first opened.
     */
    public void startTimer() {
        if (!focused) {
            initTimer();
        }
        focused = true;
    }

    public void setup() {

    }

    /**
     * Stops the timer when the tab or window is exited.
     */
    public void stopTimer(){
        if(focused) {
            if (time != null) {
                tick.cancel();
            }
        }
        focused = false;
    }
}
