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


    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    private ObservableList<DonatableOrgan> expiryList = FXCollections.observableArrayList();

    public ClinicianExpiryController() {
        this.titleBar = new TitleBar();
        titleBar.setStage(WindowManager.getStage());

    }

    //Call me on init, WIP
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
            //TODO Find a clean way of getting organ dates and times in MS or something to be able to add to deathDate
            LocalDateTime expiryDate;




            if (1==1
                    //uncomment this when expiryDate can be initialized
                   //now.isBefore(expiryDate) && expiryDate.isBefore(now.plusHours(100)
                    ){
                // Set time remaining
                //calculate the initial value of time remaining (if lower than 100 hours left)
                Duration timeLeft = Duration.ZERO;
                organ.setTimeLeft(timeLeft);

            } else {

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
