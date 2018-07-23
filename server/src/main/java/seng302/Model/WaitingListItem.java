package seng302.Model;

import seng302.Model.Attribute.Organ;

import java.time.LocalDate;


/**
 * Contains information for a waiting list record.
 */
public class WaitingListItem {

    private Organ organType;
    private LocalDate organRegisteredDate;
    private int id;
    private int userId;
    private LocalDate organDeregisteredDate;
    private int organDeregisteredCode;
    private boolean isConflicting = false;

    public WaitingListItem(Organ organType, LocalDate organRegisteredDate, int id, int userId, LocalDate organDeregisteredDate, int organDeregisteredCode) {
        this.organType = organType;
        this.organRegisteredDate = organRegisteredDate;
        this.id = id;
        this.userId = userId;
        this.organDeregisteredDate = organDeregisteredDate;
        this.organDeregisteredCode = organDeregisteredCode;
    }

    public WaitingListItem(Organ organType, LocalDate organRegisteredDate, int id, int userId, LocalDate organDeregisteredDate, int organDeregisteredCode, boolean isConflicting) {
        this.organType = organType;
        this.organRegisteredDate = organRegisteredDate;
        this.id = id;
        this.userId = userId;
        this.organDeregisteredDate = organDeregisteredDate;
        this.organDeregisteredCode = organDeregisteredCode;
        this.isConflicting = isConflicting;
    }


    public Organ getOrganType() {
        return organType;
    }

    public LocalDate getOrganRegisteredDate() {
        return organRegisteredDate;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDate getOrganDeregisteredDate() {
        return organDeregisteredDate;
    }

    public int getOrganDeregisteredCode() {
        return organDeregisteredCode;
    }



}
