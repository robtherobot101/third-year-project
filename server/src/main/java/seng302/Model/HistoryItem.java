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

    public int getId() {
        return id;
    }
}
