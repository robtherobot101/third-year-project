package seng302.NotificationManager;


/**
 * An extension of the Notification class which is a simple message
 */
public class Message extends Notification {

    /**
     * Creates a new message to be sent via the push API
     * @param message The content of the message
     */
    public Message(String message) {
        super("New message", message);
        // Mark this notification as a message
        addCustomData("message", true);
    }

}
