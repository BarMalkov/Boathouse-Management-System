package EnginePackage;

import AlertPackage.Alert;
import BoatPackage.Boat;
import Errors.IllegalActionException;
import ReservationPackage.Assigning;
import ReservationPackage.Reservation;
import ReservationPackage.ReservationFilter;
import ReservationPackage.TimeSlot;
import RowerPackage.Rower;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class StateSaver {
    @XmlElementWrapper
    @XmlElement
    List <Rower> savedRowerList;
    @XmlElementWrapper
    @XmlElement
    List <Reservation> savedReservationList;
    @XmlElementWrapper
    @XmlElement
    List <TimeSlot> savedTimeSlotList;
    @XmlElementWrapper
    @XmlElement
    List <Boat> savedBoatList;
    @XmlElementWrapper
    @XmlElement
    List <Assigning> savedAssignList;
    @XmlElementWrapper
    @XmlElement
    List <Alert> savedAlertList;

    public StateSaver(){
        savedRowerList = new ArrayList<>();
        savedBoatList = new ArrayList<>();
        savedAssignList = new ArrayList<>();
        savedReservationList = new ArrayList<>();
        savedTimeSlotList = new ArrayList<>();
        savedAlertList = new ArrayList<>();
    }

    public List<Rower> getSavedRowerList() {
        return savedRowerList;
    }

    public List<TimeSlot> getSavedTimeSlotList() {
        return savedTimeSlotList;
    }

    public List<Reservation> getSavedReservationList() {
        return savedReservationList;
    }

    public List<Boat> getSavedBoatList() {
        return savedBoatList;
    }

    public List<Assigning> getSavedAssignList() {
        return savedAssignList;
    }

    public List<Alert> getSavedAlertList() { return savedAlertList; }

    public void saveState(EngineClass engineToSave) throws JAXBException {
        try {
            File stateSave = new File("C:\\Windows\\Temp\\state.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(StateSaver.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            try { savedBoatList = engineToSave.getAllBoats(); }catch (IllegalActionException ignored){}
            engineToSave.getAllUsers().forEach(rower ->rower.setReservationsID(new ArrayList<>()));
            try {
                int reservationID = 1;
                savedReservationList = engineToSave.getReservationsForManager(ReservationFilter.NextWeek,
                        null, false, null, null);
                for (Reservation reservation: savedReservationList) {
                    reservation.setReservationID(reservationID);
                    reservationID++;
                    engineToSave.getRowersOfReservation(reservation).forEach(rower ->
                            rower.addReservationID(reservation.getReservationID()));

                }
            } catch (IllegalActionException e){ savedReservationList = new ArrayList<>(); }
            savedRowerList = engineToSave.getAllUsers();
            try { savedTimeSlotList = engineToSave.getTimeSlots(); } catch (IllegalActionException e){ savedTimeSlotList = new ArrayList<>();}
            try { savedAssignList = engineToSave.getListOfAssigns(); } catch (IllegalActionException e){ savedAssignList = new ArrayList<>();}
            try { savedAlertList = engineToSave.getAlerts(); } catch (IllegalActionException e) { savedAlertList = new ArrayList<>(); }
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(this, stateSave);
        }
        catch (JAXBException e) {
            throw new JAXBException("couldn't complete Action due to server error");
        }
    }



    public void importState(EngineClass system) throws JAXBException, FileNotFoundException {
        try {
            InputStream inputStream = new FileInputStream(new File("C:\\Windows\\Temp\\state.xml"));
            StateSaver savedState = XmlHandler.deserializeFromState(inputStream);
            loadBoats(savedState, system);
            loadTimeSlots(savedState, system);
            loadUsers(savedState, system);
            loadUsersAndReservations(savedState, system);
            loadAssigns(savedState, system);
            loadAlerts(savedState, system);
        } catch (JAXBException exception) {
            throw new JAXBException("couldn't upload system state");
        }
    }

    private void loadAlerts(StateSaver savedState, EngineClass system) {
        if(savedState.savedAlertList != null)
            savedAlertList = savedState.getSavedAlertList();

        for (Alert alert: savedAlertList) {
            if (alert.getRecipients() != null)
                system.addAlert(alert.getMessage(), alert.getRecipients());
            else
                system.addAlert(alert.getMessage());
        }
    }

    private void loadAssigns(StateSaver savedState, EngineClass system) {
        if(savedState.savedAssignList != null)
            savedAssignList = savedState.getSavedAssignList();

        for (Assigning savedAssigning: savedAssignList)
            system.addAssigning(savedAssigning.getReservation(), savedAssigning.getAssignedBoats());
    }

    private void loadUsersAndReservations(StateSaver savedState, EngineClass system) {
        if(savedState.getSavedReservationList() != null)
            savedReservationList =  savedState.getSavedReservationList();

        for (Rower rower : system.getAllUsers()) {
            for(Reservation savedReservation: savedReservationList){
                Reservation newReservation = new Reservation(savedReservation.getReservationOwner(),
                        savedReservation.getPracticeDate(), savedReservation.getStartTime(),
                        savedReservation.getEndTime(), savedReservation.getBoatTypes());
                newReservation.setReservationID(savedReservation.getReservationID());
                newReservation.setDateOfReservation(savedReservation.getDateOfReservation());
                newReservation.setTimeOfReservation(savedReservation.getTimeOfReservation());
                newReservation.setParticipants(savedReservation.getParticipants());
                if(savedReservation.isApproved()) newReservation.setApproved(true);
                if(rower.getReservationsID().contains(savedReservation.getReservationID()))
                    try{system.addRowerToReservation(rower,newReservation); }catch (IllegalActionException ignored){}
            }
        }
    }

    private void loadBoats(StateSaver savedState, EngineClass system){
        if(savedState.getSavedBoatList() != null)
            savedBoatList =  savedState.getSavedBoatList();

        for(Boat savedBoat: savedBoatList){
            try {
                system.addNewBoat(savedBoat.getSerialNumber(), savedBoat.getBoatName(),
                        savedBoat.getBoatType(),savedBoat.isPrivateBoat(), savedBoat.isWide(),
                        savedBoat.isCostal(), savedBoat.isInRepair());
            }
            catch (IllegalActionException ignored){}
        }
    }

    private void loadTimeSlots(StateSaver savedState, EngineClass system){
        if(savedState.getSavedTimeSlotList() != null)
            savedTimeSlotList =  savedState.getSavedTimeSlotList();

        for(TimeSlot savedTimeSlot: savedTimeSlotList){
            try {
                system.addNewTimeSlot(savedTimeSlot.getActivityName(), savedTimeSlot.getStartTime(),
                        savedTimeSlot.getEndTime(),savedTimeSlot.getBoatType());
            }
            catch (IllegalActionException ignored){}
        }
    }

    private void loadUsers(StateSaver savedState, EngineClass system) throws IllegalArgumentException{
        if(savedState.getSavedRowerList() != null)
            savedRowerList =  savedState.getSavedRowerList();

        for(Rower savedRower: savedRowerList){
            try {
                system.addNewRower(savedRower.getID(), savedRower.getUserName(), savedRower.getEmail(),
                        savedRower.getPassword(), savedRower.getPhoneNumber(), savedRower.getAge(),
                        savedRower.isManager(), savedRower.getComment(), savedRower.getLevel(),
                        savedRower.getDateOfRegistration(), savedRower.getDateOfExpiration(),
                        savedRower.hasPrivateBoat(), savedRower.getPrivateBoatID());
                system.getRower(savedRower.getEmail()).setReservationsID(savedRower.getReservationsID());
            }
            catch (IllegalActionException ignored){}
        }
    }

}