package seng302.Core;

import java.time.LocalDate;

/**
 * Class contains all the information for a given disease used in the Medical History section.
 */
public class Disease {

    private String name;
    private LocalDate diagnosisDate;

    public Disease(String name, LocalDate diagnosisDate) {
        this.name = name;
        this.diagnosisDate = diagnosisDate;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }
}
