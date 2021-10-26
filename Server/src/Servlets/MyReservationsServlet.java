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
import java.util.stream.Collectors;

@WebServlet(name = "reservationServlet", urlPatterns = "/myReservations")
public class MyReservationsServlet extends HttpServlet {

    private Gson gson = new Gson();
    Rower loggedInUser;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        loggedInUser= ServletUtils.getLoggedInUser(getServletContext());
        String reservations = "";
        try {
            reservations = gson.toJson(serverEngine.getUserReservations(loggedInUser,
                    ReservationFilter.NextWeek, null, false));
        } catch (IllegalActionException ignored) {}
        System.out.println(reservations);
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.println(reservations);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        loggedInUser= ServletUtils.getLoggedInUser(getServletContext());
        BufferedReader reader = req.getReader();
        String reservationToRemove = reader.lines().collect(Collectors.joining());
        Reservation reservation = gson.fromJson(reservationToRemove, Reservation.class);
        System.out.println(reservation);
        try {
            serverEngine.removeRowerFromReservation(loggedInUser, reservation);
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
