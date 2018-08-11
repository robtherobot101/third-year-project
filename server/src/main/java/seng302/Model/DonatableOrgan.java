package seng302.Model;

import seng302.Model.Attribute.Organ;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class DonatableOrgan {

    private LocalDateTime timeOfDeath;
    private Organ organType;
    private long donorId;
    private int id;
    private Duration timeLeft;
    private Boolean expired;


    public DonatableOrgan(LocalDateTime timeOfDeath, Organ organType, long donorId, int id, boolean expired){
        this.timeOfDeath = timeOfDeath;
        this.donorId = donorId;
        this.organType = organType;
        this.id = id;
        this.expired = expired;
    }

    public DonatableOrgan(LocalDateTime timeOfDeath, Organ organType, long donorId, boolean expired){
        this.timeOfDeath = timeOfDeath;
        this.donorId = donorId;
        this.organType = organType;
        this.expired = expired;
    }

    public LocalDateTime getTimeOfDeath() {
        return timeOfDeath;
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

    public void setTimeOfDeath(LocalDateTime timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
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

