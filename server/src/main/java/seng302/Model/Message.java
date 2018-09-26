package seng302.Model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private int conversationId;
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
    public Message(String text, int userId, int conversationId) {
        id = 0;
        this.text = text;
        timestamp = LocalDateTime.now();
        this.userId = userId;
        this.conversationId = conversationId;
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

    public void setId(int id) { this.id = id ; }

    public Map<String, String> messageMap() {
        Map<String, String> m = new HashMap<>();
        m.put("id", String.valueOf(id));
        m.put("text", text);
        m.put("timestamp", timestamp.toString());
        m.put("userId", String.valueOf(userId));
        m.put("conversationId", String.valueOf(conversationId));
        return m;
    }
}
