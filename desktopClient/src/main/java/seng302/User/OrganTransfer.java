package seng302.User;


import seng302.User.Attribute.Organ;

import java.time.LocalDateTime;

public class OrganTransfer {
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private LocalDateTime arrivalTime;
    private int id;
    private long receiverId;
    private Organ organType;

    public OrganTransfer(double startLat, double startLon, double endLat, double endLon, LocalDateTime arrivalTime, int id, long receiverId, Organ organType){
        this.startLat = startLat;
        this.startLon = startLon;
        this.endLat = endLat;
        this.endLon = endLon;
        this.arrivalTime = arrivalTime;
        this.id = id;
        this.receiverId = receiverId;
        this.organType = organType;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLon() {
        return endLon;
    }

    public double getStartLat() {
        return startLat;
    }

    public double getStartLon() {
        return startLon;
    }

    public int getOrganId() {
        return id;
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
}
