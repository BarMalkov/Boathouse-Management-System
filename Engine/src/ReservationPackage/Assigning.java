package ReservationPackage;

import BoatPackage.Boat;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "reservation", "assignedBoats"
})
@XmlRootElement(name = "assigning")
public class Assigning implements Serializable {
    @XmlElement(name = "Reservation")
    Reservation reservation;
    @XmlElement(name = "Boat")
    Boat assignedBoats;

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setAssignedBoats(Boat assignedBoats) {
        this.assignedBoats = assignedBoats;
    }

    public Assigning(Reservation reservation, Boat assignedBoats) {
        this.reservation = reservation;
        this.assignedBoats = assignedBoats;
    }

    public Assigning() {
        this.reservation = new Reservation();
        this.assignedBoats = new Boat();
    }

    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assigning assigning = (Assigning) o;
        return getReservation().equals(assigning.getReservation()) &&
                getAssignedBoats().equals(assigning.getAssignedBoats());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReservation(), getAssignedBoats());
    }

    public Boat getAssignedBoats() {
        return assignedBoats;
    }
}
