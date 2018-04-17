package seng302.Core;

import java.time.LocalDate;

public class WaitingListItem {
    private Organ organType;
    private LocalDate organRegisteredDate;
    private LocalDate organDeregisteredDate;

    public WaitingListItem(Organ organType){
        this.organType = organType;
        this.organRegisteredDate = LocalDate.now();
    }

    public Organ getOrganType() {
        return organType;
    }

    public LocalDate getOrganRegisteredDate() {
        return organRegisteredDate;
    }

    public LocalDate getOrganDeregisteredDate() {
        return organDeregisteredDate;
    }
}
