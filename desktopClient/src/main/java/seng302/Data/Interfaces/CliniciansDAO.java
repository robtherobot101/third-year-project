package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.User.Clinician;

import java.util.ArrayList;

public interface CliniciansDAO {
    void insertClinician(Clinician clinician) throws HttpResponseException;

    void updateClinician(Clinician clinician) throws HttpResponseException;

    ArrayList<Clinician> getAllClinicians() throws HttpResponseException;

    Clinician getClinician(long id) throws HttpResponseException;

    void removeClinician(Clinician clinician) throws HttpResponseException;


}
