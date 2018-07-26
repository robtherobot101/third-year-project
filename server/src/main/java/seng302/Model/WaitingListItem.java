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

    /**
     * constructor method to create a new waiting list item object
     * @param organType Organ the type of organ a user is waiting on
     * @param organRegisteredDate LocalDate the date of the waiting list item registration
     * @param id int the id of the waiting list entry
     * @param userId int the id of the user that is waiting on the organ
     * @param organDeregisteredDate LocalDate the date that the entry was de-registered
     * @param organDeregisteredCode int the code for the de-registration
     */
    public WaitingListItem(Organ organType, LocalDate organRegisteredDate, int id, int userId, LocalDate organDeregisteredDate, int organDeregisteredCode) {
        this.organType = organType;
        this.organRegisteredDate = organRegisteredDate;
        this.id = id;
        this.userId = userId;
        this.organDeregisteredDate = organDeregisteredDate;
        this.organDeregisteredCode = organDeregisteredCode;
    }

    /**
     * constructor method to create a new waiting list item object
     * @param organType Organ the type of organ a user is waiting on
     * @param organRegisteredDate LocalDate the date of the waiting list item registration
     * @param id int the id of the waiting list entry
     * @param userId int the id of the user that is waiting on the organ
     * @param organDeregisteredDate LocalDate the date that the entry was de-registered
     * @param organDeregisteredCode int the code for the de-registration
     * @param isConflicting boolean if the user is waiting on the organ and also a donor
     */
    public WaitingListItem(Organ organType, LocalDate organRegisteredDate, int id, int userId, LocalDate organDeregisteredDate, int organDeregisteredCode, boolean isConflicting) {
        this.organType = organType;
        this.organRegisteredDate = organRegisteredDate;
        this.id = id;
        this.userId = userId;
        this.organDeregisteredDate = organDeregisteredDate;
        this.organDeregisteredCode = organDeregisteredCode;
        this.isConflicting = isConflicting;
    }

    /**
     * method to get the organ type of the waiting list item
     * @return Organ the type of organ
     */
    public Organ getOrganType() {
        return organType;
    }

    /**
     * method to get the register date of the waiting list item
     * @return LocalDate the registration date
     */
    public LocalDate getOrganRegisteredDate() {
        return organRegisteredDate;
    }

    /**
     * method to get the id of the waiting list item object
     * @return int the id of the waiting list item
     */
    public int getId() {
        return id;
    }

    /**
     * method to get the id of the user that is waiting on the organ
     * @return int the id of the user on the waiting list
     */
    public int getUserId() {
        return userId;
    }

    /**
     * method to get the organ de-registered date, can be null
     * @return LocalDate the de-registered date of a waiting list item
     */
    public LocalDate getOrganDeregisteredDate() {
        return organDeregisteredDate;
    }

    /**
     * method to get the deregister code of a waitinglist object, can be null
     * @return int the deregister code of the waiting list item
     */
    public int getOrganDeregisteredCode() {
        return organDeregisteredCode;
    }



}
