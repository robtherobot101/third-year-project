package seng302.Model;

import java.time.LocalDateTime;

/**
 * Class for handling all history details of a user
 */
public class HistoryItem {
    private LocalDateTime dateTime;
    private String action;
    private String description;
    private int id;

    /**
     * constructor method to create a new history item object that has been pulled from the database
     * @param dateTime LocalDateTime the time of the action
     * @param action String the action that occurred
     * @param description String the description of the action
     * @param id int the action id
     */
    public HistoryItem(LocalDateTime dateTime, String action, String description, int id) {
        this.dateTime = dateTime;
        this.action = action;
        this.description = description;
        this.id = id;
    }

    /**
     * constructor method to create a new history item
     * @param dateTime LocalDateTime the time of the action
     * @param action String the action that occurred
     * @param description String the description of the action
     */
    public HistoryItem(LocalDateTime dateTime, String action, String description) {
        this.dateTime = dateTime;
        this.action = action;
        this.description = description;;
    }

    /**
     * method to get the date time of a history item object
     * @return LocalDateTime the date & time of the history item
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * method to get the action of a history item object
     * @return String the action that occurred
     */
    public String getAction() {
        return action;
    }

    /**
     * method to get the description of a history item object
     * @return String the description of the action that occurred
     */
    public String getDescription() {
        return description;
    }

    /**
     * Determines whether the information in this HistoryItem is the same as another (checks all fields apart from ID).
     *
     * @param other The object to compare to
     * @return Whether the information is equal
     */
    public boolean informationEqual(HistoryItem other) {
        return (this.action.equals(other.action) && this.description.equals(other.description) && this.dateTime.equals(other.dateTime));
    }

    /**
     * method to get the id of the history item object
     * @return int the id of the history item
     */
    public int getId() {
        return id;
    }
}
