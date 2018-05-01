package seng302.Generic;

import java.time.LocalDate;

import java.time.LocalDateTime;

import seng302.User.Attribute.Organ;
import seng302.User.User;


public class WaitingListItem {
    private Organ organType;
    private String organRegisteredDate;
    private String organDeregisteredDate;
    private Integer organDeregisteredCode;

    public WaitingListItem(Organ organType){
        this.organType = organType;
        this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
    }

    public void registerOrgan(){
        if (this.organRegisteredDate == null) {
            this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        }
        this.organDeregisteredDate = null;
    }

    public void deregisterOrgan(Integer reasonCode){
        if (this.organDeregisteredDate == null) {
            this.organDeregisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
            this.organDeregisteredCode = reasonCode;
        }
        this.organRegisteredDate = null;
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
