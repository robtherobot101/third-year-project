package seng302.Generic;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Class contains all the information for a given disease used in the Medical History section.
 */
public class Disease {

    private String name;
    private LocalDate diagnosisDate;
    private boolean isChronic;
    private boolean isCured;

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * method to create a new disease instance
     * @param name name of disease
     * @param diagnosisDate diagnosis date of disease
     * @param isChronic if the disease is chronic
     * @param isCured if the disease is cured
     */
    public Disease(String name, LocalDate diagnosisDate, boolean isChronic, boolean isCured) {
        this.name = name;
        this.diagnosisDate = diagnosisDate;
        this.isChronic = isChronic;
        this.isCured = isCured;
    }

    /**
     * returns the name of the disease
     * @return String name of the disease
     */
    public String getName() { return name; }

    /**
     * sets the name of the disease
     * @param name String the name of the disease
     */
    public void setName(String name) { this.name = name; }

    /**
     * returns the diagnosis date of the disease
     * @return LocalDate the date of the diagnosis
     */
    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    /**
     * sets the diagnosis date of the disease
     * @param diagnosisDate LocalDate the date of diagnosis
     */
    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    /**
     * returns if the disease is chronic
     * @return Boolean is the disease chronic
     */
    public boolean isChronic() {
        return isChronic;
    }

    /**
     * sets if the disease is chronic
     * @param chronic Boolean if the disease is chronic
     */
    public void setChronic(boolean chronic) {
        isChronic = chronic;
    }

    /**
     * returns if the disease is cured
     * @return Boolean if the disease is cured
     */
    public boolean isCured() {
        return isCured;
    }

    /**
     * sets if the disease is cured
     * @param cured Boolean if the disease is cured
     */
    public void setCured(boolean cured) {
        isCured = cured;
    }

    public static Comparator<Disease> ascNameComparator = new Comparator<Disease>() {
        @Override
        public int compare(Disease d1, Disease d2) {
            if (d1.isChronic() && d2.isChronic()) {
                return d1.getName().compareTo(d2.getName());
            } else if (d1.isChronic()) {
                return 0;
            } else if (d2.isChronic()) {
                return 1;
            } else {
                return d1.getName().compareTo(d2.getName());
            }
        }
    };

    public static Comparator<Disease> descNameComparator = new Comparator<Disease>() {
        @Override
        public int compare(Disease d1, Disease d2) {
            if (d1.isChronic() && d2.isChronic()) {
                return d2.getName().compareTo(d1.getName());
            } else if (d1.isChronic()) {
                return 0;
            } else if (d2.isChronic()) {
                return 1;
            } else {
                return d2.getName().compareTo(d1.getName());
            }
        }
    };

    public static Comparator<Disease> ascDateComparator = new Comparator<Disease>() {
        @Override
        public int compare(Disease d1, Disease d2) {
            if (d1.isChronic() && d2.isChronic()) {
                if (d1.getDiagnosisDate().isBefore(d2.getDiagnosisDate())) {
                    return 1;
                } else {
                    return 0;
                }
            } else if (d1.isChronic()) {
                return 0;
            } else if (d2.isChronic()) {
                return 1;
            } else {
                if (d1.getDiagnosisDate().isBefore(d2.getDiagnosisDate())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    };

    public static Comparator<Disease> descDateComparator = new Comparator<Disease>() {
        @Override
        public int compare(Disease d1, Disease d2) {
            if (d1.isChronic() && d2.isChronic()) {
                if (d2.getDiagnosisDate().isBefore(d1.getDiagnosisDate())) {
                    return 1;
                } else {
                    return 0;
                }
            } else if (d1.isChronic()) {
                return 0;
            } else if (d2.isChronic()) {
                return 1;
            } else {
                if (d2.getDiagnosisDate().isBefore(d1.getDiagnosisDate())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    };
}
