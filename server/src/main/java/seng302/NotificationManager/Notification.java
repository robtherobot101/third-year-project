package seng302.NotificationManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Notification {

    private Map<String, Object> notification = new HashMap<>();;

    public Notification(String title, String message, String device) {
        Map<String, Object> notificationContent = new HashMap<>();
        notificationContent.put("name", "Test");
        notificationContent.put("title", title);
        notificationContent.put("body", message);
        notification.put("notification_content", notificationContent);
    }

    public Map<String, Object> getNotification() {
        return Collections.unmodifiableMap(notification);
    }
}
