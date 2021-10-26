package BoatPackage;

import jaxb.generated.Boats.BoatType;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType(name = "SimpleBoatType")
@XmlEnum
public enum SimpleBoatType implements Serializable {
    @XmlEnumValue("Single")
    Single ("Single rower boat", 1),
    @XmlEnumValue("Dual_One_Paddle")
    Dual_One_Paddle("Dual rower one paddle boat", 2),
    @XmlEnumValue("Dual_One_Paddle_Coxed")
    Dual_One_Paddle_Coxed("Dual rower one paddle boat with coxswain", 3),
    @XmlEnumValue("Dual_Two_Paddle")
    Dual_Two_Paddle("Dual rower two paddle boat", 2),
    @XmlEnumValue("Dual_Two_Paddle_Coxed")
    Dual_Two_Paddle_Coxed("Dual rower two paddle boat with coxswain", 3),
    @XmlEnumValue("Quad_One_Paddle")
    Quad_One_Paddle("Quad rower one paddle boat", 4),
    @XmlEnumValue("Quad_One_Paddle_Coxed")
    Quad_One_Paddle_Coxed("Quad rower one paddle boat with coxswain", 5),
    @XmlEnumValue("Quad_Two_Paddle")
    Quad_Two_Paddle("Quad rower two paddle boat", 4),
    @XmlEnumValue("Quad_Two_Paddle_Coxed")
    Quad_Two_Paddle_Coxed("Quad rower two paddle boat with coxswain", 5),
    @XmlEnumValue("Eight_One_Paddle")
    Eight_One_Paddle("Eight rower one paddle boat with coxswain", 9),
    @XmlEnumValue("Eight_Two_Paddle")
    Eight_Two_Paddle("Eight rower two paddle boat with coxswain", 9);

    private final String boatDescription;
    private final int numberOfRowers;

    public int getNumberOfRowers() {
        return numberOfRowers;
    }

    SimpleBoatType(String boatDescription, int numberOfRowers){
        this.numberOfRowers = numberOfRowers;
        this.boatDescription = boatDescription;
    }

    public String getBoatDescription() {
        return boatDescription;
    }

    public static SimpleBoatType parseBoatTypeToSimpleBoatType(BoatType boatType)throws IllegalArgumentException {
        switch (boatType){
            case SINGLE:
                return SimpleBoatType.Single;
            case PAIR:
                return SimpleBoatType.Dual_One_Paddle;
            case COXED_PAIR:
                return SimpleBoatType.Dual_One_Paddle_Coxed;
            case DOUBLE:
                return SimpleBoatType.Dual_Two_Paddle;
            case COXED_DOUBLE:
                return SimpleBoatType.Dual_Two_Paddle_Coxed;
            case FOUR:
                return SimpleBoatType.Quad_One_Paddle;
            case COXED_FOUR:
                return SimpleBoatType.Quad_One_Paddle_Coxed;
            case QUAD:
                return SimpleBoatType.Quad_Two_Paddle;
            case COXED_QUAD:
                return SimpleBoatType.Quad_Two_Paddle_Coxed;
            case EIGHT:
                return SimpleBoatType.Eight_One_Paddle;
            case OCTUPLE:
                return SimpleBoatType.Eight_Two_Paddle;
            default:
                throw new IllegalArgumentException("Invalid boat type");
        }
    }
}


