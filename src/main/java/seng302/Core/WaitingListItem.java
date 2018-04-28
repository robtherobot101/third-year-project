package seng302.Core;

import java.time.LocalDateTime;

public class WaitingListItem {
    private Organ organType;
    private String organRegisteredDate;
    private String organDeregisteredDate;
    private boolean stillWaitingOn;

    public WaitingListItem(Organ organType, User user){
        this.organType = organType;
        this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        this.stillWaitingOn = true;
    }

    /**
     * Updates an organs registration date and removes its deregistration date.
     * Called when registering a previously deregistered organ.
     */
    public void registerOrgan(){
        if (this.organRegisteredDate == null) {
            this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        }
        this.organDeregisteredDate = null;
    }

    /**
     * Updates an organs deregistration date and removes its registration date.
     * Called when deregistering a previously registered organ.
     */
    public void deregisterOrgan(){
        if (this.organDeregisteredDate == null) {
            this.organDeregisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        }
        this.stillWaitingOn = false;
        this.organRegisteredDate = null;
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
