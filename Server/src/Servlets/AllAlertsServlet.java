package Servlets;

import AlertPackage.Alert;
import BoatPackage.Boat;
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
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;


@WebServlet(name = "allAlertsServlet", urlPatterns = "/allAlerts")
public class AllAlertsServlet extends HttpServlet {

    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        Rower loggedInUser = ServletUtils.getLoggedInUser(getServletContext());
        String userEmail = loggedInUser.getEmail();
        List<Alert> alertList =  serverEngine.getAlertForUser(userEmail);
        String jsonResp = gson.toJson(alertList);
        log(jsonResp);
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.println(jsonResp);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String alertToRemove = reader.lines().collect(Collectors.joining());
        Alert alert = gson.fromJson(alertToRemove, Alert.class);
        System.out.println(alert);
        try {
            serverEngine.removeAlert(alert);
            serverEngine.saveState();
            try (PrintWriter out = resp.getWriter()) {
                out.print("alert deleted successfully");
            }
        } catch (JAXBException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }
}
