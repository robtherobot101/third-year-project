package seng302.User;


import java.time.LocalDateTime;

public class OrganTransfer {
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private LocalDateTime arrivalTime;
    private int OrganId;
    private long receiverId;

    public OrganTransfer(double startLat, double startLon, double endLat, double endLon, LocalDateTime arrivalTime, int organId, long receiverId){
        this.startLat = startLat;
        this.startLon = startLon;
        this.endLat = endLat;
        this.endLon = endLon;
        this.arrivalTime = arrivalTime;
        this.OrganId = organId;
        this.receiverId = receiverId;
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
        return OrganId;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public long getReceiverId() {
        return receiverId;
    }
}
