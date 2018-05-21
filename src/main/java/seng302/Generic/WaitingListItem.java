package seng302.Generic;

import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Contains information for a transplant waiting list record.
 */
public class WaitingListItem {

    protected Organ organType;
    protected LocalDate organRegisteredDate;
    protected Integer waitingListItemId;
    protected Long userId;


    public WaitingListItem(Organ organType, int id, long userId) {
        this.organType = organType;
        this.organRegisteredDate = LocalDate.now();
        this.waitingListItemId = id;
        this.userId = userId;
    }

    public WaitingListItem(ReceiverWaitingListItem copy) {
        this.organType = copy.organType;
        this.organRegisteredDate = copy.organRegisteredDate;
        this.waitingListItemId = copy.waitingListItemId;
        this.userId = copy.userId;
    }

    public WaitingListItem(Organ organ, LocalDate date, long id, Integer waitingListId) {
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

    public LocalDate getOrganRegisteredDate() {
        return organRegisteredDate;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getWaitingListItemId() {
        return waitingListItemId;
    }
}
