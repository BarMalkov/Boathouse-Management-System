package Servlets;

import AlertPackage.Alert;
import EnginePackage.EngineClass;
import RowerPackage.Rower;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


@WebServlet(name = "removeAlertsServlet", urlPatterns = "/removeAlerts")
public class RemoveAlertsServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        Rower loggedInUser = ServletUtils.getLoggedInUser(getServletContext());
        List<Alert> alertList = serverEngine.getAlertForUser(loggedInUser.getEmail());
        for (Iterator<Alert> iterator = alertList.iterator(); iterator.hasNext();) {
            Alert alert = iterator.next();
            if(!alert.isManualAlert()) {
                serverEngine.removeAlert(alert);
            }
        }
        try {
            serverEngine.saveState();
        } catch (JAXBException ignored) {}
    }
}
