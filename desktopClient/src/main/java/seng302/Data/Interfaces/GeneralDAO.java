package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.Generic.APIResponse;

public interface GeneralDAO {
    // Now uses API server!
    Object loginUser(String usernameEmail, String password) throws HttpResponseException;

    void reset() throws HttpResponseException;

    void resample() throws HttpResponseException;

    String sendCommand(String command);

    boolean isUniqueIdentifier(String username) throws HttpResponseException;
}
