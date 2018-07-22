package seng302.Model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

/**
 * Class contains all the information for a given disease used in the Medical History section.
 */
public class Disease {

    private String name;
    private LocalDate diagnosisDate;
    private boolean isChronic;
    private boolean isCured;
    private int id;

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * method to create a new disease instance
     *
     * @param name          name of disease
     * @param diagnosisDate diagnosis date of disease
     * @param isChronic     if the disease is chronic
     * @param isCured       if the disease is cured
     */
    public Disease(String name, LocalDate diagnosisDate, boolean isChronic, boolean isCured, int id) {
        this.name = name;
        this.diagnosisDate = diagnosisDate;
        this.isChronic = isChronic;
        this.isCured = isCured;
        this.id = id;
    }

    /**
     * returns id of disease
     * @return id of disease
     */
    public int getId() {
        return id;
    }

    /**
     * sets id of disease
     * @param id id of disease
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * returns the name of the disease
     *
     * @return String name of the disease
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the disease
     *
     * @param name String the name of the disease
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the diagnosis date of the disease
     *
     * @return LocalDate the date of the diagnosis
     */
    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    /**
     * sets the diagnosis date of the disease
     *
     * @param diagnosisDate LocalDate the date of diagnosis
     */
    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    /**
     * returns if the disease is chronic
     *
     * @return Boolean is the disease chronic
     */
    public boolean isChronic() {
        return isChronic;
    }

    /**
     * sets if the disease is chronic
     *
     * @param chronic Boolean if the disease is chronic
     */
    public void setChronic(boolean chronic) {
        isChronic = chronic;
    }

    /**
     * returns if the disease is cured
     *
     * @return Boolean if the disease is cured
     */
    public boolean isCured() {
        return isCured;
    }

    /**
     * sets if the disease is cured
     *
     * @param cured Boolean if the disease is cured
     */
    public void setCured(boolean cured) {
        isCured = cured;
    }

    /**
     * Comparator to compare 2 diseases in ascending order by name.
     */
    public static Comparator<Disease> ascNameComparator = (d1, d2) -> {
        if (d1.isChronic() && d2.isChronic()) {
            return d1.getName().compareTo(d2.getName());
        } else if (d1.isChronic()) {
            return 0;
        } else if (d2.isChronic()) {
            return 1;
        } else {
            return d1.getName().compareTo(d2.getName());
        }
    };

    /**
     * Comparator to compare 2 diseases in descending order by name.
     */
    public static Comparator<Disease> descNameComparator = (d1, d2) -> {
        if (d1.isChronic() && d2.isChronic()) {
            return d2.getName().compareTo(d1.getName());
        } else if (d1.isChronic()) {
            return 0;
        } else if (d2.isChronic()) {
            return 1;
        } else {
            return d2.getName().compareTo(d1.getName());
        }
    };

    /**
     * Comparator to compare 2 diseases in ascending order by date.
     */
    public static Comparator<Disease> ascDateComparator = (d1, d2) -> {
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
    };

    /**
     * Comparator to compare 2 diseases in descending order by date.
     */
    public static Comparator<Disease> descDateComparator = (d1, d2) -> {
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
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disease disease = (Disease) o;
        return isChronic == disease.isChronic &&
                isCured == disease.isCured &&
                id == disease.id &&
                Objects.equals(name, disease.name) &&
                Objects.equals(diagnosisDate, disease.diagnosisDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, diagnosisDate, isChronic, isCured, id);
    }
}
