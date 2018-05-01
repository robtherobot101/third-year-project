package seng302.GUI;

import javafx.scene.control.ProgressBar;
import org.controlsfx.control.StatusBar;

/**
 * Provides a method to update the status shown on a status bar
 */
public class StatusIndicator {
    private StatusBar statusBar;

    /**
     * Default constructor
     */
    public StatusIndicator(){};

    /**
     * Set the status and progress bar on the status bar
     * @param status A string representing the result of the last operation
     * @param busy A boolean indicating whether a process is running in the background (e.g. an API call)
     */
    public void setStatus(String status, boolean busy){
        statusBar.setText(status);
        if(busy) statusBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        else statusBar.setProgress(0);
    }

    public void ready(){
        statusBar.setText("Ready");
        statusBar.setProgress(0);
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }
}
