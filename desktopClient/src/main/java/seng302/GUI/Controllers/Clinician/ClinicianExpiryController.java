package seng302.GUI.Controllers.Clinician;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.Generic.WindowManager;
import seng302.User.DonatableOrgan;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ClinicianExpiryController implements Initializable {


    public enum expiry_times{

    }
    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    //This is filler bc  I don't know how tree tables work yet
    private ObservableList<DonatableOrgan> expiryList = FXCollections.observableArrayList();

    public ClinicianExpiryController() {
        this.titleBar = new TitleBar();
        titleBar.setStage(WindowManager.getStage());
    }

    /**
     * Creates a timer which ticks every second and updates each organ object, counting down their expiry time by 1 second.
     * This timer runs in a background thread, and with only 1 timer running SHOULD be real time reliable.
     */
    public void setTimeLeft(){
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
                Duration timeLeft = Duration.ZERO;
                organ.setTimeLeft(timeLeft);
            } else {
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
