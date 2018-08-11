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
import seng302.User.Attribute.NZRegion;
import seng302.User.Attribute.Organ;
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
    ComboBox<String> organFilter, regionFilter;

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
            //TODO Find a cleaner way of getting organ dates and times in durations or something to be able to add to deathDate, and find expiry durations for ear and tissue
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
        //create timer task to tick down


        //TODO figure out how to handle changing tab - end the timer or leave it running in the background until app close??
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
                    } else {
                        //...unless it is 0, in which do whatever needs to be done
                    }
                    organsTable.refresh();
                }


            }

        };
        time.scheduleAtFixedRate(tick, delay, period);

    }
    /**
     * Updates the organs in the available organs table
     */
    public void updateOrgans() {
        try {
            List<DonatableOrgan> temp = new ArrayList<>(WindowManager.getDataManager().getGeneral().getAllDonatableOrgans(new HashMap(),token));
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

    public void filterOrgans(){
        HashMap filterParams = new HashMap();
        if (regionFilter.getSelectionModel().getSelectedItem() != "All Regions"){
            filterParams.put("userRegion", regionFilter.getSelectionModel().getSelectedItem());
        }
        if (organFilter.getSelectionModel().getSelectedItem() != "All Organs"){
            filterParams.put("organ", organFilter.getSelectionModel().getSelectedItem());
        }
        try{
            List<DonatableOrgan> temp = new ArrayList<>(WindowManager.getDataManager().getGeneral().getAllDonatableOrgans(filterParams, token));
            setInitTimeLeft(temp);
            expiryList.clear();
            for(DonatableOrgan organ : temp) {
                if (!organ.getTimeLeft().isNegative() && !organ.getTimeLeft().isZero()) {
                    addUserInfo(organ);
                    expiryList.add(organ);
                }
            }
            expiryList.sort(Comparator.comparing(DonatableOrgan::getTimeLeft));
        } catch (HttpResponseException e) {
            Debugger.error("Failed to retrieve all users and refresh transplant waiting list..");
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

        ObservableList<String> organSearchlist = FXCollections.observableArrayList();
        Organ[] organsList = Organ.values();
        organSearchlist.add("All Organs");
        for (Organ o : organsList) {
            String v = o.toString();
            organSearchlist.add(v);
        }
        organFilter.setItems(organSearchlist);
        organFilter.setValue("All Organs");

        ObservableList<String> regionSearchlist = FXCollections.observableArrayList();
        NZRegion[] regionList = NZRegion.values();
        regionSearchlist.add("All Regions");
        for (NZRegion o : regionList) {
            String v = o.toString();
            regionSearchlist.add(v);
        }
        regionFilter.setItems(regionSearchlist);
        regionFilter.setValue("All Regions");

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
}
