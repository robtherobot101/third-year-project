package seng302.Generic;

import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contains information for a transplant waiting list record.
 */
public class ReceiverWaitingListItem extends WaitingListItem {

    public LocalDate organDeregisteredDate;
    public boolean stillWaitingOn;
    public Integer organDeregisteredCode;


    /**
     * creates a new object with a given object type
     *
     * @param organType type of organ to use
     */
    public ReceiverWaitingListItem(Organ organType, int id, long userId) {
        super(organType, id, userId);
        this.stillWaitingOn = true;
    }

    /**
     * Creates a new object as a deep copy of a current object.
     * Used to fix an error with undo/redo modifying old objects on the stack on deregister.
     *
     * @param copy the original waiting list item.
     */
    public ReceiverWaitingListItem(ReceiverWaitingListItem copy) {
        super(copy);
        this.organDeregisteredDate = copy.organDeregisteredDate;
        this.stillWaitingOn = copy.stillWaitingOn;
        this.organDeregisteredCode = copy.organDeregisteredCode;
    }

    public ReceiverWaitingListItem(Organ organType, LocalDate registeredDate, LocalDate deregisteredDate, Long userId, Integer deregisteredCode, Integer waitingListitemId) {
        this.organType = organType;
        this.organRegisteredDate = registeredDate;
        this.organDeregisteredDate = deregisteredDate;
        this.userId = userId;
        this.organDeregisteredCode = deregisteredCode;
        this.waitingListItemId = waitingListitemId;
        if (deregisteredDate == null) { this.stillWaitingOn = true; } else { this.stillWaitingOn = false;}
    }

    /**
     * Creates a new object with restricted parameters
     * Used in testing
     *
     * @param heart objects organType
     * @param id    objects user id
     */
    public ReceiverWaitingListItem(Organ heart, Long id) {
        this.organType = heart;
        this.userId = id;
        this.organRegisteredDate = LocalDate.now();
        this.stillWaitingOn = true;
    }

    /**
     * Updates an organs registration date and removes its deregistration date.
     * Can be called when registering a previously deregistered organ.
     */
    public void registerOrgan() {
        this.organRegisteredDate = LocalDate.now();
        this.organDeregisteredCode = null;
        this.stillWaitingOn = true;
        this.organDeregisteredDate = null;
    }

    /**
     * Updates an organs deregistration date and removes its registration date.
     * Can be called when deregistering a previously registered organ.
     *
     * @param reasonCode reason code to assign to an organ upon deregister
     */
    public void deregisterOrgan(Integer reasonCode) {
        if (this.organDeregisteredDate == null) {
            ReceiverWaitingListItem temp;
            if (reasonCode != 3) {
                User selectedUser = SearchUtils.getUserById(this.getUserId());
                selectedUser.getWaitingListItems().remove(this);
                temp = new ReceiverWaitingListItem(this);
                selectedUser.getWaitingListItems().add(temp);
                temp.organDeregisteredDate = LocalDate.now();
                temp.organDeregisteredCode = reasonCode;
                temp.stillWaitingOn = false;
            } else {
                this.organDeregisteredDate = LocalDate.now();
                this.organDeregisteredCode = reasonCode;
            }

        }
        this.stillWaitingOn = false;
    }

    /**
     * gets the still waiting on attribute of a reciever object
     *
     * @return boolean if an organ transplant is still pending
     */
    public boolean getStillWaitingOn() {
        return stillWaitingOn;
    }

    /**
     * Returns whether or not a user is also donating an organ they are hoping to receive.
     *
     * @param user the user being tested.
     * @return true if the organ is also being donated, otherwise false.
     */
    public boolean isDonatingOrgan(User user) {
        return user.getOrgans().contains(getOrganType());
    }

    /**
     * returns the de-registered date of the object selected
     *
     * @return de-registered date of an object
     */
    public LocalDate getOrganDeregisteredDate() {
        return organDeregisteredDate;
    }

    /**
     * returns the de-registered code of the object selected
     *
     * @return de-registered code of an object
     */
    public Integer getOrganDeregisteredCode() {
        return organDeregisteredCode;
    }

    /**
     * returns the waitingListItemId of a given object
     *
     * @return waitingListItemId of an object
     */
    public Integer getWaitingListItemId() {
        return waitingListItemId;
    }


}
