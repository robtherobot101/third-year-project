package seng302.Core;

import java.time.LocalDate;

/**
 * Class contains all the information for a given procedure used in the Medical History (Procedures) section.
 */
public class Procedure {

    private String summary;
    private String description;
    private LocalDate date;
    private boolean isOrganAffecting;

    public Procedure(String summary, String description, LocalDate date, boolean isOrganAffecting) {

        this.summary = summary;
        this.description = description;
        this.date = date;
        this.isOrganAffecting = isOrganAffecting;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isOrganAffecting() {
        return isOrganAffecting;
    }

    public void setOrganAffecting(boolean organAffecting) {
        isOrganAffecting = organAffecting;
    }

    @Override
    public String toString(){
        return summary;
    }


}
