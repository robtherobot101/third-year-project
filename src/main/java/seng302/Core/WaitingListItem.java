package seng302.Core;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WaitingListItem {
    private Organ organType;
    private String organRegisteredDate;
    private String organDeregisteredDate;
    private User user;

    public WaitingListItem(Organ organType, User user){
        this.organType = organType;
        this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        this.user = user;
    }

    public void registerOrgan(){
        if (this.organRegisteredDate == null) {
            this.organRegisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        }
        this.organDeregisteredDate = null;
    }

    public void deregisterOrgan(){
        if (this.organDeregisteredDate == null) {
            this.organDeregisteredDate = User.dateTimeFormat.format(LocalDateTime.now());
        }
        this.organRegisteredDate = null;
    }

    public boolean getUserDonatingOrgan(){
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
