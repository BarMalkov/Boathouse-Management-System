package Servlets;

import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.Reservation;
import ReservationPackage.ReservationFilter;
import ReservationPackage.TimeSlot;
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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "editActivityServlet", urlPatterns = "/editActivity")
public class EditActivityServlet extends HttpServlet {
    TimeSlot timeSlotBeforeEdit;
    TimeSlot timeSlotAfterEdit;
    Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String timeSlotsArray = reader.lines().collect(Collectors.joining());
        TimeSlot[] timeSlots = gson.fromJson(timeSlotsArray, TimeSlot[].class);
        timeSlotBeforeEdit = timeSlots[0];
        timeSlotAfterEdit = timeSlots[1];
        try {
            if (checkValidTime(timeSlotAfterEdit)) {
                serverEngine.addNewTimeSlot(timeSlotAfterEdit.getActivityName(),
                        timeSlotAfterEdit.getStartTime(), timeSlotAfterEdit.getEndTime(),
                        timeSlotAfterEdit.getBoatType());
                serverEngine.removeTimeSlotFromSystem(timeSlotBeforeEdit);
                serverEngine.saveState();
                try (PrintWriter out = resp.getWriter()) {
                    out.print("Activity Edit Successfully");
                }
            }else {
                try (PrintWriter out = resp.getWriter()) {
                    out.print("start time must be before end time");
                }
            }
        } catch (IllegalActionException | JAXBException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }

    private boolean checkValidTime(TimeSlot timeSlotAfterEdit) {
        return timeSlotAfterEdit.getStartTime().isBefore(timeSlotAfterEdit.getEndTime());
    }


}
