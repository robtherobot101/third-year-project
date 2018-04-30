package seng302.Generic;

import java.time.LocalDate;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.time.LocalDateTime;

/**
 * Contains information for a transplant waiting list record.
 */
public class WaitingListItem {
    private Organ organType;
    private String organRegisteredDate;
    private String organDeregisteredDate;
    private boolean stillWaitingOn;

    public WaitingListItem(Organ organType){
        this.organType = organType;
        this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        this.stillWaitingOn = true;
    }

    /**
     * Creates a new object as a deep copy of a current object.
     * Used to fix an error with undo/redo modifying old objects on the stack on deregister.
     * @param copy the original waiting list item.
     */
    public WaitingListItem(WaitingListItem copy) {
        this.organType = copy.organType;
        this.organRegisteredDate = copy.organRegisteredDate;
        this.organDeregisteredDate = copy.organDeregisteredDate;
        this.stillWaitingOn = copy.stillWaitingOn;
    }

    /**
     * Updates an organs registration date and removes its deregistration date.
     * Can be called when registering a previously deregistered organ.
     */
    public void registerOrgan(){
        if (this.organRegisteredDate == null) {
            this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        }
        this.stillWaitingOn = true;
        this.organDeregisteredDate = null;
    }

    /**
     * Updates an organs deregistration date and removes its registration date.
     * Can be called when deregistering a previously registered organ.
     */
    public void deregisterOrgan(){
        if (this.organDeregisteredDate == null) {
            this.organDeregisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        }
        this.stillWaitingOn = false;
    }

    public boolean getStillWaitingOn(){
        return stillWaitingOn;
    }

    /**
     * Returns whether or not a user is also donating an organ they are hoping to receive.
     * @param user the user being tested.
     * @return true if the organ is also being donated, otherwise false.
     */
    public boolean isDonatingOrgan(User user){
        return user.getOrgans().contains(organType);
    }

    public Organ getOrganType() {
        return organType;
    }

    public String getOrganRegisteredDate() {
        return organRegisteredDate;
    }

    public String getOrganDeregisteredDate() {
        return organDeregisteredDate;
    }
}
