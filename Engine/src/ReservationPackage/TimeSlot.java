package ReservationPackage;

import BoatPackage.SimpleBoatType;
import EnginePackage.LocalTimeAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "timeSlot")
public class TimeSlot implements Serializable {
    @XmlAttribute(name = "ActivityName")
    private String activityName;
    @XmlJavaTypeAdapter(value = LocalTimeAdapter.class)
    private LocalTime startTime;
    @XmlJavaTypeAdapter(value = LocalTimeAdapter.class)
    private LocalTime endTime;
    @XmlAttribute
    @XmlSchemaType(name = "SimpleBoatType")
    private SimpleBoatType boatType;

    public TimeSlot(String activityName, LocalTime startTime, LocalTime endTime,
                    SimpleBoatType boatType) {
        this.activityName = activityName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.boatType = boatType;
    }

    public TimeSlot() {
        this.activityName ="";
        this.startTime = LocalTime.now();
        this.endTime = LocalTime.now();
        this.boatType = null;
    }


    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public SimpleBoatType getBoatType() {
        return boatType;
    }

    public void setBoatType(SimpleBoatType boatType) { this.boatType = boatType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return getActivityName().equalsIgnoreCase(timeSlot.getActivityName()) &&
                getStartTime().equals(timeSlot.getStartTime()) &&
                getEndTime().equals(timeSlot.getEndTime()) &&
                getBoatType() == timeSlot.getBoatType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActivityName(), getStartTime(), getEndTime(), getBoatType());
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}

