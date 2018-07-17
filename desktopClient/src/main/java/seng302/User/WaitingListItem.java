package seng302.User;

import seng302.Generic.SearchUtils;
import seng302.User.Attribute.Organ;

import java.time.LocalDate;


/**
 * Contains information for a transplant waiting list record.
 */
public class WaitingListItem {
    public LocalDate organDeregisteredDate;
    public boolean stillWaitingOn;
    public Integer organDeregisteredCode;


    protected Organ organType;
    protected LocalDate organRegisteredDate;
    protected Long receiverId;

    private String receiverName;
    private String receiverRegion;

    /**
     * Constructor of the object. Used when registering a new organ
     *
     * @param receiverName          The name of the receiver.
     * @param receiverRegion        The region of the receiver.
     * @param organ         The organ that the receiver needs.
     * @param receiverId            The id of the receiver.
     */
    public WaitingListItem(String receiverName, String receiverRegion, long receiverId, Organ organ) {
        this.receiverName = receiverName;
        this.receiverRegion = receiverRegion;
        this.receiverId = receiverId;

        this.organType = organ;

        this.organRegisteredDate = LocalDate.now();
    }

    /**
     * Constructor of the object. Used when creating items which are already de-registered
     *
     * @param receiverName          The name of the receiver.
     * @param receiverRegion        The region of the receiver.
     * @param registeredDate          The date that the organ was registered.
     * @param deregisteredDate          The date that the organ was de-registered.
     * @param organDeregisteredCode          The code used to de-register the organ
     * @param organ         The organ that the receiver needs.
     * @param receiverId            The id of the receiver.
     */
    public WaitingListItem(String receiverName, String receiverRegion, long receiverId, LocalDate registeredDate, LocalDate deregisteredDate, int organDeregisteredCode, Organ organ) {
        this.receiverName = receiverName;
        this.receiverRegion = receiverRegion;
        this.receiverId = receiverId;

        this.organRegisteredDate = registeredDate;
        this.organDeregisteredDate = deregisteredDate;
        this.organDeregisteredCode = organDeregisteredCode;
        this.organType = organ;

    }

    /**
     * Updates an organs registration date and removes its deregistration date.
     * Can be called when registering a previously deregistered organ.
     */
    public void registerOrgan() {
        this.organRegisteredDate = LocalDate.now();
        this.organDeregisteredCode = null;
        this.stillWaitingOn = true;
        this.organDeregisteredDate = null;
    }

    /**
     * Updates an organs deregistration date and removes its registration date.
     * Can be called when deregistering a previously registered organ.
     * There are 5 reason codes:
     *     1. Registration error
     *     2. A disease requiring the transplant has been cured
     *     3. The receiver has passed away
     *     4. The transplant has been successful
     *     5. An administrator has removed this via the command line
     *
     * @param reasonCode reason code to assign to an organ upon deregister
     */
    public void deregisterOrgan(Integer reasonCode) {
        if (this.organDeregisteredDate == null) {
            if (reasonCode != 3) {
                this.organDeregisteredDate = LocalDate.now();
                this.organDeregisteredCode = reasonCode;
            } else {
                this.organDeregisteredDate = LocalDate.now();
                this.organDeregisteredCode = reasonCode;
            }

        }
    }

    public Organ getOrganType() {
        return organType;
    }

    public LocalDate getOrganRegisteredDate() {
        return organRegisteredDate;
    }

    public LocalDate organDeregisteredDate() {
        return organDeregisteredDate;
    }

    public Integer getOrganDeregisteredCode() {
        return organDeregisteredCode;
    }

    public Long getUserId() {
        return receiverId;
    }

    public String getReceiverRegion() {
        return receiverRegion;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public boolean getStillWaitingOn(){
        return organDeregisteredDate == null;
    }
}
