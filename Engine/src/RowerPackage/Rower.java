package RowerPackage;

import EnginePackage.LocalDateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "reservation")
public class Rower implements Serializable {
    @XmlAttribute
    private String ID;
    @XmlAttribute
    private String userName;
    @XmlAttribute
    private String password;
    @XmlAttribute
    private String email;
    @XmlAttribute
    private String phoneNumber;
    @XmlAttribute
    private int age;
    @XmlAttribute
    private Level level;
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate dateOfRegistration;
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate dateOfExpiration;
    @XmlAttribute
    private boolean hasPrivateBoat;
    @XmlAttribute
    private String privateBoatID;
    @XmlAttribute
    private boolean isManager;
    @XmlAttribute
    private String comment;
    @XmlAttribute
    private List<Integer> reservationsID = new ArrayList<>();

    public Rower(String ID, String name, String password, String email, String phoneNumber,
                 int age, Level level, LocalDate dateOfRegistration, LocalDate dateOfExpiration,
                 boolean hasPrivateBoat, String privateBoatID, boolean isManager, String comment) {
        this.ID = ID;
        this.userName = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.level = level;
        this.dateOfRegistration = dateOfRegistration;
        this.dateOfExpiration = dateOfExpiration;
        this.hasPrivateBoat = hasPrivateBoat;
        this.privateBoatID = privateBoatID;
        this.isManager = isManager;
        this.comment = comment;
    }

    public Rower() {
        this.ID = "";
        this.userName ="";
        this.password = "";
        this.email = "";
        this.phoneNumber = "";
        this.age = 0;
        this.level = null;
        this.dateOfRegistration = LocalDate.now();
        this.dateOfExpiration = LocalDate.now();
        this.hasPrivateBoat = false;
        this.privateBoatID = "";
        this.isManager = false;
        this.comment = "";
    }

    public List<Integer> getReservationsID() {
        return reservationsID;
    }
    public void setReservationsID(List<Integer> reservationsID) {
        this.reservationsID = reservationsID;
    }

    public void addReservationID(int reservationID){
        reservationsID.add(reservationID);
    }
    public String getID(){return this.ID;}
    public String getUserName(){return this.userName;}
    public String getPassword(){return this.password;}
    public String getEmail(){return this.email;}
    public String getPhoneNumber(){return this.phoneNumber;}
    public int getAge(){return this.age;}
    public Level getLevel(){return this.level;}
    public LocalDate getDateOfRegistration(){return this.dateOfRegistration;}
    public LocalDate getDateOfExpiration(){return this.dateOfExpiration;}
    public boolean hasPrivateBoat() { return hasPrivateBoat; }
    public String getPrivateBoatID() { return hasPrivateBoat ? privateBoatID : null; }
    public boolean isManager() { return isManager;}
    public String getComment(){return this.comment;}
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPassword(String newPassword){this.password = newPassword;}
    public void setEmail(String newEmail){this.email = newEmail;}
    public void setPhoneNumber(String newPhoneNumber){this.phoneNumber = newPhoneNumber;}
    public void setAge(int age) { this.age = age; }
    public void setLevel(Level newLevel){this.level = newLevel;}
    public void setDateOfRegistration(LocalDate dateOfRegistration) { this.dateOfRegistration = dateOfRegistration; }
    public void setDateOfExpiration(LocalDate newDate){this.dateOfExpiration = newDate;}
    public void setHasPrivateBoat(boolean hasPrivateBoat) { this.hasPrivateBoat = hasPrivateBoat; }
    public void setPrivateBoatID(String privateBoatID) { this.privateBoatID = privateBoatID; }
    public void setAsManager(){this.isManager = true;}
    public void setComment(String newComment){this.comment = newComment;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rower rower = (Rower) o;
        return getID().equalsIgnoreCase(rower.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }
}
