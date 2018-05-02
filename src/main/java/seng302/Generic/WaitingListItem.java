package seng302.Generic;

import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.time.LocalDateTime;
import java.util.Date;

public class WaitingListItem {

    protected Organ organType;
    protected String organRegisteredDate;
    protected Integer waitingListItemId;
    protected Long userId;


    public WaitingListItem(Organ organType) {
        this.organType = organType;
        this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        this.waitingListItemId = Main.getNextWaitingListId();
        this.userId = Main.getUserWindowController().getCurrentUser().getId();
    }

    public WaitingListItem(ReceiverWaitingListItem copy) {
        this.organType = copy.organType;
        this.organRegisteredDate = copy.organRegisteredDate;
        this.waitingListItemId = copy.waitingListItemId;
    }

    public WaitingListItem(Organ organ, String date, long id, Integer waitingListId) {
        this.organType = organ;
        this.organRegisteredDate = date;
        this.userId = id;
        this.waitingListItemId = waitingListId;
    }

    public WaitingListItem() {

    }

    public Organ getOrganType() {
        return organType;
    }

    public String getOrganRegisteredDate() {
        return organRegisteredDate;
    }

    public Long getUserId(){
        return userId;
    }

    public Integer getWaitingListItemId(){return waitingListItemId;}
}
