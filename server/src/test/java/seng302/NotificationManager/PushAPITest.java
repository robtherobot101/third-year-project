package seng302.NotificationManager;

import com.google.gson.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PushAPITest {

    private List<String> devices1 = Arrays.asList("3486230475", "92183746912", "9817436915");

    private JsonParser jp = new JsonParser();
    private Gson gs = new Gson();

    /**
     * Takes a JsonElement and a list of strings pointing to a specific JsonElement
     *
     * @param je The root JsonElement
     * @param path The path to the desired element
     * @return The desired element
     */
    private JsonElement getObject (JsonElement je, String... path) {
        for(String node : path) {
            je = je.getAsJsonObject().get(node);
        }
        return je;
    }

    @Test
    public void constructNotificationJson1() {
        Notification notification = new Notification("New", "Notification");
        JsonElement je = jp.parse(notification.toJSON(devices1));
        ArrayList devices = gs.fromJson(getObject(je, "notification_target", "devices").getAsJsonArray(), ArrayList.class);
        // Ensure data was properly captured in the JSON string
        assertTrue(devices.containsAll(devices1));
        assertEquals("New", getObject(je, "notification_content", "title").getAsString());
        assertEquals("Notification", getObject(je, "notification_content", "body").getAsString());
    }

    @Test
    public void constructNotificationJson2 () {
        Notification notification = new Notification("New", "Notification");
        notification.addCustomData("key", "data");
        List<String> l = Arrays.asList("a", "b", "c");
        notification.addCustomData("array", l);
        Map<String, String> m = new HashMap<>();
        m.put("key1", "data1");
        m.put("key2", "data2");
        notification.addCustomData("object", m);
        JsonElement je = jp.parse(notification.toJSON(devices1));

        // Ensure the custom data was captured in the JSON string
        assertEquals("data", getObject(je, "notification_content", "custom_data", "key").getAsString());
        JsonArray ja = getObject(je, "notification_content", "custom_data", "array").getAsJsonArray();
        for (int i = 0; i < ja.size(); i++) {
            assertEquals(l.get(i), ja.get(i).getAsString());
        }
        assertEquals("data1", getObject(je, "notification_content", "custom_data", "object", "key1").getAsString());
        assertEquals("data2", getObject(je, "notification_content", "custom_data", "object",  "key2").getAsString());

    }
}