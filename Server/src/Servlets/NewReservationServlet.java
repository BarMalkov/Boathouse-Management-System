package Servlets;

import BoatPackage.SimpleBoatType;
import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.Reservation;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "NewReservationServlet", urlPatterns = "/newReservation")
public class NewReservationServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Rower loggedInUser = ServletUtils.getLoggedInUser(getServletContext());
        SimpleBoatType[] boatTypeList  = { SimpleBoatType.Single, SimpleBoatType.Dual_One_Paddle};
        String[] participants = {loggedInUser.getEmail()};
        Reservation demoReservation = new Reservation(loggedInUser.getEmail(), LocalDate.now(), LocalTime.now().minusHours(1),
                LocalTime.now().plusHours(1), Arrays.asList(boatTypeList.clone()));
        demoReservation.setParticipants(Arrays.asList(participants.clone()));
        String reservationToServer = gson.toJson(demoReservation);
        resp.setContentType("application/json");
        try(PrintWriter out = resp.getWriter()){
            out.println(reservationToServer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String reservationStr = reader.lines().collect(Collectors.joining());
        Reservation newReservation = gson.fromJson(reservationStr, Reservation.class);
        if(newReservation.getStartTime().isAfter(newReservation.getEndTime()) ||
                newReservation.getStartTime().equals(newReservation.getEndTime())){
            try (PrintWriter out = resp.getWriter()) {
                out.print("couldn't add reservation\n" +
                        "start time must be after end time");
            }
            return;
        }
        int indexOfUserToAdd = newReservation.getParticipants().size() - 1;
        List<String> reservationParticipants = newReservation.getParticipants();
        if(serverEngine.isUserExist(reservationParticipants.get(indexOfUserToAdd))){
            try {
                serverEngine.addRowerToReservation(serverEngine.getRower(reservationParticipants.get(indexOfUserToAdd)),
                        newReservation);
                serverEngine.saveState();
                try (PrintWriter out = resp.getWriter()) {
                    out.print("reservation added successfully");
                }
            } catch (IllegalActionException | JAXBException exception) {
                try (PrintWriter out = resp.getWriter()) {
                    out.print(exception.getMessage());
                }
            }
        }else{
            try (PrintWriter out = resp.getWriter()) {
                out.print("user doesn't exist on the system");
            }
        }

    }
}
