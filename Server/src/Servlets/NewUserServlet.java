package Servlets;

import BoatPackage.SimpleBoatType;
import EnginePackage.EngineClass;
import Errors.IllegalActionException;
import ReservationPackage.TimeSlot;
import RowerPackage.Level;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

@WebServlet(name = "NewUserServlet", urlPatterns = "/newUser")
public class NewUserServlet extends HttpServlet {
    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Rower demoUser = new Rower("1", "1", "1", "1", "1", 1, Level.BEGINNER,
                LocalDate.now().minusYears(1), LocalDate.now().plusYears(1), false, "-",
                false, "comment");
        String userToServer = gson.toJson(demoUser);
        resp.setContentType("application/json");
        try(PrintWriter out = resp.getWriter()){
            out.println(userToServer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String userStr = reader.lines().collect(Collectors.joining());
        Rower newUser = gson.fromJson(userStr, Rower.class);
        try {
            serverEngine.addNewRower(newUser.getID(), newUser.getUserName(), newUser.getEmail(),
                    newUser.getPassword(), newUser.getPhoneNumber(), newUser.getAge(), newUser.isManager(),
                    newUser.getComment(), newUser.getLevel(), newUser.getDateOfRegistration(),
                    newUser.getDateOfExpiration(), newUser.hasPrivateBoat(), newUser.getPrivateBoatID());
            try (PrintWriter out = resp.getWriter()) {
                out.print("user added successfully");
            }
            serverEngine.saveState();
        } catch (IllegalActionException | JAXBException exception) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(exception.getMessage());
            }
        }
    }

}
