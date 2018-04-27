package seng302.Generic;

import java.time.LocalDate;
import seng302.User.Attribute.Organ;

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
