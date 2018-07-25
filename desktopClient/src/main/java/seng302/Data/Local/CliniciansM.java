package seng302.Data.Local;

import seng302.Data.Interfaces.CliniciansDAO;
import seng302.Generic.Debugger;
import seng302.User.Clinician;

import java.util.ArrayList;
import java.util.List;

public class CliniciansM implements CliniciansDAO {

    private List<Clinician> clinicians;

    public CliniciansM() {
        this.clinicians = new ArrayList<>();
    }

    @Override
    public void insertClinician(Clinician clinician, String token) {
        long nextClinicianId = 0;
        for (Clinician c : clinicians) {
            if (c.getStaffID() > nextClinicianId) {
                nextClinicianId = c.getStaffID();
            }
        }
        clinician.setStaffID(nextClinicianId + 1);
        clinicians.add(clinician);
    }

    @Override
    public void updateClinician(Clinician Clinician, String token) {
        removeClinician(Clinician.getStaffID(), null);
        clinicians.add(Clinician);
    }

    @Override
    public List<Clinician> getAllClinicians(String token) {
        return clinicians;
    }

    @Override
    public Clinician getClinician(long id, String token) {
        for(Clinician a : clinicians) {
            if(a.getStaffID() == id) {
                return a;
            }
        }
        Debugger.log("Clinician with id: " + id + " not found. Returning null.");
        return null;
    }

    @Override
    public void removeClinician(long id, String token) {
        for(Clinician c : clinicians) {
            if(c.getStaffID() == id) {
                clinicians.remove(c);
                break;
            }
        }
    }

}
