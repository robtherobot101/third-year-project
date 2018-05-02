package seng302.Core;

import seng302.Generic.WaitingListItem;
import seng302.User.Attribute.Organ;

import java.util.Date;

/**
 * An object to store the necessary data for the transplant waiting list
 */
public class TransplantWaitingListItem extends WaitingListItem {

    private String Name;
    private String region;

    /**
     * Constructor of the object.
     *
     * @param Name The name of the receiver.
     * @param region The region of the receiver.
     * @param date The date that the organ was registered.
     * @param organ The organ that the receiver needs.
     * @param id The id of the receiver.
     */
    public TransplantWaitingListItem(String Name, String region, String date, Organ organ, long id, Integer waitingListId) {
        super(organ, date, id, waitingListId);
        this.Name = Name;
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public String getName() {
        return Name;
    }

}
