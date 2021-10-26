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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "NewBoatServlet", urlPatterns = "/newBoat")
public class NewBoatServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Boat demoBoat = new Boat("1", "1", false, false,
                SimpleBoatType.Single, false, false);
        String boatToServer = gson.toJson(demoBoat);
        resp.setContentType("application/json");
        try(PrintWriter out = resp.getWriter()){
            out.println(boatToServer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String boatStr = reader.lines().collect(Collectors.joining());
        Boat newBoat = gson.fromJson(boatStr, Boat.class);
        try {
            serverEngine.addNewBoat(newBoat.getSerialNumber(), newBoat.getBoatName(), newBoat.getBoatType(),
                    newBoat.isPrivateBoat(), newBoat.isWide(), newBoat.isCostal(), newBoat.isInRepair());
            try (PrintWriter out = resp.getWriter()) {
                out.print("boat added successfully");
            }
            serverEngine.saveState();
        } catch (IllegalActionException | JAXBException exception) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(exception.getMessage());
            }
        }
    }
}
