package seng302.Generic;

import java.time.LocalDateTime;

import seng302.User.Attribute.Organ;
import seng302.User.User;

/**
 * Contains information for a transplant waiting list record.
 */
public class ReceiverWaitingListItem extends WaitingListItem{
    public String organDeregisteredDate;
    public boolean stillWaitingOn;
    public Integer organDeregisteredCode;


    public ReceiverWaitingListItem(Organ organType){
        super(organType);
        this.stillWaitingOn = true;
    }

    /**
     * Creates a new object as a deep copy of a current object.
     * Used to fix an error with undo/redo modifying old objects on the stack on deregister.
     * @param copy the original waiting list item.
     */
    public ReceiverWaitingListItem(ReceiverWaitingListItem copy) {
        super(copy);
        this.organDeregisteredDate = copy.organDeregisteredDate;
        this.stillWaitingOn = copy.stillWaitingOn;
        this.organDeregisteredCode = copy.organDeregisteredCode;
    }

    /**
     * Updates an organs registration date and removes its deregistration date.
     * Can be called when registering a previously deregistered organ.
     */
    public void registerOrgan(){
        this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        this.organDeregisteredCode = null;
        this.stillWaitingOn = true;
        this.organDeregisteredDate = null;
    }

    /**
     * Updates an organs deregistration date and removes its registration date.
     * Can be called when deregistering a previously registered organ.
     */
    public void deregisterOrgan(Integer reasonCode){
        if (this.organDeregisteredDate == null) {
            ReceiverWaitingListItem temp;
            if (reasonCode != 3){
                User selectedUser = Main.getUserById(this.getUserId());
                selectedUser.getWaitingListItems().remove(this);
                temp = new ReceiverWaitingListItem(this);
                selectedUser.getWaitingListItems().add(temp);
                temp.organDeregisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
                temp.organDeregisteredCode = reasonCode;
                temp.stillWaitingOn = false;
            } else {
                this.organDeregisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
                this.organDeregisteredCode = reasonCode;
            }

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
        return user.getOrgans().contains(getOrganType());
    }

    public String getOrganDeregisteredDate() {
        return organDeregisteredDate;
    }

    public boolean isStillWaitingOn() { return stillWaitingOn; }

    public Integer getOrganDeregisteredCode() { return organDeregisteredCode; }

    public Integer getWaitingListItemId() { return waitingListItemId; }


}
