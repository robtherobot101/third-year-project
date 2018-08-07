package seng302.GUI.Controllers.Clinician;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.apache.http.client.HttpResponseException;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.DonatableOrgan;

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

    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    //This is filler bc  I don't know how tree tables work yet
    private ObservableList<DonatableOrgan> expiryList = FXCollections.observableArrayList();

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public boolean hasToken() {
        return token != null;
    }


    /**
     * Creates a timer which ticks every second and updates each organ object, counting down their expiry time by 1 second.
     * This timer runs in a background thread, and with only 1 timer running SHOULD be real time reliable.
     */
    public void setTimeLeftList(List<DonatableOrgan> expiryList){
        // get data from server and load them into the tree table or whatever

        //set up the timer
        int delay = 1000;
        int period = 1000;
        Timer time = new Timer();

        for (DonatableOrgan organ : expiryList){
            //for each item in the list
            LocalDateTime now = LocalDateTime.now();

            // Calculate the date the organ expires, based on the organ
            LocalDateTime deathDate = organ.getDateOfDeath();
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
        time.scheduleAtFixedRate(new TimerTask() {
            public void run() {


                for (DonatableOrgan organ : expiryList){
                    if (organ.getTimeLeft().compareTo(Duration.ZERO) > 0){
                        //for each item set timeLeft -1
                        organ.tickTimeLeft();
                    } else {
                        //...unless it is 0, in which do whatever needs to be done
                    }

                }


            }

        }, delay, period);

        //TODO figure out how to handle changing tab - end the timer or leave it running in the background until app close??
    }

    /**
     * Updates the organs in the available organs table
     */
    public void updateOrgans() {
        try {
            List<DonatableOrgan> temp = new ArrayList<>(WindowManager.getDataManager().getGeneral().getAllDonatableOrgans(token));
            setTimeLeftList(temp);
            expiryList.clear();
            for(DonatableOrgan organ : temp) {
                if (!organ.getTimeLeft().isNegative() && !organ.getTimeLeft().isZero()) {
                    expiryList.add(organ);
                }
            }
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
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("DonorId"));
        countdownColumn.setCellValueFactory(new PropertyValueFactory<>("timeLeft"));
        dateOfDeathColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDeath"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("receiverRegion"));

        //transplantTable.setItems(transplantList);
        organsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        updateOrgans();

        organsTable.setItems(expiryList);
    }
}
