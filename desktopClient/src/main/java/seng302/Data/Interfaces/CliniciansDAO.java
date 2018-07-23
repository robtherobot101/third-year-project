package seng302.Data.Interfaces;

import org.apache.http.client.HttpResponseException;
import seng302.User.Clinician;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface CliniciansDAO {
    void insertClinician(Clinician clinician) throws HttpResponseException;

    void updateClinician(Clinician clinician) throws HttpResponseException;

    Collection<Clinician> getAllClinicians() throws HttpResponseException;

    Clinician getClinician(long id) throws HttpResponseException;

    void removeClinician(long id) throws HttpResponseException;
}
