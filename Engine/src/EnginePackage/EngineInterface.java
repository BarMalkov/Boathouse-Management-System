package EnginePackage;

import BoatPackage.Boat;
import BoatPackage.SimpleBoatType;
import Errors.IllegalActionException;
import ReservationPackage.Assigning;
import ReservationPackage.Reservation;
import ReservationPackage.ReservationFilter;
import ReservationPackage.TimeSlot;
import RowerPackage.Level;
import RowerPackage.Rower;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface EngineInterface {

    List<Level> getRowersLevels();

    List<Assigning> getListOfAssigns() throws IllegalActionException;

    List<ReservationFilter> getReservationFilters();

    List<TimeSlot> getTimeSlots() throws IllegalActionException;

    List<Reservation> getUserReservations(Rower LoggedInUser, ReservationFilter reservationFilter, LocalDate dateToShow, boolean showToEdit) throws IllegalActionException;

    List<Reservation> getReservationsForManager(ReservationFilter reservationFilter, LocalDate dateToShow,
                                                boolean showToEdit, LocalTime startTime, LocalTime endTime) throws IllegalActionException;

    List<SimpleBoatType> getSimpleBoatTypeList();

    List<LocalDate> getAvailableDatesList(boolean fromTomorrow);

    Rower getRower(String email);

    boolean isValidEmail(String email);

    void editUserName(Rower rowerToEdit, String userName);

    void editUserPassword(Rower rowerToEdit, String password);

    Rower editUserEmail( Rower rowerToEdit, String email) throws IllegalActionException;

    void editUserPhoneNumber(Rower rowerToEdit, String phoneNumber);

    void addNewBoat(String boatID, String boatName, SimpleBoatType boatType, boolean isPrivate,
                    boolean isWide, boolean isCostal, boolean isInRepair) throws IllegalArgumentException, IllegalActionException;

    List<Boat> getAllBoats() throws IllegalActionException;

    void deleteReservation(Rower LoggedInUser, Reservation reservationToDelete);

    TimeSlot createFreeTimeSlot(LocalTime startTime, LocalTime endTime) throws IllegalActionException;

    Reservation createNewReservation(Rower LoggedInUser, LocalDate reservationDate, TimeSlot timeSlot) throws IllegalActionException;

    List<Rower> getAllUsers();

    void addPartnersToReservation(List<Rower> partnersForReservation, Reservation reservation);

    void addRowerToReservation(Rower rowerToAdd, Reservation reservationToAdd) throws IllegalActionException;

    void editReservationDate(Reservation reservationToEdit, LocalDate updatedDate) throws IllegalActionException;

    void editReservationTimeSlot(Reservation reservationToEdit, TimeSlot updatedTimeSlot) throws IllegalActionException;

    void removePartnersFromReservation(Reservation reservationToEdit) throws IllegalActionException;

    List<Rower> getRowersOfReservation(Reservation reservationToEdit) throws IllegalActionException;

    List<SimpleBoatType> getBoatTypesOfReservation(Reservation reservationToEdit);

    List<Boat> getBoatsOfReservation(Reservation reservationToAssign) throws IllegalActionException;

    Boat getRecommendBoat(Reservation reservationToAssign) throws IllegalActionException;

    void removeUser(Rower userToRemove, Rower LoggedInUser) throws IllegalActionException;

    void addNewTimeSlot(String activityName, LocalTime startTime, LocalTime endTime, SimpleBoatType activityBoat) throws IllegalActionException;

    Reservation addBoatTypeToReservation(Reservation reservationToEdit, SimpleBoatType simpleBoatType)throws IllegalActionException;

    boolean isUserExist(String rowerEmail);

    boolean isPasswordMatchEmail(String email, String password);

    void removeRowerFromReservation(Rower chosenRowerOfReservation, Reservation reservation) throws IllegalActionException;

    Reservation removeBoatTypeFromReservation(Reservation reservationToEdit, SimpleBoatType chosenSimpleBoatType) throws IllegalActionException;

    boolean isValidBoatID(String boatID);

    void removeBoatFromSystem(Boat boatToRemove) throws IllegalActionException;

    void editBoatSerialNumber(Boat boatToEdit, String boatID) throws IllegalActionException;

    void editBoatName(Boat boatToEdit, String newName);

    void editBoatPrivate(Boat boatToEdit, Rower rowerToEdit) throws IllegalActionException;

    void editBoatNotPrivate(Boat boatToEdit) throws IllegalActionException;

    void editBoatInRepair(Boat boatToEdit) throws IllegalActionException;

    void editBoatNotInRepair(Boat boatToEdit) throws IllegalActionException;

    void removeTimeSlotFromSystem(TimeSlot timeSlotToRemove);

    void editTimeSlotStartTime(TimeSlot timeSlotToEdit, LocalTime newStartTime)throws IllegalActionException;

    void editTimeSlotEndTime(TimeSlot timeSlotToEdit, LocalTime newEndTime)throws IllegalActionException;

    void editTimeSlotActivityName(TimeSlot timeSlotToEdit, String newActivityName) throws IllegalActionException;

    void editTimeSlotBoatType(TimeSlot timeSlotToEdit, SimpleBoatType chooseSimpleBoatType) throws IllegalActionException;


    void addNewRower(String userID, String userName, String email, String password,
                     String phoneNumber, int age, boolean isManager, String comment,
                     Level rowerLevel, LocalDate dateOfRegistration, LocalDate dateOfExpiration,
                     boolean hasPrivateBoat, String boatSerialNumber) throws IllegalActionException;

    void editBoatPaddles(Boat boatToEdit, SimpleBoatType simpleBoatType) throws IllegalActionException;

    boolean isAssignedBoat(Boat boatToRemove);

    boolean isValidUserID(String userID);

    boolean isNeedToMergeReservation(Reservation reservationToAssign, Boat boatToAssign) throws IllegalActionException;

    boolean isNeedToSplitReservation(Reservation reservationToAssign, Boat boatToAssign) throws IllegalActionException;

    void mergeReservations(Reservation reservationToAssign, Reservation reservationToMerge)throws IllegalActionException;

    void splitReservations(Reservation reservationToAssign, Rower rowerToSplit);

    boolean isAutoAssigning(Reservation reservationToAssign) throws IllegalActionException;

    void addAssigning(Reservation reservationToAssign, Boat boatToAssign);

    void removeAssigning(Assigning assigningToRemove) throws IllegalActionException;

    void editAssignedBoat(Assigning assigningToEdit, Boat newBoatToAssign) throws IllegalActionException;

    void importActivities(String inputXmlPath) throws FileNotFoundException, JAXBException, IllegalActionException,IllegalArgumentException;

    void importBoats(String inputXmlPath) throws JAXBException, FileNotFoundException, IllegalArgumentException, IllegalActionException;

    void importMembers(String inputXmlPath) throws FileNotFoundException, JAXBException, IllegalActionException;

    String exportActivities() throws JAXBException;

    String exportBoats() throws JAXBException;

    String exportMembers() throws JAXBException;

    void saveState() throws JAXBException;
}
