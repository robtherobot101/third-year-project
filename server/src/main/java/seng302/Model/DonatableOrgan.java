package seng302.Model;

import seng302.Model.Attribute.Organ;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class DonatableOrgan {

    private LocalDateTime timeOfExpiry;
    private Organ organType;
    private long donorId;
    private int id;
    private Duration timeLeft;


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

    public LocalDateTime getTimeOfExpiry() {
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

    public void setTimeOfExpiry(LocalDateTime timeOfExpiry) {
        this.timeOfExpiry = timeOfExpiry;
    }

    public Duration getTimeLeft(){
        return timeLeft;
    }

    public void setTimeLeft(Duration time) {
        timeLeft = time;
    }

    public void tickTimeLeft(){
        timeLeft.minus(1, SECONDS);
    }

}

