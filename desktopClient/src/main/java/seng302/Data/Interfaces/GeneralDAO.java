package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.Generic.APIResponse;
import seng302.Generic.Country;
import seng302.User.DonatableOrgan;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;

public interface GeneralDAO {
    // Now uses API server!
    Map<Object, String> loginUser(String usernameEmail, String password) throws HttpResponseException;

    void logoutUser(String token) throws HttpResponseException;

    void reset(String token) throws HttpResponseException;

    void resample(String token) throws HttpResponseException;

    String sendCommand(String command, String token);

    boolean isUniqueIdentifier(String username) throws HttpResponseException;

    List<WaitingListItem> getAllWaitingListItems(String token) throws HttpResponseException;

    boolean status() throws HttpResponseException, ConnectException;

    List<Country> getAllCountries(String token) throws HttpResponseException;

    void updateCountries(List<Country> countries, String token) throws HttpResponseException;

    List<DonatableOrgan> getAllDonatableOrgans(String Token) throws HttpResponseException;
}
