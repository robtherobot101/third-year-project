package seng302.NotificationManager;

import com.windowsazure.messaging.*;

public class Manager {
    public static void test() throws NotificationHubsException {
        NotificationHub hub = new NotificationHub("Endpoint=sb://transcendnamespace.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=kQeaiOxVZMqq07CizTI4wBIdzqqJE9YHNUrz7VakMuw=", "transcend");
        String message = "{\"data\":{\"msg\":\"Hello from Java!\"}}";
        Notification n = Notification.createGcmNotifiation(message);
        hub.sendNotification(n);
    }

}
