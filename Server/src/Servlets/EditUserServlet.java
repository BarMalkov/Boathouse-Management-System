package Servlets;


import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.Reservation;
import ReservationPackage.ReservationFilter;
import RowerPackage.Rower;
import com.google.gson.Gson;
import constants.Constants;
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

@WebServlet(name = "editUserServlet", urlPatterns = "/editUser")
public class EditUserServlet extends HttpServlet {
    Gson gson = new Gson();
    Rower rowerBeforeEdit;
    Rower rowerAfterEdit;
    List<Reservation> rowerReservations = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String usersArray = reader.lines().collect(Collectors.joining());
        System.out.println(usersArray);
        Rower[] rowersArray = gson.fromJson(usersArray, Rower[].class);
        rowerBeforeEdit = rowersArray[0];
        rowerAfterEdit = rowersArray[1];
        try {
            rowerReservations = serverEngine.getUserReservations(rowerBeforeEdit, ReservationFilter.NextWeek,
                    null, false);
        } catch (IllegalActionException ignored) {}
        try {
            Rower masterAdmin = (Rower) getServletContext().getAttribute(Constants.USER_MANAGER_ATTRIBUTE_NAME);
            serverEngine.removeUser(rowerBeforeEdit, masterAdmin);
            serverEngine.addNewRower(rowerAfterEdit.getID(), rowerAfterEdit.getUserName(), rowerAfterEdit.getEmail(),
                    rowerAfterEdit.getPassword(), rowerAfterEdit.getPhoneNumber(), rowerAfterEdit.getAge(),
                    rowerAfterEdit.isManager(), rowerAfterEdit.getComment(), rowerAfterEdit.getLevel(),
                    rowerAfterEdit.getDateOfRegistration(), rowerAfterEdit.getDateOfExpiration(),
                    rowerAfterEdit.hasPrivateBoat(), rowerAfterEdit.getPrivateBoatID());
            try(PrintWriter out = resp.getWriter()){
                out.print("User Edit Successfully");
            }
            for (Reservation reservation: rowerReservations) {
                serverEngine.addRowerToReservation(rowerAfterEdit, reservation);
            }
        }catch (IllegalActionException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
            try{
                serverEngine.addNewRower(rowerBeforeEdit.getID(), rowerBeforeEdit.getUserName(), rowerBeforeEdit.getEmail(),
                    rowerBeforeEdit.getPassword(), rowerBeforeEdit.getPhoneNumber(), rowerBeforeEdit.getAge(),
                    rowerBeforeEdit.isManager(), rowerBeforeEdit.getComment(), rowerBeforeEdit.getLevel(),
                    rowerBeforeEdit.getDateOfRegistration(), rowerBeforeEdit.getDateOfExpiration(),
                    rowerBeforeEdit.hasPrivateBoat(), rowerBeforeEdit.getPrivateBoatID());
                for (Reservation reservation: rowerReservations) {
                    serverEngine.addRowerToReservation(rowerBeforeEdit, reservation);
                }
            }catch(IllegalActionException ignored) {}
        }
        try {
            serverEngine.saveState();
        } catch (JAXBException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }

    }
}
