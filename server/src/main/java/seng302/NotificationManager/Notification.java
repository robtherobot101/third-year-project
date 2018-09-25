package seng302.NotificationManager;

import com.google.gson.Gson;

import java.util.Collection;
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
        notificationContent.put("name", "Notification");
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
     * Convert the notification to a JSON string to be sent to the Push API
     *
     * @param devices A list of device ids to send the notification to
     * @return A string representation of the notification in JSON format
     */
    public String toJSON(Collection<String> devices) {
        Map<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("notification_content", notificationContent);
        Map<String, Object> targetMap = new HashMap<>();
        targetMap.put("type", "devices_target");
        targetMap.put("devices", devices.toArray());
        notificationMap.put("notification_target", targetMap);
        return new Gson().toJson(notificationMap);
    }
}
