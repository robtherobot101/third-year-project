package seng302.data.interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.User.Clinician;

import java.util.Collection;

public interface CliniciansDAO {

    void insertClinician(Clinician clinician, String token) throws HttpResponseException;

    void updateClinician(Clinician clinician, String token) throws HttpResponseException;

    void updateAccount(long id, String username, String password, String token) throws HttpResponseException;

    Collection<Clinician> getAllClinicians(String token) throws HttpResponseException;

    Clinician getClinician(long id, String token) throws HttpResponseException;

    void removeClinician(long id, String token) throws HttpResponseException;
}
