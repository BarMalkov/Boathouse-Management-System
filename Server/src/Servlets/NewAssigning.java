package Servlets;

import BoatPackage.SimpleBoatType;
import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.Assigning;
import ReservationPackage.Reservation;
import ReservationPackage.ReservationFilter;
import ReservationPackage.TimeSlot;
import RowerPackage.Rower;
import com.google.gson.Gson;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@WebServlet(name = "NewAssigningServlet", urlPatterns = "/newAssigning")
public class NewAssigning extends HttpServlet {
    private Gson gson = new Gson();
    Reservation reservationToAssign;



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String assigningStr = reader.lines().collect(Collectors.joining());
        System.out.println(assigningStr);
        Assigning assigning = gson.fromJson(assigningStr, Assigning.class);
        try {
            while(serverEngine.isNeedToMergeReservation(assigning.getReservation(), assigning.getAssignedBoats())){
                try {
                    List<Reservation> possibleMatch = serverEngine.getReservationsForManager(ReservationFilter.ByDay,
                            assigning.getReservation().getPracticeDate(), true,
                            assigning.getReservation().getStartTime(), assigning.getReservation().getEndTime());
                    Reservation reservationToMerge = possibleMatch.get(0);
                    for (Reservation reservation: possibleMatch) {
                        if(!reservation.equals(assigning.getReservation())) {
                            reservationToMerge = reservation;
                            break;
                        }
                    }
                    serverEngine.mergeReservations(assigning.getReservation(), reservationToMerge);
                }catch (IllegalActionException e){
                    try (PrintWriter out = resp.getWriter()) {
                        out.print("couldn't create assigning because not enough rowers are signed to this time slot");
                        return;
                    }
                }
            }
            while(serverEngine.isNeedToSplitReservation(assigning.getReservation(), assigning.getAssignedBoats())){

                serverEngine.splitReservations(assigning.getReservation(),
                        serverEngine.getRower(assigning.getReservation().getParticipants().get(0)));
            }

            List<Rower> rowers = serverEngine.getRowersOfReservation(assigning.getReservation());
            List <String> rowerEmails = new ArrayList<>();
            rowers.forEach(rower -> {rowerEmails.add(rower.getEmail());});
            assigning.getReservation().setParticipants(rowerEmails);
            serverEngine.addAssigning(assigning.getReservation(), assigning.getAssignedBoats());
            serverEngine.addAlert("Your reservation of " +
                    assigning.getReservation().getPracticeDate().toString() +
                    " starting at " + assigning.getReservation().getStartTime().toString() + " is approved",
                    assigning.getReservation().getParticipants());
            try (PrintWriter out = resp.getWriter()) {
                out.print("assigning added successfully");
            }
            serverEngine.saveState();
        } catch (JAXBException | IllegalActionException exception) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(exception.getMessage());
            }
        }
    }
}
