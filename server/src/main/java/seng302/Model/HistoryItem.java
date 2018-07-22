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

    public HistoryItem(LocalDateTime dateTime, String action, String description, int id) {
        this.dateTime = dateTime;
        this.action = action;
        this.description = description;
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getAction() {
        return action;
    }

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

    public int getId() {
        return id;
    }
}
