package BoatPackage;


import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Boat")
public class Boat implements Serializable {

    @XmlAttribute(name = "ID")
    private String serialNumber;
    @XmlAttribute(name= "BoatName")
    private String boatName;
    @XmlAttribute(name = "private")
    private boolean privateBoat;
    @XmlAttribute(name = "inRepair")
    private boolean inRepair;
    @XmlAttribute(name = "type")
    private SimpleBoatType boatType;
    @XmlAttribute(name = "coxswain")
    private boolean hasCoxswain;
    @XmlAttribute(name = "costal")
    private boolean isCostal;
    @XmlAttribute(name = "Wide")
    private boolean isWide;

    public Boat() {
        this.serialNumber = "";
        this.boatName = "";
        this.privateBoat = false;
        this.inRepair = false;
        this.boatType = null;
        this.hasCoxswain = false;
        this.isCostal = true;
        this.isWide = false;
    }

    public Boat(String serialNumber, String boatName, boolean privateBoat,
                boolean inRepair, SimpleBoatType boatType, boolean isCostal, boolean isWide) {
        this.serialNumber = serialNumber;
        this.boatName = boatName;
        this.privateBoat = privateBoat;
        this.inRepair = inRepair;
        this.boatType = boatType;
        this.isCostal = isCostal;
        this.isWide = isWide;
        this.hasCoxswain = isBoatWithCoxswain(boatType);
    }
    public SimpleBoatType getBoatType() {
        return boatType;
    }

    public boolean isHasCoxswain() {
        return hasCoxswain;
    }

    public boolean isCostal() {
        return isCostal;
    }

    public boolean isWide() {
        return isWide;
    }

    public void setBoatType(SimpleBoatType boatType) {this.boatType = boatType; }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getBoatName() {
        return boatName;
    }

    public boolean isPrivateBoat() {
        return privateBoat;
    }

    public boolean isInRepair() {
        return inRepair;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public void setPrivateBoat(boolean privateBoat) {
        this.privateBoat = privateBoat;
    }

    public void setInRepair(boolean inRepair) {
        this.inRepair = inRepair;
    }

    private boolean isBoatWithCoxswain(SimpleBoatType boatType){
        return boatType.getBoatDescription().endsWith("coxswain");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boat boat = (Boat) o;
        return getSerialNumber().equalsIgnoreCase(boat.getSerialNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSerialNumber());
    }
}
