package seng302.data.database;

import seng302.User.Medication.Medication;
import seng302.generic.APIResponse;
import seng302.generic.APIServer;

import java.util.HashMap;
import java.util.List;

public class MedicationsDB {

    private final APIServer server;

    private MedicationsDB(APIServer server) { this.server = server; }

    public List<Medication> getMedications(long userId, String token) {
        APIResponse response = server.getRequest(new HashMap<>(), token, "users", String.valueOf(userId), "medications");
        return null;
    }
}
