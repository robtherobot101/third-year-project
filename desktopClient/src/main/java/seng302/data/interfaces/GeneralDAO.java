package seng302.data.interfaces;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.client.HttpResponseException;
import org.json.JSONObject;
import seng302.User.Hospital;
import seng302.User.OrganTransfer;
import seng302.generic.Country;
import seng302.User.DonatableOrgan;
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

    List<WaitingListItem> getAllWaitingListItems(Map<String, String> params, String token) throws HttpResponseException;

    boolean status() throws HttpResponseException, ConnectException;

    List<Country> getAllCountries(String token) throws HttpResponseException;

    void updateCountries(List<Country> countries, String token) throws HttpResponseException;

    Boolean checkPassword(String password, long id) throws HttpResponseException;

    List<DonatableOrgan> getAllDonatableOrgans(Map filterParams, String Token) throws HttpResponseException;

    JSONObject getPosition(String address) throws UnirestException;

    List<Hospital> getHospitals(String token) throws HttpResponseException;

    void insertTransfer(OrganTransfer transfer, String token) throws HttpResponseException;

    List<OrganTransfer> getAllOrganTransfers(String token) throws HttpResponseException;
}
