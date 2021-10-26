package EnginePackage;

import AlertPackage.Alert;
import BoatPackage.Boat;
import BoatPackage.SimpleBoatType;
import Errors.IllegalActionException;
import ReservationPackage.Assigning;
import ReservationPackage.Reservation;
import ReservationPackage.ReservationFilter;
import ReservationPackage.TimeSlot;
import RowerPackage.Level;
import RowerPackage.Rower;
import jaxb.generated.Activity.Activities;
import jaxb.generated.Activity.Timeframe;
import jaxb.generated.Boats.Boats;
import jaxb.generated.Member.Member;
import jaxb.generated.Member.Members;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


public class EngineClass implements EngineInterface {
    private XmlHandler xmlHandler = new XmlHandler();
    private Map<Rower, List<Reservation>> listOfReservationsByRowers = new HashMap<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private final List<SimpleBoatType> simpleBoatTypeList = new ArrayList<>(Arrays.asList(SimpleBoatType.values()));
    private final List<Level> rowersLevels = new ArrayList<>(Arrays.asList(Level.values()));
    private List<Boat> listOfBoats = new ArrayList<>();
    private List<Assigning> listOfAssigns = new ArrayList<>();
    private final List<ReservationFilter> reservationFilters = new ArrayList<>(Arrays.asList(ReservationFilter.values()));
    private StateSaver stateSaver = new StateSaver();
    private List<Alert> alerts = new ArrayList<>();
    private static List<LocalDate> availableDatesList = new ArrayList<>();
    static {
        for (int i = 0; i <= 7; i++)
            availableDatesList.add(LocalDate.now().plusDays(i));
    }

    public EngineClass(){
        try {
            stateSaver.importState(this);
        } catch (JAXBException | FileNotFoundException ignored) {
                listOfReservationsByRowers.put(new Rower("1","admin","1234",
                        "admin@user", "054", 25,
                        Level.BEGINNER, LocalDate.now().minusDays(1), LocalDate.now().plusYears(1),
                        false, "-",true, "comment"), new ArrayList<>());
        }

    }
    //-------------------------------Alerts----------------------------------------------------------------------------

    public void addAlert(String message){
        alerts.add(new Alert(message));
    }

    public void removeAlert(Alert alert){
        alerts.remove(alert);
    }

    public void addAlert(String message, List<String> recipients){
        alerts.add(new Alert(message, recipients));
    }

    public List<Alert> getAlertForUser(String email){
        List<Alert> alertsForUser = new ArrayList<>();
        alerts.forEach((alert)->{
            if(alert.isManualAlert() || alert.isEmailInRecipientsList(email)){
                alertsForUser.add(alert);
            }
        });

        return alertsForUser;
    }

    public List<Alert> getAlertsForManager(){
        List<Alert> alertsForManager = new ArrayList<>();
        alerts.forEach((alert)->{
            if(alert.isManualAlert())
                alertsForManager.add(alert);
        });

        return alertsForManager;
    }


    //-------------------------------getters----------------------------------------------------------------------------


    public List<Alert> getAlerts() throws IllegalActionException {
        if(alerts.size() == 0)
            throw new IllegalActionException("no alerts to show");

        return alerts;
    }

    public List<Level> getRowersLevels() { return Collections.unmodifiableList(rowersLevels); }

    public List<LocalDate> getAvailableDatesList(boolean fromTomorrow) {
        if(fromTomorrow)
            return Collections.unmodifiableList(new ArrayList<>(availableDatesList.subList(1,availableDatesList.size())));

        return Collections.unmodifiableList(availableDatesList);
    }

    public List<ReservationFilter> getReservationFilters() { return Collections.unmodifiableList(reservationFilters); }

    public List<SimpleBoatType> getSimpleBoatTypeList() { return Collections.unmodifiableList(simpleBoatTypeList); }

    public List<TimeSlot> getTimeSlots() throws IllegalActionException {
        if(timeSlots.size() == 0)
            throw new IllegalActionException("No timeSlots To show");
        return Collections.unmodifiableList(timeSlots); }

    public List<Assigning> getListOfAssigns() throws IllegalActionException {
        if(listOfAssigns.size() == 0)
            throw new IllegalActionException("No assigns to show");

        return Collections.unmodifiableList(listOfAssigns);
    }

    public List<Boat> getAllBoats() throws IllegalActionException {
        if(listOfBoats.size() == 0)
            throw new IllegalActionException("No boats to show");

        return Collections.unmodifiableList(listOfBoats);
    }

    public List<Rower> getAllUsers() {
        return Collections.unmodifiableCollection(listOfReservationsByRowers.keySet()).stream().collect(Collectors.toList());
    }

    public List<Reservation> getUserReservations(Rower loggedInUser,ReservationFilter reservationFilter,
                                                 LocalDate dateToShow, boolean showToEdit)
            throws IllegalActionException{
        List <Reservation> reservationsToReturn = null;
        switch (reservationFilter) {
            case ByDay:
                reservationsToReturn = Collections.unmodifiableList(listOfReservationsByRowers.get(loggedInUser).
                        stream().filter(reservation ->
                        reservation.getPracticeDate().isEqual(dateToShow)).collect(Collectors.toList()));
                break;
            case LastWeek:
                reservationsToReturn = Collections.unmodifiableList(listOfReservationsByRowers.get(loggedInUser).
                        stream().filter(reservation ->
                        reservation.getPracticeDate().isBefore(LocalDate.now())).collect(Collectors.toList()));
                break;
            case NextWeek:
                reservationsToReturn = Collections.unmodifiableList(listOfReservationsByRowers.get(loggedInUser).
                        stream().filter(Reservation::isNextWeekReservation).
                        collect(Collectors.toList()));
        }

        if(showToEdit)
            reservationsToReturn = reservationsToReturn.stream().filter(reservation -> !reservation.isApproved()).
                    collect(Collectors.toList());

        if(reservationsToReturn.size() ==0)
            throw new IllegalActionException("No reservations To show");

        return  Collections.unmodifiableList(reservationsToReturn);
    }

    public List<Reservation> getReservationsForManager(ReservationFilter reservationFilter, LocalDate dateToShow,
                                                       boolean showToEdit, LocalTime startTime, LocalTime endTime)
            throws IllegalActionException{
        List <Reservation> temporaryReservationList = null;
        switch (reservationFilter) {
            case ByDay:
                temporaryReservationList = listOfReservationsByRowers.values().stream().
                        flatMap(List::stream).
                        filter(reservation ->
                                reservation.getPracticeDate().isEqual(dateToShow)).
                        collect(Collectors.toList());
                break;
            case LastWeek:
                temporaryReservationList = listOfReservationsByRowers.values().stream().
                        flatMap(List::stream).
                        filter(reservation ->
                                reservation.getPracticeDate().isBefore(LocalDate.now())).
                        collect(Collectors.toList());
                break;
            case NextWeek:
                temporaryReservationList = listOfReservationsByRowers.values().stream().
                        flatMap(List::stream).
                        filter(Reservation::isNextWeekReservation).
                        collect(Collectors.toList());
                break;
        }
        if(showToEdit)
            temporaryReservationList = temporaryReservationList.stream().filter(reservation ->
                    !reservation.isApproved()).
                    collect(Collectors.toList());

        if(startTime != null && endTime != null)
            temporaryReservationList = temporaryReservationList.stream().filter(reservation ->
                    isTheSameTimeSlot(reservation.getStartTime(), reservation.getEndTime(),
                            startTime, endTime)).collect(Collectors.toList());

        if(temporaryReservationList.size() == 0)
            throw new IllegalActionException("No reservations To show");

        return Collections.unmodifiableList(temporaryReservationList.stream().distinct().collect(Collectors.toList()));
    }

    public List<Rower> getRowersOfReservation(Reservation reservationToEdit) throws IllegalActionException {
        if(listOfReservationsByRowers.entrySet().stream()
                .filter(entry -> entry.getValue().contains(reservationToEdit)).map(Map.Entry::getKey).count() ==0)
            throw new IllegalActionException("No more users to show of this reservation");

        return listOfReservationsByRowers.entrySet().stream()
                .filter(entry -> entry.getValue().contains(reservationToEdit)).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<SimpleBoatType> getBoatTypesOfReservation(Reservation reservationToEdit) {
        return Collections.unmodifiableList(reservationToEdit.getBoatTypes());
    }

    public List<Boat> getBoatsOfReservation(Reservation reservationToAssign) throws IllegalActionException{
        if(Collections.unmodifiableList(listOfBoats.stream().filter(boat ->
                isBoatTypeOfReservation(reservationToAssign.getBoatTypes(), boat.getBoatType())
                        && isAvailableBoat(boat, reservationToAssign)).collect(Collectors.toList())).size() == 0)
            throw new IllegalActionException("No matching boats to this reservation requested boats");

        return Collections.unmodifiableList(listOfBoats.stream().filter(boat ->
                isBoatTypeOfReservation(reservationToAssign.getBoatTypes(), boat.getBoatType())
                        && isAvailableBoat(boat, reservationToAssign)).collect(Collectors.toList()));
    }


    public Boat getRecommendBoat(Reservation reservationToAssign) throws IllegalActionException {
        List<Boat> boatsOfReservation = getBoatsOfReservation(reservationToAssign);
        return boatsOfReservation.get(0);
    }
    public void saveState() throws JAXBException{
        stateSaver.saveState(this);
    }

    public Rower getRower(String email) {
        return listOfReservationsByRowers.keySet().stream().filter(rower ->
                rower.getEmail().equalsIgnoreCase(email)).findFirst().get();
    }

    private Boat getBoat(String boatID){
        for (Boat boat: listOfBoats) {
            if(boat.getSerialNumber().equalsIgnoreCase(boatID))
                return boat;
        }
        return null;
    }

    //-------------------------------validators-------------------------------------------------------------------------

    public boolean isValidEmail(String email) {
        List<Rower> rowerList = new ArrayList<>(listOfReservationsByRowers.keySet());
        for (Rower rower : rowerList) {
            if (rower.getEmail().equalsIgnoreCase(email))
                return false;
        }
        return true;
    }

    public boolean isAssignedBoat(Boat boatToRemove) {
        for (Assigning assigning: listOfAssigns) {
            if(assigning.getAssignedBoats().equals(boatToRemove))
                return true;
        }
        return false;
    }

    public boolean isUserExist(String rowerEmail) {
        for (Rower rower:listOfReservationsByRowers.keySet()) {
            if(rower.getEmail().equalsIgnoreCase(rowerEmail))
                return true;
        }
        return false;
    }

    public boolean isPasswordMatchEmail(String email, String password){
        return getRower(email).getPassword().equalsIgnoreCase(password);
    }

    public boolean isValidBoatID(String boatID) {
        if(listOfBoats.size() == 0)
            return true;
        for (Boat boat: listOfBoats) {
            if(boat.getSerialNumber().equalsIgnoreCase(boatID))
                return false;
        }

        return true;
    }

    private boolean isBoatExist(String boatID){
        for (Boat boat: listOfBoats) {
            if(boat.getSerialNumber().equalsIgnoreCase(boatID))
                return true;
        }
        return false;
    }

    public boolean isValidUserID(String userID) {
        for (Rower rower: listOfReservationsByRowers.keySet()) {
            if(rower.getID().equalsIgnoreCase(userID))
                return false;
        }
        return true;
    }

    public boolean isNeedToMergeReservation(Reservation reservationToAssign, Boat boatToAssign) throws IllegalActionException {
        return getRowersOfReservation(reservationToAssign).size() <
                boatToAssign.getBoatType().getNumberOfRowers();
    }

    public boolean isNeedToSplitReservation(Reservation reservationToAssign, Boat boatToAssign) throws IllegalActionException {
        return getRowersOfReservation(reservationToAssign).size() >
                boatToAssign.getBoatType().getNumberOfRowers();
    }

    private boolean isAvailableBoat(Boat boat, Reservation reservationToAssign) {
        List <Assigning> ListOfBoatAssigns = listOfAssigns.stream().filter(assigning ->
                assigning.getAssignedBoats().equals(boat)).collect(Collectors.toList());

        boolean isAvailable = !boat.isPrivateBoat() && !boat.isInRepair();
        if(ListOfBoatAssigns.size() > 0)
            isAvailable = !isRequestedBoatAlreadyAssigned(ListOfBoatAssigns, reservationToAssign);

        return isAvailable;
    }

    private boolean isRequestedBoatAlreadyAssigned(List<Assigning> listOfBoatAssigns,
                                                   Reservation reservationToAssign) {
        for (Assigning assigning: listOfBoatAssigns) {
            if(isReservationCollisions(reservationToAssign, assigning.getReservation()))
                return true;
        }
        return  false;
    }

    private boolean isTheSameTimeSlot(LocalTime timeSlot1StartTime, LocalTime timeSlot1EndTime,
                                      LocalTime timeSlot2StartTime, LocalTime timeSlot2EndTime) {
        boolean isSameStartHour = timeSlot1StartTime.getHour() == timeSlot2StartTime.getHour();
        boolean isSameEndHour = timeSlot1EndTime.getHour() == timeSlot2EndTime.getHour();
        boolean isSameStartMin = timeSlot1StartTime.getMinute() == timeSlot2StartTime.getMinute();
        boolean isSameEndMin = timeSlot1EndTime.getMinute() == timeSlot2EndTime.getMinute();

        return isSameStartHour && isSameEndHour && isSameStartMin && isSameEndMin;
    }

    private boolean isTimeSlotExistOnSystem(String activityName, LocalTime startTime,
                                            LocalTime endTime, SimpleBoatType activityBoat) {
        for (TimeSlot timeSlot : timeSlots) {
            if (activityName.equalsIgnoreCase(timeSlot.getActivityName()) &&
                    startTime.equals(timeSlot.getStartTime()) && endTime.equals(timeSlot.getEndTime())) {
                if (activityBoat != null && timeSlot.getBoatType() != null) {
                    if (activityBoat.equals(timeSlot.getBoatType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isTimeSlotCollision(LocalTime timeSlot1StartTime,LocalTime timeSlot1EndTime,
                                        LocalTime timeSlot2StartTime, LocalTime timeSlot2EndTime){
        return timeSlot1StartTime.equals(timeSlot2StartTime)            ||
                timeSlot1EndTime.equals(timeSlot2EndTime)               ||
                (timeSlot1StartTime.isAfter(timeSlot2StartTime) &&
                        timeSlot1StartTime.isBefore(timeSlot2EndTime))  ||
                (timeSlot2StartTime.isAfter(timeSlot1StartTime) &&
                        timeSlot2StartTime.isBefore(timeSlot1EndTime))    ||
                (timeSlot1StartTime.isBefore(timeSlot2StartTime) &&
                        timeSlot1EndTime.isAfter(timeSlot2EndTime));
    }

    private boolean isBoatTypeOfReservation(List<SimpleBoatType> boatTypes, SimpleBoatType boatType) {
        return boatTypes.contains(boatType);
    }

    private boolean isReservationCollisions(Reservation reservation, Reservation reservationToAdd) {
        return reservation.getPracticeDate().isEqual(reservationToAdd.getPracticeDate()) &&
                isTimeSlotCollision(reservation.getStartTime(),reservation.getEndTime(),
                        reservationToAdd.getStartTime(), reservationToAdd.getEndTime());
    }

    private boolean isSameBoatType(SimpleBoatType boat1, SimpleBoatType boat2){
        if(boat1.getBoatDescription().startsWith("Dual") &&
                boat2.getBoatDescription().startsWith("Dual"))
            return true;
        else if(boat1.getBoatDescription().startsWith("Single") &&
                boat2.getBoatDescription().startsWith("Single"))
            return true;
        else if(boat1.getBoatDescription().startsWith("Quad") &&
                boat2.getBoatDescription().startsWith("Quad"))
            return true;
        else if(boat1.getBoatDescription().startsWith("Eight") &&
                boat2.getBoatDescription().startsWith("Eight"))
            return true;

        return false;
    }

    //-------------------------------edit users details-----------------------------------------------------------------

    public void addNewRower(String userID, String userName, String email, String password,
                            String phoneNumber, int age, boolean isManager, String comment,
                            Level rowerLevel, LocalDate dateOfRegistration, LocalDate dateOfExpiration,
                            boolean hasPrivateBoat, String boatSerialNumber) throws IllegalActionException {

        if(!isValidUserID(userID))
            throw new IllegalActionException("ID is already exists");
        if(!isValidEmail(email))
            throw new IllegalActionException("Email is already exists");
        if(dateOfRegistration.isAfter(dateOfExpiration))
            throw new IllegalActionException("Date of registration cannot be after date of expiration");
        if(dateOfExpiration.isBefore(LocalDate.now()))
            throw new IllegalActionException("Your subscription is expired");
        if(hasPrivateBoat) {
            if(!isBoatExist(boatSerialNumber))
                throw new IllegalActionException("Boat not found");
            if(listOfReservationsByRowers.keySet().stream().anyMatch(rower ->
                    (rower.hasPrivateBoat() && rower.getPrivateBoatID().equalsIgnoreCase(boatSerialNumber))))
                throw new IllegalActionException("Requested private boat is already someone else's");
        }

        listOfReservationsByRowers.put(new Rower(userID, userName, password, email,
                phoneNumber, age, rowerLevel, dateOfRegistration,
                dateOfExpiration, hasPrivateBoat, boatSerialNumber, isManager,
                comment), new ArrayList<>());
        if(hasPrivateBoat){
            int indexToEdit = listOfBoats.indexOf(getBoat(boatSerialNumber));
            listOfBoats.get(indexToEdit).setPrivateBoat(true);
        }
    }


    public void editUserName(Rower rowerToEdit, String userName) {
        int indexToEdit = listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).indexOf(rowerToEdit);
        listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).
                get(indexToEdit).setUserName(userName);
    }

    public void editUserPassword(Rower rowerToEdit, String password) {
        int indexToEdit = listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).indexOf(rowerToEdit);
        listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).
                get(indexToEdit).setPassword(password);

    }

    public Rower editUserEmail( Rower rowerToEdit, String email) throws IllegalActionException {
        if(!isValidEmail(email))
            throw new IllegalActionException("Invalid email");

        int indexToEdit = listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).indexOf(rowerToEdit);
        listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).
                get(indexToEdit).setEmail(email);

        return getRower(email);
    }

    public void editUserPhoneNumber(Rower rowerToEdit, String phoneNumber) {
        int indexToEdit = listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).indexOf(rowerToEdit);
        listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).
                get(indexToEdit).setPhoneNumber(phoneNumber);
    }

    public void removeUser(Rower userToRemove, Rower loggedInUser) throws IllegalActionException {
        if(userToRemove.equals(loggedInUser))
            throw new IllegalActionException("You can't delete this user or change it's data");
        listOfReservationsByRowers.remove(userToRemove);
    }

    //-------------------------------edit boats details-----------------------------------------------------------------

    public void addNewBoat(String boatID, String boatName, SimpleBoatType boatType, boolean isPrivate,
                           boolean isWide, boolean isCostal, boolean isInRepair) throws IllegalActionException {
        if(!isValidBoatID(boatID))
            throw new IllegalActionException("Boat ID already exist on the system");

        listOfBoats.add(new Boat(boatID, boatName, isPrivate, isInRepair, boatType, isCostal, isWide));
    }

    public void removeBoatFromSystem(Boat boatToRemove) throws IllegalActionException {
        List<Assigning> assignsToRemove = new ArrayList<>();
        for (Assigning assigning: listOfAssigns) {
            if(assigning.getAssignedBoats().equals(boatToRemove)){
                assignsToRemove.add(assigning);
            }
        }
        if(assignsToRemove.size() > 0){
            for (Assigning assigning: assignsToRemove)
                removeAssigning(assigning);
        }

        listOfReservationsByRowers.keySet().forEach(rower -> {
            if (rower.hasPrivateBoat() && rower.getPrivateBoatID() != null &&
                    rower.getPrivateBoatID().equals(boatToRemove.getSerialNumber())) {
                rower.setHasPrivateBoat(false);
                rower.setPrivateBoatID("");
            }
        });
        listOfBoats.remove(boatToRemove);
    }

    public void editBoatSerialNumber(Boat boatToEdit, String boatID) throws IllegalActionException {
        if(!isValidBoatID(boatID)){
            throw new IllegalActionException("the ID you chose already belong to another boat");
        }
        int indexToEdit = listOfBoats.indexOf(boatToEdit);
        listOfBoats.get(indexToEdit).setSerialNumber(boatID);
    }

    public void editBoatName(Boat boatToEdit, String newName) {
        int indexToEdit = listOfBoats.indexOf(boatToEdit);
        listOfBoats.get(indexToEdit).setBoatName(newName);
    }

    public void editBoatPrivate(Boat boatToEdit, Rower rowerToEdit) throws IllegalActionException{
        int indexToEdit = listOfBoats.indexOf(boatToEdit);
        if(listOfBoats.get(indexToEdit).isPrivateBoat()) {
            throw new IllegalActionException("The boat is already private");
        }
        if(rowerToEdit.hasPrivateBoat()) {
            throw new IllegalActionException("This rower already has private boat");
        }

        listOfBoats.get(indexToEdit).setPrivateBoat(true);
        indexToEdit = listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).indexOf(rowerToEdit);
        listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).
                get(indexToEdit).setHasPrivateBoat(true);
        listOfReservationsByRowers.keySet().stream().collect(Collectors.toList()).
                get(indexToEdit).setPrivateBoatID(boatToEdit.getSerialNumber());
    }

    public void editBoatNotPrivate(Boat boatToEdit) throws IllegalActionException{
        int indexToEdit = listOfBoats.indexOf(boatToEdit);
        if(!listOfBoats.get(indexToEdit).isPrivateBoat())
            throw new IllegalActionException("The boat is already not private");

        listOfBoats.get(indexToEdit).setPrivateBoat(false);
        for (Rower rower: listOfReservationsByRowers.keySet()) {
            if(rower.hasPrivateBoat()){
                if(rower.getPrivateBoatID().equalsIgnoreCase(boatToEdit.getSerialNumber())){
                    rower.setPrivateBoatID("");
                    rower.setHasPrivateBoat(false);
                }
            }

        }
    }

    public void editBoatInRepair(Boat boatToEdit) throws IllegalActionException{
        int indexToEdit = listOfBoats.indexOf(boatToEdit);
        if(listOfBoats.get(indexToEdit).isInRepair())
            throw new IllegalActionException("The boat is already in repair");

        else listOfBoats.get(indexToEdit).setInRepair(true);
        List<Assigning> assignsToRemove = new ArrayList<>();
        for (Assigning assigning: listOfAssigns) {
            if(assigning.getAssignedBoats().equals(boatToEdit)){
                assignsToRemove.add(assigning);
            }
        }
        if(assignsToRemove.size() > 0){
            for (Assigning assigning: assignsToRemove)
                removeAssigning(assigning);
        }

    }

    public void editBoatNotInRepair(Boat boatToEdit) throws IllegalActionException{
        int indexToEdit = listOfBoats.indexOf(boatToEdit);
        if(!listOfBoats.get(indexToEdit).isInRepair())
            throw new IllegalActionException("The boat is already not in repair");

        else listOfBoats.get(indexToEdit).setInRepair(false);
    }

    public void editBoatPaddles(Boat boatToEdit, SimpleBoatType simpleBoatType) throws IllegalActionException {
        if(!isSameBoatType(boatToEdit.getBoatType(), simpleBoatType))
            throw new IllegalActionException("Boat type mismatch");
        else{
            int indexOfBoatToEdit = listOfBoats.indexOf(boatToEdit);
            listOfBoats.get(indexOfBoatToEdit).setBoatType(simpleBoatType);
        }
    }


    //-------------------------------edit reservation details-----------------------------------------------------------

    public Reservation createNewReservation(Rower loggedInUser, LocalDate reservationDate, TimeSlot timeSlot) throws IllegalActionException {
        Reservation reservationToAdd = new Reservation(loggedInUser.getEmail(), reservationDate, timeSlot);
        for (Reservation reservation: listOfReservationsByRowers.get(loggedInUser)) {
            if(isReservationCollisions(reservation, reservationToAdd))
                throw new IllegalActionException("Rower Already have reservation in the requested time");
        }

        listOfReservationsByRowers.get(loggedInUser).add(reservationToAdd);
        return reservationToAdd;
    }

    public void deleteReservation(Rower loggedInUser, Reservation reservationToDelete) {
        listOfReservationsByRowers.get(loggedInUser).remove(reservationToDelete);
    }

    public void addPartnersToReservation(List<Rower> partnersForReservation, Reservation reservation) {
        for (Rower rower : partnersForReservation) {
            Reservation newReservation = new Reservation(reservation.getReservationOwner(), reservation.getPracticeDate(),
                    reservation.getStartTime(), reservation.getEndTime(), reservation.getBoatTypes());
            newReservation.setTimeOfReservation(reservation.getTimeOfReservation());
            newReservation.setDateOfReservation(reservation.getDateOfReservation());
            newReservation.setParticipants(reservation.getParticipants());
            listOfReservationsByRowers.get(rower).add(newReservation);
        }
        List<String> newParticipantsList = reservation.getParticipants();
        partnersForReservation.forEach(partner ->{
            newParticipantsList.add(partner.getEmail());
        });
        try {
            getRowersOfReservation(reservation).forEach(rower -> {
                int indexOfReservation = listOfReservationsByRowers.get(rower).indexOf(reservation);
                if(indexOfReservation != -1){
                    listOfReservationsByRowers.get(rower).get(indexOfReservation).setParticipants(newParticipantsList);
                }
            });
        } catch (IllegalActionException ignored) {}
    }

    public void removePartnersFromReservation(Reservation reservationToEdit) throws IllegalActionException {
        List<Rower> rowerToEdit = getRowersOfReservation(reservationToEdit);
        for (Rower rower : rowerToEdit) {
            listOfReservationsByRowers.get(rower).remove(reservationToEdit);
        }
    }

    public void addRowerToReservation(Rower rowerToAdd, Reservation reservationToAdd) throws IllegalActionException {
        for (Reservation reservation: listOfReservationsByRowers.get(rowerToAdd)) {
            if(isReservationCollisions(reservation,reservationToAdd))
                throw new IllegalActionException("Rower Already have reservation in the requested time");
        }

        listOfReservationsByRowers.get(rowerToAdd).add(reservationToAdd);
        getRowersOfReservation(reservationToAdd).forEach(rower -> {
            int indexOfReservation = listOfReservationsByRowers.get(rower).indexOf(reservationToAdd);
            if(indexOfReservation != -1){
                if(!listOfReservationsByRowers.get(rower).
                        get(indexOfReservation).getParticipants().contains(rowerToAdd.getEmail()))
                    listOfReservationsByRowers.get(rower).get(indexOfReservation).
                            addParticipants(rowerToAdd.getEmail());
            }
        });
    }

    public TimeSlot createFreeTimeSlot(LocalTime startTime, LocalTime endTime) throws IllegalActionException{
        if(startTime.isAfter(endTime))
            throw new IllegalActionException("end time must be after start time");
        return new TimeSlot("General activity", startTime, endTime, null);
    }

    public void removeRowerFromReservation(Rower chosenRowerOfReservation, Reservation reservation) throws IllegalActionException {
        getRowersOfReservation(reservation).forEach(rower -> {
            int indexOfReservation = listOfReservationsByRowers.get(rower).indexOf(reservation);
            if(indexOfReservation != -1){
                listOfReservationsByRowers.get(rower).get(indexOfReservation).removeParticipant(chosenRowerOfReservation.getEmail());
            }
        });
        listOfReservationsByRowers.get(chosenRowerOfReservation).remove(reservation);
    }

    public void editReservationDate(Reservation reservationToEdit, LocalDate updatedDate)
            throws IllegalActionException {
        Reservation editedReservation = new Reservation(reservationToEdit.getReservationOwner(),
        updatedDate, reservationToEdit.getStartTime(), reservationToEdit.getEndTime(), reservationToEdit.getBoatTypes());
        editedReservation.setDateOfReservation(reservationToEdit.getDateOfReservation());
        editedReservation.setTimeOfReservation(reservationToEdit.getTimeOfReservation());
        editedReservation.setParticipants(reservationToEdit.getParticipants());
        List <Rower> rowersOfReservation = getRowersOfReservation(reservationToEdit);
        for (Rower rower: rowersOfReservation) {
            for (Reservation reservation : listOfReservationsByRowers.get(rower)) {
                if (isReservationCollisions(reservation, editedReservation)) {
                    throw new IllegalActionException("One of the rowers belong to this reservation" +
                            " Already have reservation in the requested time");
                }
            }
        }
        addPartnersToReservation(getRowersOfReservation(reservationToEdit), editedReservation);
        removePartnersFromReservation(reservationToEdit);
    }

    public void editReservationTimeSlot(Reservation reservationToEdit, TimeSlot updatedTimeSlot)
            throws IllegalActionException {
        Reservation editedReservation = new Reservation(reservationToEdit.getReservationOwner(),
                reservationToEdit.getPracticeDate(), updatedTimeSlot.getStartTime(),
                updatedTimeSlot.getEndTime(), reservationToEdit.getBoatTypes());
        editedReservation.setDateOfReservation(reservationToEdit.getDateOfReservation());
        editedReservation.setTimeOfReservation(reservationToEdit.getTimeOfReservation());
        editedReservation.setParticipants(reservationToEdit.getParticipants());
        List <Rower> rowersOfReservation = getRowersOfReservation(reservationToEdit);
        for (Rower rower: rowersOfReservation) {
            for (Reservation reservation : listOfReservationsByRowers.get(rower)) {
                if (isReservationCollisions(reservation, editedReservation)) {
                    throw new IllegalActionException("One of the rowers belong to this reservation" +
                            " Already have reservation in the requested time");
                }
            }
        }
        addPartnersToReservation(getRowersOfReservation(reservationToEdit), editedReservation);
        removePartnersFromReservation(reservationToEdit);
    }

    public Reservation addBoatTypeToReservation(Reservation reservationToEdit, SimpleBoatType boatTypeToAdd)
            throws IllegalActionException {
        if(reservationToEdit.getBoatTypes().contains(boatTypeToAdd))
            throw new IllegalActionException("You already chose this boat type");

        getRowersOfReservation(reservationToEdit).forEach(rower ->
                listOfReservationsByRowers.get(rower).forEach(reservation -> {
                    if(reservation.equals(reservationToEdit)) {
                            reservation.getBoatTypes().add(boatTypeToAdd);
                        }
                }));

        reservationToEdit.getBoatTypes().add(boatTypeToAdd);
        return reservationToEdit;
    }

    public Reservation removeBoatTypeFromReservation(Reservation reservationToEdit, SimpleBoatType boatTypeToRemove)
            throws IllegalActionException {

        if(reservationToEdit.getBoatTypes().size() == 1)
            throw new IllegalActionException("You must have at least one boat type in reservation");

        getRowersOfReservation(reservationToEdit).forEach(rower ->
                listOfReservationsByRowers.get(rower).forEach(reservation -> {
                    if(reservation.equals(reservationToEdit)) {
                        reservation.getBoatTypes().remove(boatTypeToRemove);
                    }
                }));
        reservationToEdit.getBoatTypes().remove(boatTypeToRemove);
        return reservationToEdit;
    }

    public void mergeReservations(Reservation reservationToAssign, Reservation reservationToMerge)
            throws IllegalActionException {
        if(reservationToAssign.equals(reservationToMerge))
            throw new IllegalActionException("You've already chosen this reservation");


        addPartnersToReservation(getRowersOfReservation(reservationToMerge), reservationToAssign);
        removePartnersFromReservation(reservationToMerge);
    }

    public void splitReservations(Reservation reservationToAssign, Rower rowerToSplit) {


        Reservation newReservation = new Reservation(rowerToSplit.getEmail(), reservationToAssign.getPracticeDate(),
                reservationToAssign.getStartTime(), reservationToAssign.getEndTime(), reservationToAssign.getBoatTypes());

        try {
            removeRowerFromReservation(rowerToSplit, reservationToAssign);
        } catch (IllegalActionException ignored) {}
        newReservation.addParticipants(rowerToSplit.getEmail());
        listOfReservationsByRowers.get(rowerToSplit).remove(reservationToAssign);
        listOfReservationsByRowers.get(rowerToSplit).add(newReservation);
    }

    //-------------------------------edit Time Slots details------------------------------------------------------------

    public void addNewTimeSlot(String activityName, LocalTime startTime, LocalTime endTime, SimpleBoatType activityBoat)
            throws IllegalActionException {
        if(isTimeSlotExistOnSystem(activityName, startTime, endTime, activityBoat))
            throw new IllegalActionException("the time slot you're trying to enter exist on the system");

        timeSlots.add(new TimeSlot(activityName, startTime, endTime, activityBoat));
    }

    public void removeTimeSlotFromSystem(TimeSlot timeSlotToRemove) {
        timeSlots.remove(timeSlotToRemove);
    }

    public void editTimeSlotStartTime(TimeSlot timeSlotToEdit, LocalTime newStartTime)throws IllegalActionException {
        if(isTimeSlotExistOnSystem(timeSlotToEdit.getActivityName(), newStartTime, timeSlotToEdit.getEndTime(), timeSlotToEdit.getBoatType()))
            throw new IllegalActionException("the time slot you're trying to enter exist on the system");
        if(newStartTime.isAfter(timeSlotToEdit.getEndTime()))
            throw new IllegalActionException("Start time must be before end time");

        int indexToEdit = timeSlots.indexOf(timeSlotToEdit);
        timeSlots.get(indexToEdit).setStartTime(newStartTime);
    }

    public void editTimeSlotEndTime(TimeSlot timeSlotToEdit, LocalTime newEndTime)throws IllegalActionException {
        if(isTimeSlotExistOnSystem(timeSlotToEdit.getActivityName(), timeSlotToEdit.getStartTime(), newEndTime, timeSlotToEdit.getBoatType()))
            throw new IllegalActionException("the time slot you're trying to enter exist on the system");
        if(newEndTime.isBefore(timeSlotToEdit.getStartTime()))
            throw new IllegalActionException("End time must be after start time");

        int indexToEdit = timeSlots.indexOf(timeSlotToEdit);
        timeSlots.get(indexToEdit).setEndTime(newEndTime);
    }

    public void editTimeSlotActivityName(TimeSlot timeSlotToEdit, String newActivityName) throws IllegalActionException {
        if(isTimeSlotExistOnSystem(newActivityName, timeSlotToEdit.getStartTime(), timeSlotToEdit.getEndTime(), timeSlotToEdit.getBoatType()))
            throw new IllegalActionException("the time slot you're trying to enter exist on the system");
        int indexToEdit = timeSlots.indexOf(timeSlotToEdit);
        timeSlots.get(indexToEdit).setActivityName(newActivityName);
    }

    public void editTimeSlotBoatType(TimeSlot timeSlotToEdit, SimpleBoatType newBoatType) throws IllegalActionException {
        if(isTimeSlotExistOnSystem(timeSlotToEdit.getActivityName(), timeSlotToEdit.getStartTime(), timeSlotToEdit.getEndTime(), newBoatType))
            throw new IllegalActionException("the time slot you're trying to enter exist on the system");
        int indexToEdit = timeSlots.indexOf(timeSlotToEdit);
        timeSlots.get(indexToEdit).setBoatType(newBoatType);
    }

    //-------------------------------edit Assigns details---------------------------------------------------------------

    public boolean isAutoAssigning(Reservation reservationToAssign) throws IllegalActionException {
        List <Rower> rowerOfReservation = getRowersOfReservation(reservationToAssign);
        for (Rower rower: rowerOfReservation) {
            if(rower.hasPrivateBoat()){
                Boat rowerPrivateBoat = getBoat(rower.getPrivateBoatID());
                if(rowerPrivateBoat != null &&
                        rowerPrivateBoat.getBoatType().getNumberOfRowers() ==
                                getRowersOfReservation(reservationToAssign).size())
                    addAssigning(reservationToAssign, rowerPrivateBoat);
                return true;
            }
        }
        return false;
    }

    public void addAssigning(Reservation reservationToAssign, Boat boatToAssign) {
        try {
            getRowersOfReservation(reservationToAssign).forEach(rower -> {
                int indexToEdit = listOfReservationsByRowers.get(rower).indexOf(reservationToAssign);
                listOfReservationsByRowers.get(rower).get(indexToEdit).setApproved(true);
            });
        } catch (IllegalActionException ignored) {}
        Reservation assignedReservation = new Reservation(reservationToAssign.getReservationOwner(), reservationToAssign.getPracticeDate(),
                reservationToAssign.getStartTime(), reservationToAssign.getEndTime(), reservationToAssign.getBoatTypes());
        assignedReservation.setApproved(true);
        assignedReservation.setTimeOfReservation(reservationToAssign.getTimeOfReservation());
        assignedReservation.setDateOfReservation(reservationToAssign.getDateOfReservation());
        assignedReservation.setParticipants(reservationToAssign.getParticipants());
        listOfAssigns.add(new Assigning(assignedReservation, boatToAssign));
    }

    public void removeAssigning(Assigning assigningToRemove) throws IllegalActionException {
        List<Rower> rowersOfReservation = getRowersOfReservation(assigningToRemove.getReservation());
        listOfAssigns.remove(assigningToRemove);
        for (Rower rower: rowersOfReservation ) {
            int indexToEdit = listOfReservationsByRowers.get(rower).indexOf(assigningToRemove.getReservation());
            listOfReservationsByRowers.get(rower).get(indexToEdit).setApproved(false);
        }
    }

    public void editAssignedBoat(Assigning assigningToEdit, Boat newBoatToAssign) throws IllegalActionException {
        if(!assigningToEdit.getAssignedBoats().getBoatType().equals(newBoatToAssign.getBoatType()))
            throw new IllegalActionException("Chosen boat doesn't match requested boat type");

        int indexToEdit = listOfAssigns.indexOf(assigningToEdit);
        listOfAssigns.get(indexToEdit).setAssignedBoats(newBoatToAssign);
    }

    //-------------------------------XML code --------------------------------------------------------------------------


    public void importActivities(String xmlFile) throws JAXBException,
            IllegalActionException,IllegalArgumentException {
        try {
            InputStream inputStream = new ByteArrayInputStream(xmlFile.getBytes());
            Activities activities = XmlHandler.deserializeFromActivities(inputStream);
            loadActivities(activities);
        }catch (NullPointerException exception){
            throw new IllegalActionException("the given xml file didn't match the required schema");
        }catch (JAXBException e) {
            throw new JAXBException("the given file is damaged and isn't written according to Xml rules");
        }
    }

    public void importBoats(String xmlFile) throws JAXBException, IllegalArgumentException, IllegalActionException {
        try{
            InputStream inputStream = new ByteArrayInputStream(xmlFile.getBytes());
            Boats boats = XmlHandler.deserializeFromBoats(inputStream);
            loadBoats(boats);
        }catch (NullPointerException exception){
            throw new IllegalActionException("the given xml file didn't match the required schema");
        }catch (JAXBException e) {
            throw new JAXBException("the given file is damaged and isn't written according to Xml rules");
        }
    }

    public void importMembers(String xmlFile) throws JAXBException, IllegalActionException {
        try {
            InputStream inputStream = new ByteArrayInputStream(xmlFile.getBytes());
            Members members = XmlHandler.deserializeFromMembers(inputStream);
            loadMembers(members);
        } catch (NullPointerException exception) {
            throw new IllegalActionException("the given xml file didn't match the required data schema");
        } catch (JAXBException e) {
            throw new JAXBException("the given file is damaged and isn't written according to Xml rules");
        }
    }

    private void loadActivities(Activities activities) throws IllegalActionException, IllegalArgumentException{
        List<Timeframe> timeframes = activities.getTimeframe();
        SimpleBoatType timeSlotBoatType = null;
        StringBuilder errorMessage = new StringBuilder();
        int errorCounter = 0;
        for(Timeframe timeframe:timeframes){
            try {
                if(timeframe.getBoatType() != null) {
                    timeSlotBoatType = SimpleBoatType.parseBoatTypeToSimpleBoatType(timeframe.getBoatType());
                }
                addNewTimeSlot(timeframe.getName(),
                        xmlHandler.parseStringToLocalTime(timeframe.getStartTime()),
                        xmlHandler.parseStringToLocalTime(timeframe.getEndTime()),
                        timeSlotBoatType);
            }
            catch (IllegalArgumentException | IllegalActionException exception){
                errorCounter++;
                errorMessage.append("Couldn't import record for activity : "+ timeframe.getName()+","+
                        "due to: " + exception.getMessage()+"\n");
            }
        }
        if(errorCounter > 0)
            throw new IllegalActionException(errorMessage.toString());
    }

    private void loadBoats(Boats boats) throws IllegalActionException {
        List<jaxb.generated.Boats.Boat> jaxbBoats = boats.getBoat();
        boolean isPrivate = false, isWide = false, isCostal = false,isInRepair = false;
        StringBuilder errorMessage = new StringBuilder();
        int errorCounter = 0;
        for(jaxb.generated.Boats.Boat jaxbBoat: jaxbBoats){
            try {
                if(jaxbBoat.isPrivate() != null)
                    isPrivate = jaxbBoat.isPrivate();
                if(jaxbBoat.isWide() != null)
                    isWide = jaxbBoat.isWide();
                if(jaxbBoat.isCostal() != null)
                    isCostal = jaxbBoat.isCostal();
                if(jaxbBoat.isOutOfOrder() != null)
                    isInRepair = jaxbBoat.isOutOfOrder();
                addNewBoat(jaxbBoat.getId(), jaxbBoat.getName(),
                        SimpleBoatType.parseBoatTypeToSimpleBoatType(jaxbBoat.getType()),
                        isPrivate, isWide, isCostal, isInRepair);
            }
            catch (IllegalActionException exception){
                errorCounter++;
                errorMessage.append("Couldn't import record for boat id: "+ jaxbBoat.getId()+","+
                        "due to: " + exception.getMessage()+"\n");
            }
        }
        if (errorCounter > 0)
            throw new IllegalActionException(errorMessage.toString());
    }

    private void loadMembers(Members members) throws IllegalActionException {
        List<Member> listOfMembers = members.getMember();
        String comments = "", privateBoatID = "", phoneNumber = "";
        int age = -1;
        boolean hasPrivateBoat = false, isManager = false;
        LocalDate registrationDate = null;
        LocalDate expirationDate = null;
        Level rowingLevel = Level.BEGINNER;
        StringBuilder errorMessage = new StringBuilder();
        int errorCounter = 0;
        for(Member member: listOfMembers){
            try {
                if(member.getJoined() != null)
                    registrationDate = xmlHandler.parseXMLGregorianCalendarToLocalDate(member.getJoined());
                if(member.getMembershipExpiration() != null)
                    expirationDate = xmlHandler.parseXMLGregorianCalendarToLocalDate(member.getMembershipExpiration());
                if(member.getAge() != null)
                    age = member.getAge();
                if(member.getComments() != null)
                    comments = member.getComments();
                if(member.isHasPrivateBoat()!= null)
                    hasPrivateBoat = member.isHasPrivateBoat();
                if(member.getPrivateBoatId() != null)
                    privateBoatID = member.getPrivateBoatId();
                if(member.getPhone() != null)
                    phoneNumber = member.getPhone();
                if(member.getLevel() != null)
                    rowingLevel = xmlHandler.parseRowingLevelFromXml(member.getLevel());
                if(member.isManager() != null)
                    isManager = member.isManager();

                addNewRower(member.getId(), member.getName(), member.getEmail(),
                        member.getPassword(), phoneNumber, age, isManager, comments, rowingLevel, registrationDate,
                        expirationDate, hasPrivateBoat, privateBoatID);
            }
            catch (IllegalArgumentException | IllegalActionException exception){
                errorCounter++;
                errorMessage.append("Couldn't import record for member id: "+ member.getId()+","+
                        "due to: " + exception.getMessage()+"\n");
            }
        }
        if(errorCounter > 0)
            throw new IllegalActionException(errorMessage.toString());
    }

    public String exportActivities() throws JAXBException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Activities activitiesToExport = new Activities();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Activities.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            for (TimeSlot timeSlot: timeSlots) {
                Timeframe newTimeFrame = new Timeframe();
                newTimeFrame.setName(timeSlot.getActivityName());
                newTimeFrame.setStartTime(timeSlot.getStartTime().toString());
                newTimeFrame.setEndTime(timeSlot.getEndTime().toString());
                if(timeSlot.getBoatType() != null)
                    newTimeFrame.setBoatType(xmlHandler.parseSimpleBoatTypeToBoatType
                            (timeSlot.getBoatType()));

                activitiesToExport.getTimeframe().add(newTimeFrame);
            }
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(activitiesToExport, outputStream);
            return outputStream.toString();
        }
        catch (JAXBException e) {
            throw new JAXBException("JAXB Exception.\n" + "couldn't export data.");
        }
    }

    public String exportBoats() throws JAXBException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Boats boatsToExport = new Boats();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Boats.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            for (Boat boat: listOfBoats) {
                jaxb.generated.Boats.Boat jaxbBoat = new jaxb.generated.Boats.Boat();
                jaxbBoat.setName(boat.getBoatName());
                jaxbBoat.setId(boat.getSerialNumber());
                jaxbBoat.setType(xmlHandler.parseSimpleBoatTypeToBoatType(boat.getBoatType()));
                jaxbBoat.setCostal(boat.isCostal());
                jaxbBoat.setHasCoxswain(boat.isHasCoxswain());
                jaxbBoat.setOutOfOrder(boat.isInRepair());
                jaxbBoat.setPrivate(boat.isPrivateBoat());

                boatsToExport.getBoat().add(jaxbBoat);
            }
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(boatsToExport, outputStream);
            return outputStream.toString();
        }
        catch (JAXBException e) {
            throw new JAXBException("JAXB Exception.\n" + "couldn't export data.");
        }
    }

    public String exportMembers() throws JAXBException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Members membersToExport = new Members();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Members.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            for (Rower rower : listOfReservationsByRowers.keySet()) {
                Member newMember = new Member();
                newMember.setName(rower.getUserName());
                newMember.setId(rower.getID());
                newMember.setEmail(rower.getEmail());
                newMember.setPassword(rower.getPassword());
                newMember.setAge(rower.getAge());
                newMember.setPhone(rower.getPhoneNumber());
                newMember.setHasPrivateBoat(rower.hasPrivateBoat());
                newMember.setPrivateBoatId(rower.getPrivateBoatID());
                newMember.setJoined(xmlHandler.parseLocalDateToXMLGregorianCalendar(rower.getDateOfRegistration()));
                newMember.setMembershipExpiration(xmlHandler.parseLocalDateToXMLGregorianCalendar(rower.getDateOfExpiration()));
                newMember.setComments(rower.getComment());
                newMember.setLevel(xmlHandler.parseRowingLevelToXml(rower.getLevel()));
                membersToExport.getMember().add(newMember);
            }
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(membersToExport, outputStream);
            return outputStream.toString();
        }
        catch (JAXBException | DatatypeConfigurationException e) {
            throw new JAXBException("JAXB Exception.\n" + "couldn't export data.");
        }
    }
}