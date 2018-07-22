package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.Generic.APIResponse;

public interface GeneralDAO {
    // Now uses API server!
    APIResponse loginUser(String usernameEmail, String password);

    void resetDatabase() throws HttpResponseException;

    void loadSampleData() throws HttpResponseException;

    String sendCommand(String command);

    boolean isUniqueIdentifier(String username) throws HttpResponseException;
}
