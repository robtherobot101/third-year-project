package seng302.Core;

import seng302.User.Attribute.Organ;

import java.util.Date;

/**
 * An object to store the necessary data for the transplant waiting list
 */
public class TransplantWaitingListItem {

    private String Name;
    private String region;
    private Date date;
    private Organ organ;
    private long id;

    /**
     * Constructor of the object.
     *
     * @param Name The name of the receiver.
     * @param region The region of the receiver.
     * @param date The date that the organ was registered.
     * @param organ The organ that the receiver needs.
     * @param id The id of the receiver.
     */
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
