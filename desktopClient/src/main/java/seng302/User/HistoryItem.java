package seng302.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class for handling all history details of a User
 */
public class HistoryItem {
    private LocalDateTime dateTime;
    private String action;
    private String description;

    public HistoryItem(LocalDateTime dateTime, String action, String description) {
        this.dateTime = dateTime;
        this.action = action;
        this.description = description;
    }

    public HistoryItem(String action, String description) {
        this.dateTime = LocalDateTime.now();
        this.action = action;
        this.description = description;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
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
}
