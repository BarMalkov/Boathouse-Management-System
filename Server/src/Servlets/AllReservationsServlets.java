package Servlets;

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


@WebServlet(name = "allReservationServlet", urlPatterns = "/allReservations")
public class AllReservationsServlets extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        List <Reservation> nextWeekReservations = null;
        List <Reservation> lastWeekReservations = null;
        String reservations = "";
        try {
            nextWeekReservations = serverEngine.getReservationsForManager(ReservationFilter.NextWeek,
                    null, false, null, null );
        } catch (IllegalActionException ignored) {}
        try {
            lastWeekReservations = serverEngine.getReservationsForManager(ReservationFilter.LastWeek,
                    null, false, null, null );
        } catch (IllegalActionException ignored) {}
        if(nextWeekReservations != null && lastWeekReservations != null){
            lastWeekReservations.addAll(nextWeekReservations);
            reservations = gson.toJson(lastWeekReservations);
        }
        else if(nextWeekReservations == null){
            reservations = gson.toJson(lastWeekReservations);
        }
        else if(lastWeekReservations == null){
            reservations = gson.toJson(nextWeekReservations);
        }
        System.out.println(reservations);
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.println(reservations);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String reservationToRemove = reader.lines().collect(Collectors.joining());
        Reservation reservation = gson.fromJson(reservationToRemove, Reservation.class);
        System.out.println(reservation);
        try {
            serverEngine.removePartnersFromReservation(reservation);
            serverEngine.saveState();
            try (PrintWriter out = resp.getWriter()) {
                out.print("reservation deleted successfully");
            }
        } catch (JAXBException | IllegalActionException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }
}
