package seng302.User;

import seng302.User.Attribute.Organ;

import java.time.LocalDateTime;

public class DonatableOrgan {

    private LocalDateTime timeOfExpiry;
    private Organ organType;
    private long donorId;
    private int id;


    public DonatableOrgan(LocalDateTime timeOfExpiry, Organ organType, long donorId, int id){
        this.timeOfExpiry = timeOfExpiry;
        this.donorId = donorId;
        this.organType = organType;
        this.id = id;
    }

    public DonatableOrgan(LocalDateTime timeOfExpiry, Organ organType, long donorId){
        this.timeOfExpiry = timeOfExpiry;
        this.donorId = donorId;
        this.organType = organType;
    }

    public LocalDateTime getDateOfDeath() {
        return timeOfExpiry;
    }

    public long getDonorId() {
        return donorId;
    }

    public Organ getOrganType() {
        return organType;
    }

    public int getId() {
        return id;
    }

    public void setDateOfDeath(LocalDateTime timeOfExpiry) {
        this.timeOfExpiry = timeOfExpiry;
    }
}
