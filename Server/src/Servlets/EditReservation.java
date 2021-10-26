package Servlets;

import BoatPackage.Boat;
import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.Reservation;
import ReservationPackage.ReservationFilter;
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


@WebServlet(name = "editReservationServlet", urlPatterns = "/editReservation")
public class EditReservation extends HttpServlet {

    Gson gson = new Gson();
    Reservation reservationBeforeEdit;
    Reservation reservationAfterEdit;
    boolean returnToOldReservation = false;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String reservationsArray = reader.lines().collect(Collectors.joining());
        Reservation[] reservations = gson.fromJson(reservationsArray, Reservation[].class);
        reservationBeforeEdit = reservations[0];
        reservationAfterEdit = reservations[1];
        if(reservationAfterEdit.getStartTime().isAfter(reservationAfterEdit.getEndTime()) ||
                reservationAfterEdit.getStartTime().equals(reservationAfterEdit.getEndTime())){
            try (PrintWriter out = resp.getWriter()) {
                out.print("couldn't add reservation\n" +
                        "start time must be after end time");
            }
            return;
        }
        StringBuilder errorMessage = new StringBuilder();
        List<Rower> rowerList = new ArrayList<>();
        try {
            rowerList = serverEngine.getRowersOfReservation(reservationBeforeEdit);
        } catch (IllegalActionException ignored) {}
        try {
            serverEngine.removePartnersFromReservation(reservationBeforeEdit);
        } catch (IllegalActionException ignored) {}
        if(reservationAfterEdit.getParticipants().size() > 0){
            reservationAfterEdit.getParticipants().forEach(rowerEmail -> {
                if(serverEngine.isUserExist(rowerEmail)) {
                    try {
                        serverEngine.addRowerToReservation(serverEngine.getRower(rowerEmail), reservationAfterEdit);
                    } catch (IllegalActionException exception) {
                        returnToOldReservation = true;
                        errorMessage.append( rowerEmail + exception.getMessage()+"\n");
                    }
                }else{
                    errorMessage.append(rowerEmail + "doesn't exist on the system\n");
                    returnToOldReservation = true;
                }
            });
        }
        if(returnToOldReservation){
            try {
                serverEngine.removePartnersFromReservation(reservationAfterEdit);
            } catch (IllegalActionException ignored) {}
            serverEngine.addPartnersToReservation(rowerList, reservationBeforeEdit);
            errorMessage.append("reservations update failed");
            errorMessage.toString();
            try (PrintWriter out = resp.getWriter()) {
                out.print(errorMessage);
            }
        }
        else {
            try {
                serverEngine.saveState();
                try (PrintWriter out = resp.getWriter()) {
                    out.print("reservation updated successfully");
                }
            } catch (JAXBException e) {
                try (PrintWriter out = resp.getWriter()) {
                    out.print(e.getMessage());
                }
            }
        }

    }
}
