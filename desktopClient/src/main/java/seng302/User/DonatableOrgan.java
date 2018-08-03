package seng302.User;

import seng302.User.Attribute.Organ;

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

    public Duration getTimeLeft(){
        return timeLeft;
    }

    public void setTimeLeft(Duration time) {
        timeLeft = time;
    }

    public void tickTimeLeft(){
        timeLeft.minus(1, SECONDS);
    }

    /**
     * Returns a duration of how long the organ will last based on the organ type entered.
     * @param organType The organ type being donated
     * @return How long the organ will last
     */
    public Duration getExpiryDuration(Organ organType) {
        //TODO Find the duration of these
        Duration duration = null;
        switch(organType){

            case LUNG:
                duration = Duration.parse("PT6H");
                break;
            case HEART:
                duration = Duration.parse("PT6H");
                break;
            case PANCREAS:
                duration = Duration.parse("PT24H");
                break;
            case LIVER:
                duration = Duration.parse("PT24H");
                break;
            case KIDNEY:
                duration = Duration.parse("PT72H");
                break;
            case INTESTINE:
                break;
            case CORNEA:
                duration = Duration.parse("P7D");
                break;
            case EAR:
                break;
            case TISSUE:
                break;
            case SKIN:
                duration = Duration.parse("P3650D");
                break;
            case BONE:
                duration = Duration.parse("P3650D");
                break;

        }
        return duration;
    }
}
