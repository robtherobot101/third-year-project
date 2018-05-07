package seng302.Generic;

import java.time.LocalDate;

/**
 * Class contains all the information for a given procedure used in the Medical History (Procedures) section.
 */
public class Procedure {

    private String summary;
    private String description;
    private LocalDate date;
    private boolean isOrganAffecting;

    /**
     * method to create a new instance of a procedure object
     *
     * @param summary          the given procedure summary
     * @param description      the given procedure description
     * @param date             the given procedure date
     * @param isOrganAffecting the given organ to be operated on in the procedure
     */
    public Procedure(String summary, String description, LocalDate date, boolean isOrganAffecting) {
        this.summary = summary;
        this.description = description;
        this.date = date;
        this.isOrganAffecting = isOrganAffecting;
    }

    /**
     * returns the procedure summary
     *
     * @return String the procedure summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * sets the procedure summary
     *
     * @param summary given procedure summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * returns the procedure summary
     *
     * @return String the procedure description
     */
    public String getDescription() {
        return description;
    }

    /**
     * sets the procedure description
     *
     * @param description String given procedure summary
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * returns the date of the procedure given
     *
     * @return date of the procedure
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * sets the procedure date
     *
     * @param date date to set for the procedure
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * returns the organ the procedure is on
     *
     * @return organ the procedure is on
     */
    public boolean isOrganAffecting() {
        return isOrganAffecting;
    }

    /**
     * sets the organ the procedure is on
     *
     * @param organAffecting the organ the procedure is on
     */
    public void setOrganAffecting(boolean organAffecting) {
        isOrganAffecting = organAffecting;
    }

    @Override
    public String toString() {
        return summary;
    }


}
