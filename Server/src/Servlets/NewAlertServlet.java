package Servlets;

import AlertPackage.Alert;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "newAlertServlet", urlPatterns = "/newAlert")
public class NewAlertServlet extends HttpServlet {


    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> demoList = new ArrayList<>();
        demoList.add("demoUser");
        Alert newAlert = new Alert("empty Message", demoList);
        String jsonResp = gson.toJson(newAlert);
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.println(jsonResp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String alertStr = reader.lines().collect(Collectors.joining());
        System.out.println(alertStr);
        Alert newAlert = gson.fromJson(alertStr, Alert.class);
        try {
            serverEngine.addAlert(newAlert.getMessage());
            try (PrintWriter out = resp.getWriter()) {
                out.print("alert added successfully");
            }
            serverEngine.saveState();
        } catch (JAXBException exception) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(exception.getMessage());
            }
        }
    }

}