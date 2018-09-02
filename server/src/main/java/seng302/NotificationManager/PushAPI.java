package seng302.NotificationManager;


import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PushAPI {
    /** URLs to send API requests to */
    private final String URL_BASE = "https://appcenter.ms/api/v0.1/apps/%s/%s/push/notifications/";
    private String[] urls;
    private String token;

    public PushAPI(String user, String[] apps, String token) {
        // Create a url for each app (iOS, Android, UWP etc.)
        urls = new String[apps.length];
        for(int i = 0; i < apps.length; i++) {
            urls[i] = String.format(URL_BASE, user, apps[i]);
        }

        this.token = token;
    }

    public void sendNotification(String title, String message) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        for(String url : urls) {
            Map<String, Object> notificationContent = new HashMap<>();
            notificationContent.put("name", "Test");
            notificationContent.put("title", "Test");
            notificationContent.put("body", "This is a test notification from Java");
            Map<String ,Object> root = new HashMap<>();
            root.put("notification_content", notificationContent);
            HttpContent content = new JsonHttpContent(new JacksonFactory(), root).setMediaType(new HttpMediaType("text/json"));
            content.writeTo(System.out);
            HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(url), content);
            request.getHeaders().put("X-API-Token", token);
            String rawResponse = request.execute().parseAsString();
            System.out.println(rawResponse);
        }
    }
}
