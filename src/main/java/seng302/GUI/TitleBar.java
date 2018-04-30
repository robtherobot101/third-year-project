package seng302.GUI;

import javafx.stage.Stage;
import seng302.User.User;

public class TitleBar {

    private Stage stage;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * Update the title of the window.
     * @param title The string to set the title to.
     */
    public void setTitle(String title){
        stage.setTitle(title);
    }

    /**
     * Set the title for a user and a page.
     * @param user The name of the currently logged in user
     * @param type The type of user (User, Clinician etc)
     * @param page The currently visible page
     */
    public void setTitle(String user, String type, String page){
        String title = type + ": " + user;
        if(page != null){
            title = title.concat(" - " + page);
        }
        stage.setTitle(title);
    }

    /**
     * Append a * to the title bar when a change is made
     */
    public void saved(boolean saved){
        if(saved && stage.getTitle().endsWith("*")){
            // Remove the asterisk
            stage.setTitle(stage.getTitle().substring(0, stage.getTitle().length() - 1));
        }

        else if(!saved && !stage.getTitle().endsWith("*") ) {
            // Add the asterisk
            stage.setTitle(stage.getTitle() + "*");
        }
    }
}
