package Servlets;


import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.TimeSlot;
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

@WebServlet(name = "activitiesServlet", urlPatterns = "/activity")
public class ActivitiesServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        String activities = "";
        try {
            activities = gson.toJson(serverEngine.getTimeSlots());
        } catch (IllegalActionException ignored) {}
        System.out.println(activities);
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.println(activities);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String activityToRemove = reader.lines().collect(Collectors.joining());
        TimeSlot timeSlot = gson.fromJson(activityToRemove, TimeSlot.class);
        System.out.println(timeSlot);
        try {
            serverEngine.removeTimeSlotFromSystem(timeSlot);
            serverEngine.saveState();
            try (PrintWriter out = resp.getWriter()) {
                out.print("activity deleted successfully");
            }
        } catch (JAXBException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }
}
