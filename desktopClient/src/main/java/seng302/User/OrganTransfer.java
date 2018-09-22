package seng302.User;


import seng302.User.Attribute.Organ;
import seng302.generic.WindowManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class OrganTransfer {
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private LocalDateTime arrivalTime;
    private int id;
    private long receiverId;
    private Organ organType;


    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    private String receiverName;
    private String hospitalName;

    private Duration timeLeft;
    private String destinationRegion;

    public OrganTransfer(double startLat, double startLon, double endLat, double endLon, LocalDateTime arrivalTime, int organId, long receiverId, Organ organType){
        this.startLat = startLat;
        this.startLon = startLon;
        this.endLat = endLat;
        this.endLon = endLon;
        this.arrivalTime = arrivalTime;
        this.OrganId = organId;
        this.receiverId = receiverId;
        this.organType = organType;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public Organ getOrganType() {
        return organType;
    }

    public void setTimeLeft(Duration time) {
        timeLeft = time;
    }

    public void tickTimeLeft(){
        timeLeft = timeLeft.minus(1, SECONDS);
    }

    public Duration getTimeLeft(){
        return timeLeft;
    }

    public int getOrganId() {
        return id;
    }

    public void setDestinationRegion(String destinationRegion) {
        this.destinationRegion = destinationRegion;
    }


    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
}
