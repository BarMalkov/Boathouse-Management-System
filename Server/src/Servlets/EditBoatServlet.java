package Servlets;

import BoatPackage.Boat;
import EnginePackage.EngineClass;
import Errors.IllegalActionException;
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

@WebServlet(name = "editBoatServlet", urlPatterns = "/editBoat")
public class EditBoatServlet extends HttpServlet {

    Gson gson = new Gson();
    Boat boatBeforeEdit;
    Boat boatAfterEdit;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String boatsArray = reader.lines().collect(Collectors.joining());
        Boat[] boats = gson.fromJson(boatsArray, Boat[].class);
        boatBeforeEdit = boats[0];
        boatAfterEdit = boats[1];
        try {
            serverEngine.removeBoatFromSystem(boatBeforeEdit);
            serverEngine.addNewBoat(boatAfterEdit.getSerialNumber(), boatAfterEdit.getBoatName(),
                boatAfterEdit.getBoatType(), boatAfterEdit.isPrivateBoat(),
                boatAfterEdit.isWide(), boatAfterEdit.isCostal(), boatAfterEdit.isInRepair());
            try (PrintWriter out = resp.getWriter()) {
                out.print("Boat Edit Successfully");
            }
        } catch (IllegalActionException exception) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(exception.getMessage());
            }
            try {
                serverEngine.addNewBoat(boatBeforeEdit.getBoatName(), boatAfterEdit.getBoatName(),
                        boatAfterEdit.getBoatType(), boatAfterEdit.isPrivateBoat(), boatAfterEdit.isWide(),
                        boatAfterEdit.isCostal(), boatAfterEdit.isInRepair());
            } catch (IllegalActionException ignored) {}
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
