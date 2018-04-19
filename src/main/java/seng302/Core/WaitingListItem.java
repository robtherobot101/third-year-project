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

    public void registerOrgan(){
        this.organRegisteredDate = LocalDate.now();
        this.organDeregisteredDate = null;
    }

    public void deregisterOrgan(){
        this.organDeregisteredDate = LocalDate.now();
        this.organRegisteredDate = null;
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
