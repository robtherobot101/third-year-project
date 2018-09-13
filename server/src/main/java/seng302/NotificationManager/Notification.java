package seng302.NotificationManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Notification {

    private Map<String, Object> notificationContent = new HashMap<>();
    private Map<String, Object> customData = new HashMap<>();

    /**
     * Construct a basic notification with title and message
     * @param title The title of the notification
     * @param message The message content of the notification
     */
    public Notification(String title, String message) {
        notificationContent.put("name", "Test");
        notificationContent.put("title", title);
        notificationContent.put("body", message);
        notificationContent.put("custom_data", customData);
    }

    /**
     * Adds custom data to the notification
     * @param key The key with which the data is associated
     * @param data The data to associate with the key
     */
    public void addCustomData(String key, Object data) {
        customData.put(key, data);
    }

    /**
     * Return the map of notification content
     * @return a Map containing the notification title, name, message, and any custom data
     */
    public Map<String, Object> getNotificationContent() {
        return Collections.unmodifiableMap(notificationContent);
    }
}
