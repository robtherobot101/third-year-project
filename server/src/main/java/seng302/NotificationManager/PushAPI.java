package seng302.NotificationManager;


import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import seng302.Logic.Database.Notifications;
import seng302.Server;


import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushAPI {
    /**
     * URL to send API requests to
     */
    private final String URL_BASE = "https://appcenter.ms/api/v0.1/apps/%s/%s/push/notifications/";
    private final String user = "jma326";
    private final String[] apps = {"transcend-Android", "transcend-iOS"};
    private final String token = (String) Server.getInstance().getConfig().get("vs_token");
    private String[] urls = new String[apps.length];

    private HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

    private Notifications notificationsDatabase = new Notifications();

    private static PushAPI instance = new PushAPI();

    private PushAPI() {
        // Create a url for each app (iOS, Android, UWP etc.)
        for (int i = 0; i < apps.length; i++) {
            urls[i] = String.format(URL_BASE, user, apps[i]);
        }
    }

    public static PushAPI getInstance() {
        return instance;
    }

    /**
     * Sends a notification to each device on which the user with the given IF is logged in
     *
     * @param notification The notification object containing title, message etc.
     * @param user_id      The ID of the user to which the notification is being sent
     */
    public void sendNotification(Notification notification, String user_id) throws SQLException {
        List<String> devices = notificationsDatabase.getDevices(user_id);
        for (String url : urls) {
            // Convert notification to JSON
            HttpContent content = constructNotificationJson(devices, notification);
            HttpRequest request;
            try {
                request = requestFactory.buildPostRequest(new GenericUrl(url), content);
            } catch (IOException e) {
                Server.getInstance().log.error("Could not create API request");
                Server.getInstance().log.error(e.toString());
                return;
            }
            request.getHeaders().put("X-API-Token", token);
            // Execute the request on a seperate thread
            new Thread(() -> {
                try {
                    Server.getInstance().log.info("Notification sent: " + request.execute().parseAsString());
                } catch (IOException e) {
                    Server.getInstance().log.error("Could not send push notification");
                    Server.getInstance().log.error(e.toString());

                }
            }).start();
        }
    }

    /**
     * Takes a notification and a list of devices and converts into json http content to be sent in a post request
     * @param devices A list of Strings containing the device ids to be included in the notification
     * @param notification The Notification object containing the title, message etc.
     * @return JsonHttpContent to be sent in the POST
     */
    private JsonHttpContent constructNotificationJson(List<String> devices, Notification notification) {
        Map<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("notification_content", notification.getNotificationContent());
        Map<String, Object> targetMap = new HashMap<>();
        targetMap.put("type", "devices_target");
        targetMap.put("devices", devices.toArray());
        notificationMap.put("notification_target", targetMap);
        return new JsonHttpContent(new JacksonFactory(), notificationMap).setMediaType(new HttpMediaType("text/json"));
    }
}
