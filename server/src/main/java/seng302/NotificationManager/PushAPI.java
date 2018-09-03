package seng302.NotificationManager;


import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javafx.concurrent.Task;
import seng302.Server;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PushAPI {
    /** URL to send API requests to */
    private final String URL_BASE = "https://appcenter.ms/api/v0.1/apps/%s/%s/push/notifications/";
    private final String user = "jma326";
    private final String[] apps = {"transcend-Android", "transcend-iOS"};
    private final String token = (String) Server.getInstance().getConfig().get("vs_token");
    private String[] urls = new String[apps.length];

    private HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

    private static PushAPI instance = new PushAPI();

    private PushAPI() {
        // Create a url for each app (iOS, Android, UWP etc.)
        for(int i = 0; i < apps.length; i++) {
            urls[i] = String.format(URL_BASE, user, apps[i]);
        }
    }

    public static PushAPI getInstance() {
        return instance;
    }

    public void sendNotification(Notification notification) {

        for(String url : urls) {
            // Convert notification to JSON
            HttpContent content = new JsonHttpContent(new JacksonFactory(), notification.getNotification()).setMediaType(new HttpMediaType("text/json"));
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
}
