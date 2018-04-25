package seng302.Core;

import java.time.LocalDate;

/**
 * Class contains all the information for a given procedure used in the Medical History (Procedures) section.
 */
public class Procedure {

    private String summary;
    private String description;
    private LocalDate Date;

    public Procedure(String summary, String description, LocalDate date) {

        this.summary = summary;
        this.description = description;
        Date = date;
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
        return Date;
    }

    public void setDate(LocalDate date) {
        Date = date;
    }


}
