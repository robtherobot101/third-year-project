package seng302.NotificationManager;

import com.google.api.client.http.json.JsonHttpContent;
import com.google.gson.*;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.util.StringOutputStream;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class PushAPITest {

    private List<String> devices1 = Arrays.asList("3486230475", "92183746912", "9817436915");
    private List<String> devices2 = new ArrayList<>();

    private JsonParser jp = new JsonParser();
    private Gson gs = new Gson();

    private String jsonContentToString (JsonHttpContent content) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        content.writeTo(b);
        return new String(b.toByteArray());
    }

    private JsonArray getDevices(JsonElement je) {
        return je.getAsJsonObject().get("notification_target").getAsJsonObject().get("devices").getAsJsonArray();
    }

    @Test
    public void constructNotificationJson1() throws Exception {
        Notification notification = new Notification("New", "Notification");
        JsonHttpContent content = PushAPI.getInstance().constructNotificationJson(devices1, notification);

        JsonElement je = jp.parse(jsonContentToString(content));
        List<String> devices = gs.fromJson(getDevices(je), ArrayList.class);
        assertTrue(devices.containsAll(devices1));

    }
}