package EnginePackage;

import BoatPackage.SimpleBoatType;
import Errors.IllegalActionException;
import RowerPackage.Level;
import jaxb.generated.Activity.Activities;
import jaxb.generated.Boats.BoatType;
import jaxb.generated.Boats.Boats;
import jaxb.generated.Member.Members;
import jaxb.generated.Member.RowingLevel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.GregorianCalendar;

import static jaxb.generated.Boats.BoatType.*;
import static jaxb.generated.Member.RowingLevel.*;

public class XmlHandler {

    public static StateSaver deserializeFromState(InputStream inputStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(StateSaver.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (StateSaver) u.unmarshal(inputStream);
    }

    public Level parseRowingLevelFromXml(RowingLevel level) throws IllegalArgumentException{
        switch (level){
            case BEGINNER:
                return Level.BEGINNER;
            case INTERMEDIATE:
                return Level.INTERMEDIATE;
            case ADVANCED:
                return Level.EXPERT;
            default:
                throw new IllegalArgumentException("invalid rowing level");
        }
    }

    public RowingLevel parseRowingLevelToXml(Level level){
        switch (level){
            case BEGINNER:
                return BEGINNER;
            case INTERMEDIATE:
                return INTERMEDIATE;
            case EXPERT:
                return ADVANCED;
            default:
                return null;
        }
    }

    public static Members deserializeFromMembers(InputStream inputStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Members.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Members) u.unmarshal(inputStream);
    }

    public static Boats deserializeFromBoats(InputStream inputStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Boats.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Boats) u.unmarshal(inputStream);
    }

    public static Activities deserializeFromActivities(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Activities.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Activities) u.unmarshal(in);
    }

    public BoatType parseSimpleBoatTypeToBoatType(SimpleBoatType boatType) {
        switch (boatType){
            case  Single:
                return SINGLE;
            case Dual_One_Paddle:
                return PAIR;
            case Dual_One_Paddle_Coxed:
                return COXED_PAIR;
            case Dual_Two_Paddle:
                return DOUBLE;
            case Dual_Two_Paddle_Coxed:
                return COXED_DOUBLE;
            case Quad_One_Paddle:
                return FOUR;
            case Quad_One_Paddle_Coxed:
                return COXED_FOUR;
            case Quad_Two_Paddle:
                return QUAD;
            case Quad_Two_Paddle_Coxed:
                return COXED_QUAD;
            case Eight_One_Paddle:
                return EIGHT;
            case Eight_Two_Paddle:
                return OCTUPLE;
            default:
                return null;
        }
    }

    public LocalTime parseStringToLocalTime (String time) throws IllegalActionException {
        try {
            return LocalTime.parse(time);
        }
        catch (DateTimeParseException exception) {
            throw new IllegalActionException ("You should enter time at one of the following formats: hh:mm, hh:mm:ss");
        }
    }

    public LocalDate parseXMLGregorianCalendarToLocalDate(XMLGregorianCalendar xmlGregorianCalendar){
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    public XMLGregorianCalendar parseLocalDateToXMLGregorianCalendar(LocalDate localDate) throws DatatypeConfigurationException {
        GregorianCalendar gcal = GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
    }

}
