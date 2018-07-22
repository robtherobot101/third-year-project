package seng302.Model;

import seng302.Model.Attribute.Organ;

import java.time.LocalDate;
import java.util.Objects;


/**
 * Contains information for a waiting list record.
 */
public class WaitingListItem {

    private Organ organType;
    private LocalDate organRegisteredDate;
    private int id;
    private int userId;
    private LocalDate organDeregisteredDate;
    private int organDeregisteredCode;

    public WaitingListItem(Organ organType, LocalDate organRegisteredDate, int id, int userId, LocalDate organDeregisteredDate, int organDeregisteredCode) {
        this.organType = organType;
        this.organRegisteredDate = organRegisteredDate;
        this.id = id;
        this.userId = userId;
        this.organDeregisteredDate = organDeregisteredDate;
        this.organDeregisteredCode = organDeregisteredCode;
    }

    public Organ getOrganType() {
        return organType;
    }

    public LocalDate getOrganRegisteredDate() {
        return organRegisteredDate;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDate getOrganDeregisteredDate() {
        return organDeregisteredDate;
    }

    public int getOrganDeregisteredCode() {
        return organDeregisteredCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitingListItem that = (WaitingListItem) o;
        return id == that.id &&
                userId == that.userId &&
                organDeregisteredCode == that.organDeregisteredCode &&
                organType == that.organType &&
                Objects.equals(organRegisteredDate, that.organRegisteredDate) &&
                Objects.equals(organDeregisteredDate, that.organDeregisteredDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(organType, organRegisteredDate, id, userId, organDeregisteredDate, organDeregisteredCode);
    }
}
