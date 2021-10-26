package Servlets;

import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.Assigning;
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

@WebServlet(name = "allAssignsServlet", urlPatterns = "/allAssigns")
public class AllAssignsServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());

        String assigns = "";
        try {
            assigns = gson.toJson(serverEngine.getListOfAssigns());
        } catch (IllegalActionException ignored) {}
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.println(assigns);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String assigningToRemove = reader.lines().collect(Collectors.joining());
        Assigning assigning = gson.fromJson(assigningToRemove, Assigning.class);
        System.out.println(assigningToRemove);
        try {
            serverEngine.removeAssigning(assigning);
            serverEngine.saveState();
            try (PrintWriter out = resp.getWriter()) {
                out.print("assign deleted successfully");
            }
        } catch (JAXBException | IllegalActionException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }

}
