package ReservationPackage;

import BoatPackage.SimpleBoatType;
import EnginePackage.LocalDateAdapter;
import EnginePackage.LocalTimeAdapter;
import RowerPackage.Rower;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Reservation")
public class Reservation implements Serializable {
    @XmlAttribute(name = "reservationOwner")
    private String reservationOwner;
    @XmlAttribute(name = "ID")
    private int reservationID;
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate practiceDate;
    @XmlJavaTypeAdapter(value = LocalTimeAdapter.class)
    private LocalTime startTime;
    @XmlJavaTypeAdapter(value = LocalTimeAdapter.class)
    private LocalTime endTime;
    @XmlElementWrapper
    @XmlElement(name = "Boat")
    private List<SimpleBoatType> boatTypes;
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate dateOfReservation;
    @XmlJavaTypeAdapter(value = LocalTimeAdapter.class)
    private LocalTime timeOfReservation;
    @XmlAttribute(name = "isApproved")
    private boolean isApproved;
    @XmlAttribute
    private List<String> participants = new ArrayList<>();

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public List<String> getParticipants() {
        return participants;
    }


    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public void addParticipants(String participant){
        participants.add(participant);
    }

    public Reservation(String reservationOwner, LocalDate practiceDate, LocalTime startTime, LocalTime endTime, List<SimpleBoatType> reservationBoatTypeList) {
        this.reservationOwner = reservationOwner;
        this.practiceDate = practiceDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dateOfReservation = LocalDate.now();
        this.timeOfReservation = LocalTime.now();
        this.boatTypes = reservationBoatTypeList;
        this.isApproved = false;
    }

    public Reservation(String reservationOwner, LocalDate practiceDate, TimeSlot timeSlot){
        this.reservationOwner = reservationOwner;
        this.practiceDate = practiceDate;
        this.startTime = timeSlot.getStartTime();
        this.endTime = timeSlot.getEndTime();
        this.dateOfReservation = LocalDate.now();
        this.timeOfReservation = LocalTime.now();
        this.isApproved = false;
        this.boatTypes = new ArrayList<>();
    }

    public Reservation() {
        this.reservationOwner = " ";
        this.reservationID = 0;
        this.practiceDate = LocalDate.now();
        this.startTime = LocalTime.now();
        this.endTime = LocalTime.now();
        this.boatTypes = null;
        this.dateOfReservation = LocalDate.now();
        this.timeOfReservation = LocalTime.now();
        this.isApproved = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return isApproved() == that.isApproved() &&
                getReservationOwner().equals(that.getReservationOwner()) &&
                getPracticeDate().equals(that.getPracticeDate()) &&
                getStartTime().equals(that.getStartTime()) &&
                getEndTime().equals(that.getEndTime()) &&
                getDateOfReservation().equals(that.getDateOfReservation()) &&
                getTimeOfReservation().equals(that.getTimeOfReservation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReservationOwner(), getPracticeDate(), getStartTime(), getEndTime(), getDateOfReservation(), getTimeOfReservation(), isApproved());
    }

    public void setReservationOwner(String reservationOwner) {
        this.reservationOwner = reservationOwner;
    }

    public void setPracticeDate(LocalDate practiceDate) {
        this.practiceDate = practiceDate;
    }

    public void setDateOfReservation(LocalDate dateOfReservation) {
        this.dateOfReservation = dateOfReservation;
    }

    public int getReservationID() {
        return reservationID;
    }

    public void setTimeOfReservation(LocalTime timeOfReservation) {
        this.timeOfReservation = timeOfReservation;
    }

    public String getReservationOwner() {
        return reservationOwner;
    }

    public LocalDate getPracticeDate() {
        return practiceDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public List <SimpleBoatType> getBoatTypes() {
        return boatTypes;
    }

    public LocalDate getDateOfReservation() {
        return dateOfReservation;
    }

    public LocalTime getTimeOfReservation() {
        return timeOfReservation;
    }

    public void setBoatTypes(List<SimpleBoatType> newBoatList) {
        this.boatTypes = newBoatList;
    }

    public boolean isApproved() { return isApproved; }

    public void setApproved(boolean approved) { isApproved = approved; }

    public boolean isNextWeekReservation() {
        return (this.practiceDate.isEqual(LocalDate.now()) ||
                this.practiceDate.isAfter(LocalDate.now())) &&
                this.practiceDate.isBefore(LocalDate.now().plusDays(8));
    }

    public void removeParticipant(String email) {
        participants.remove(email);
    }
}
