package seng302.Model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private String text;
    private LocalDateTime timestamp;
    private int userId;

    /**
     * Constructs a new message to be added to the database.
     *
     * @param text The message body
     * @param userId The id of the user that sent the message
     */
    public Message(String text, int userId) {
        id = 0;
        this.text = text;
        timestamp = LocalDateTime.now();
        this.userId = userId;
    }

    /**
     * Constructs a new message from a database entry.
     *
     * @param id The message id
     * @param text The message body
     * @param timestamp The time the message was received by the server
     * @param userId The id of the user that sent the message
     */
    public Message(int id, String text, LocalDateTime timestamp, int userId) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
