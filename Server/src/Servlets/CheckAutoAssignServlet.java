package Servlets;

import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.Reservation;
import com.google.gson.Gson;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet(name = "checkAutoAssignServlet", urlPatterns = "/checkAutoAssign")
public class CheckAutoAssignServlet extends HttpServlet {
    Gson gson = new Gson();
    Reservation reservationToAssign;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String reservationStr = reader.lines().collect(Collectors.joining());
        reservationToAssign = gson.fromJson(reservationStr, Reservation.class);
        try {
            try (PrintWriter out = resp.getWriter()) {
                out.print(serverEngine.isAutoAssigning(reservationToAssign));
            }
        } catch (IllegalActionException exception) {}
    }
}
