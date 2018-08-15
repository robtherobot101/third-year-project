package seng302.GUI.Controllers.Clinician;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.http.client.HttpResponseException;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.DonatableOrgan;
import seng302.User.User;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class ClinicianAvailableOrgansController implements Initializable{

    @FXML
    AnchorPane organsPane;

    @FXML
    TableView organsTable;

    @FXML
    TableColumn organColumn, nameColumn, countdownColumn, dateOfDeathColumn, regionColumn;

    @FXML
    Button refreshOrganTable;

    @FXML
    Label refreshSuccessText;

    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    //This is filler bc  I don't know how tree tables work yet
    private ObservableList<DonatableOrgan> expiryList = FXCollections.observableArrayList();

    private Timer time = new Timer(true);
    private TimerTask tick;
    private String token;
    private boolean focused;
    private boolean updated;
    private FadeTransition fade = new FadeTransition(javafx.util.Duration.millis(3500));
    private boolean autoRefresh;

    public void setToken(String token) {
        this.token = token;
    }

    public boolean hasToken() {
        return token != null;
    }


    /**
     * Sets the initial time left values for all list items.
     */
    public void setInitTimeLeft(List<DonatableOrgan> expiryList){

        for (DonatableOrgan organ : expiryList){
            //for each item in the list
            LocalDateTime now = LocalDateTime.now();

            // Calculate the date the organ expires, based on the organ
            LocalDateTime deathDate = organ.getTimeOfDeath();
            Duration expiryDuration = organ.getExpiryDuration(organ.getOrganType());
            LocalDateTime expiryDate = deathDate.plus(expiryDuration);

            if (now.isBefore(expiryDate) && expiryDate.isBefore(now.plusHours(100))){
                // Set time remaining
                //calculate the initial value of time remaining (if lower than 100 hours left)
                Duration timeLeft = Duration.between(now, expiryDate);
                organ.setTimeLeft(timeLeft);
            } else {
                Duration timeLeft = Duration.between(now, expiryDate);
                organ.setTimeLeft(timeLeft);
                //Either the organ shouldn't be displaying, or it should display <4 days or something
            }
        }
    }

    /**
     * Creates a timer which ticks every second and updates each organ object, counting down their expiry time by 1 second.
     * This timer runs in a background thread, and with only 1 timer running SHOULD be real time reliable.
     */
    public void initTimer(){
        // get data from server and load them into the tree table or whatever

        //set up the timer
        int delay = 1000;
        int period = 1000;
        System.out.println("Initializing timer...");
        //time = new Timer(true);
        tick = new TimerTask() {
            public void run() {
                for (DonatableOrgan organ : expiryList){
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
                organsTable.refresh();
                autoRefresh = false;
            }

        };
        time.scheduleAtFixedRate(tick, delay, period);

    }
    /**
     * Updates the organs in the available organs table
     */
    public void updateOrgans() {
        try {
            List<DonatableOrgan> temp = new ArrayList<>(WindowManager.getDataManager().getGeneral().getAllDonatableOrgans(token));
            setInitTimeLeft(temp);
            expiryList.clear();
            User lastUser = null;
            for(DonatableOrgan organ : temp) {
                if (!organ.getTimeLeft().isNegative() && !organ.getTimeLeft().isZero()) {
                    if(lastUser == null){
                        lastUser = addUserInfo(organ);
                    }
                    //if multiple organs from the same user no need to do multiple API calls
                    if (organ.getDonorId() == lastUser.getId()){
                        organ.setReceiverName(lastUser.getName());
                        organ.setReceiverDeathRegion(lastUser.getRegionOfDeath());
                    } else {
                        lastUser = addUserInfo(organ);
                    }
                    expiryList.add(organ);
                }
            }
            expiryList.sort(Comparator.comparing(DonatableOrgan::getTimeLeft));
            updated = true;
        } catch (HttpResponseException e) {
            Debugger.error("Failed to retrieve all users and refresh transplant waiting list..");
        }
    }

    /**
     * adds the user info to a Donatable organ item
     * @param organ the waiting list item to update
     */
    private User addUserInfo(DonatableOrgan organ) {
        try{
            User user = WindowManager.getDataManager().getUsers().getUser(organ.getDonorId(), token);
            organ.setReceiverName(user.getName());
            organ.setReceiverDeathRegion(user.getRegionOfDeath());
            return user;
        } catch (HttpResponseException e) {
            Debugger.error("Failed to retrieve user with ID: " + organ.getDonorId());
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Initilizes the gui display with the correct content in the table.
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WindowManager.setClinicianAvailableOrgansController(this);

        organColumn.setCellValueFactory(new PropertyValueFactory<>("organType"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("receiverName"));
        countdownColumn.setCellValueFactory(new PropertyValueFactory<>("timeLeftString"));
        dateOfDeathColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDeath"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("receiverDeathRegion"));

        //transplantTable.setItems(transplantList);
        organsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        organsTable.setItems(expiryList);

        organsTable.setRowFactory(new Callback<TableView<DonatableOrgan>, TableRow<DonatableOrgan>>() {
            @Override
            public TableRow<DonatableOrgan> call(TableView<DonatableOrgan> tableView) {
                final TableRow<DonatableOrgan> row = new TableRow<DonatableOrgan>() {
                    public void updateItem(DonatableOrgan item, boolean empty) {
                        super.updateItem(item, empty);
                        getStyleClass().remove("highlighted-row-organs-25");
                        getStyleClass().remove("highlighted-row-organs-50");
                        getStyleClass().remove("highlighted-row-organs-75");
                        getStyleClass().remove("highlighted-row-organs-100");
                        setTooltip(null);
                        if (item != null && !empty) {
                            if (item.getTimePercent() <  0.25) {
                                if (!getStyleClass().contains("highlighted-row-organs-25")) {
                                    getStyleClass().add("highlighted-row-organs-25");
                                }
                            } else if (item.getTimePercent() <  0.50) {
                                if (!getStyleClass().contains("highlighted-row-organs-50")) {
                                    getStyleClass().add("highlighted-row-organs-50");
                                }
                            } else if (item.getTimePercent() <  0.75) {
                                if (!getStyleClass().contains("highlighted-row-organs-75")) {
                                    getStyleClass().add("highlighted-row-organs-75");
                                }
                            } else {
                                if (!getStyleClass().contains("highlighted-row-organs-100")) {
                                    getStyleClass().add("highlighted-row-organs-100");
                                }
                            }
                        }
                    }
                };
                organsTable.refresh();
                return row;
            }
        });
        focused = false;
        fade.setNode(refreshSuccessText);
        fade.setFromValue(1.0);
        fade.setToValue(0);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
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


    /**
     * Refreshes the available organs table and displays a label on a successful update.
     * Helps reduce server load by removing the need to create an auto update.
     */
    public void refreshTable(){
        updated = false;
        WindowManager.updateAvailableOrgans();
        if (updated){
            refreshSuccessText.setVisible(true);
            fade.playFromStart();
        }
    }

    /**
     * Called when an organ passes the colour threshold eg 50% -> 49% and so needs to be updated.
     * Not using this calls updateOrgans() in the middle of the for loop (not ideal as throws concurrent mod. exceptions)
     * @param value the value of autoRefresh
     */
    public void setAutoRefresh(boolean value){
        autoRefresh = value;
    }
}
