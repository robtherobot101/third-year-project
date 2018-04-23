package seng302.Core;

import java.time.LocalDate;

/**
 * Class contains all the information for a given disease used in the Medical History section.
 */
public class Disease {

    private String name;
    private LocalDate diagnosisDate;
    private boolean isChronic;
    private boolean isCured;

    public Disease(String name, LocalDate diagnosisDate, boolean isChronic, boolean isCured) {
        this.name = name;
        this.diagnosisDate = diagnosisDate;
        this.isChronic = isChronic;
        this.isCured = isCured;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public boolean isChronic() {
        return isChronic;
    }

    public void setChronic(boolean chronic) {
        isChronic = chronic;
    }

    public boolean isCured() {
        return isCured;
    }

    public void setCured(boolean cured) {
        isCured = cured;
    }
}
