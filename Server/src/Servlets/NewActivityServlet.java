package Servlets;

import BoatPackage.Boat;
import BoatPackage.SimpleBoatType;
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
import java.time.LocalTime;
import java.util.stream.Collectors;


@WebServlet(name = "NewActivityServlet", urlPatterns = "/newActivity")
public class NewActivityServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TimeSlot demoActivity = new TimeSlot("activity", LocalTime.now(), LocalTime.now().plusHours(1),
                SimpleBoatType.Single);
        String activityToServer = gson.toJson(demoActivity);
        resp.setContentType("application/json");
        try(PrintWriter out = resp.getWriter()){
            out.println(activityToServer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String activityStr = reader.lines().collect(Collectors.joining());
        TimeSlot newTimeSlot = gson.fromJson(activityStr, TimeSlot.class);
        try {
            serverEngine.addNewTimeSlot(newTimeSlot.getActivityName(), newTimeSlot.getStartTime(),
                    newTimeSlot.getEndTime(), newTimeSlot.getBoatType());
            try (PrintWriter out = resp.getWriter()) {
                out.print("activity added successfully");
            }
            serverEngine.saveState();
        } catch (IllegalActionException | JAXBException exception) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(exception.getMessage());
            }
        }
    }
}
