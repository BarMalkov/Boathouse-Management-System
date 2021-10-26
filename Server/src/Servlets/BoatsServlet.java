package Servlets;

import BoatPackage.Boat;
import BoatPackage.SimpleBoatType;
import EnginePackage.EngineClass;
import Errors.IllegalActionException;
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

@WebServlet(name = "boatsServlet", urlPatterns = "/boat")
public class BoatsServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        String boats = "";
        try {
            boats = gson.toJson(serverEngine.getAllBoats());
        } catch (IllegalActionException ignored) {}
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.println(boats);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String boatToRemove = reader.lines().collect(Collectors.joining());
        Boat boat = gson.fromJson(boatToRemove, Boat.class);
        System.out.println(boat);
        try {
            serverEngine.removeBoatFromSystem(boat);
            serverEngine.saveState();
            try (PrintWriter out = resp.getWriter()) {
                out.print("boat deleted successfully");
            }
        } catch (JAXBException | IllegalActionException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }
}
