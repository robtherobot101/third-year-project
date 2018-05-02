package seng302.Core;

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
