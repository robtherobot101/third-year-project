package seng302.NotificationManager;


import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import seng302.Logic.Database.Notifications;
import seng302.Server;


import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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
     * @param user_ids      The IDs of the users to which the notification is being sent
     */
    public void sendNotification(Notification notification, List<String> user_ids) {
        // Get the devices on which the users are logged on
        List<String> devices = getDevices(user_ids);
        if(devices.size() > 0) {
            for (String url : urls) {
                // Convert notification to JSON
                HttpContent content = constructNotificationJson(devices, notification);
                // Create a request
                HttpRequest request = createRequest(content, token, url);
                // Execute the request
                if (request != null) {
                    executeRequest(request).start();
                }
            }
        }
    }

    /**
     * Sends a message to all memmbers of a conversation
     * @param message A message object simply holding the message text
     * @param conversationId The id of the conversation
     */
    public void sendMessage(Message message, int conversationId) {
        // Get the ids of users in the given conversation and assign the message to their devices
        sendNotification(message, new ArrayList<>());
    }

    /**
     * Gets a list of device ids on which the given users are logged in
     * @param user_ids The IDs of the users
     * @return A list of Strings representing the IDs of each device the user with the given ID is logged in on
     */
    private List<String> getDevices(List<String> user_ids) {
        try {
            Server.getInstance().log.info("Getting devices logged in by users with ids " + user_ids.toString());
            return notificationsDatabase.getDevices(user_ids);
        } catch (SQLException e) {
            Server.getInstance().log.info("Failed to get devices");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Takes a notification and a list of devices and converts into json http content to be sent in a post request
     * @param devices A list of Strings containing the device ids to be included in the notification
     * @param notification The Notification object containing the title, message etc.
     * @return JsonHttpContent to be sent in the POST
     */
    JsonHttpContent constructNotificationJson(List<String> devices, Notification notification) {
        Map<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("notification_content", notification.getNotificationContent());
        Map<String, Object> targetMap = new HashMap<>();
        targetMap.put("type", "devices_target");
        targetMap.put("devices", devices.toArray());
        notificationMap.put("notification_target", targetMap);
        return new JsonHttpContent(new JacksonFactory(), notificationMap).setMediaType(new HttpMediaType("text/json"));
    }

    /**
     * Create an HTTP POST request to the push API with the given content. Adds the API token.
     * @param content The JSON content of the notification
     * @param token The Push API authorisation token
     * @param url The url of the Push API (with app and user name)
     * @return An HTTP request to the API to send the notification
     */
    private HttpRequest createRequest(HttpContent content, String token, String url) {
        try {
            HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(url), content);
            request.getHeaders().put("X-API-Token", token);
            return request;
        } catch (IOException e) {
            Server.getInstance().log.error("Could not create API request");
            Server.getInstance().log.error(e.toString());
            return null;
        }
    }


    /**
     * Creates a thread on which to execute an HTTP request
     * @param request The POST request to be sent
     * @return The thread to be started
     */
    private Thread executeRequest(HttpRequest request) {
        return new Thread(() -> {
            try {
                Server.getInstance().log.info("Notification sent: " + request.execute().parseAsString());
            } catch (IOException e) {
                Server.getInstance().log.error("Could not send push notification");
                Server.getInstance().log.error(e.toString());

            }
        });
    }
}
