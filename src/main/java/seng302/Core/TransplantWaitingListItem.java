package seng302.Core;

import java.util.Date;

public class TransplantWaitingListItem {

    private String Name;
    private String region;
    private Date date;
    private Organ organ;
    private long id;

    public TransplantWaitingListItem(String Name, String region, Date date, Organ organ, long id) {
        this.Name = Name;
        this.region = region;
        this.date = date;
        this.organ = organ;
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public String getName() {
        return Name;
    }

    public Date getDate() {
        return date;
    }

    public Organ getOrgan() {
        return organ;
    }

    public long getId() {
        return id;
    }
}
