package seng302.User;

import seng302.User.Attribute.Organ;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class DonatableOrgan {

    private LocalDateTime timeOfDeath;
    private Organ organType;
    private long donorId;
    private int id;
    private Duration timeLeft;
    private String receiverName;
    private String receiverRegion;
    private Long timePercent;


    public DonatableOrgan(LocalDateTime timeOfExpiry, Organ organType, long donorId, int id){
        this.timeOfDeath = timeOfExpiry;
        this.donorId = donorId;
        this.organType = organType;
        this.id = id;
    }

    public DonatableOrgan(LocalDateTime timeOfExpiry, Organ organType, long donorId){
        this.timeOfDeath = timeOfExpiry;
        this.donorId = donorId;
        this.organType = organType;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiverRegion(String receiverRegion) {
        this.receiverRegion = receiverRegion;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverRegion() {
        return receiverRegion;
    }

    public Long getTimePercent() {
        return timePercent;
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

    public void setDateOfDeath(LocalDateTime timeOfExpiry) {
        this.timeOfDeath = timeOfExpiry;
    }

    public Duration getTimeLeft(){
        return timeLeft;
    }

    public void setTimeLeft(Duration time) {
        timeLeft = time;
        timePercent = time.toMillis() / getExpiryDuration(organType).toMillis();
    }

    public void tickTimeLeft(){
        timeLeft = timeLeft.minus(1, SECONDS);
        timePercent = timeLeft.toMillis() / getExpiryDuration(organType).toMillis();
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
                duration = Duration.parse("PT10H");
                break;
            case CORNEA:
                duration = Duration.parse("P7D");
                break;
            case EAR:
                duration = Duration.parse("P3650D");//Todo this is unknown and is a place holder
                break;
            case TISSUE:
                duration = Duration.parse("P1825D");
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
