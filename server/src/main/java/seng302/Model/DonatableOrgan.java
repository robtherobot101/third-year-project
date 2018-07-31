package seng302.Model;

import seng302.Model.Attribute.Organ;

import java.time.LocalDateTime;

public class DonatableOrgan {

    private LocalDateTime dateOfDeath;
    private Organ organType;
    private long donorId;


    public DonatableOrgan(LocalDateTime dateOfDeath, Organ organType, long donorId){
        this.dateOfDeath = dateOfDeath;
        this.donorId = donorId;
        this.organType = organType;
    }

    public LocalDateTime getDateOfDeath() {
        return dateOfDeath;
    }

    public long getDonorId() {
        return donorId;
    }

    public Organ getOrganType() {
        return organType;
    }

    public void setDateOfDeath(LocalDateTime dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }
}
