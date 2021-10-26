package Servlets;

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


@WebServlet(name = "updatePersonalInfoServlet", urlPatterns = "/updatePersonalInfo")
public class updatePersonalInfoServlet extends HttpServlet {
    Rower loggedInUser;
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        loggedInUser = ServletUtils.getLoggedInUser(getServletContext());
        String loggedInUserStr = gson.toJson(loggedInUser);
        resp.setContentType("application/json");
        try(PrintWriter out = resp.getWriter()){
            out.println(loggedInUserStr);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String editUser = reader.lines().collect(Collectors.joining());
        System.out.println(editUser);
        Rower editedRower = gson.fromJson(editUser, Rower.class);
        if(loggedInUser.getID().equals("1")){
            try (PrintWriter out = resp.getWriter()) {
                out.print("can't edit user admin data");
            }
            return;
        }
        try {
            serverEngine.editUserEmail(loggedInUser, editedRower.getEmail());
            serverEngine.editUserName(loggedInUser, editedRower.getUserName());
            serverEngine.editUserPassword(loggedInUser, editedRower.getPassword());
            serverEngine.editUserPhoneNumber(loggedInUser, editedRower.getPhoneNumber());
            serverEngine.saveState();
            try (PrintWriter out = resp.getWriter()) {
                out.print("data edited successfully");
            }
        } catch (JAXBException | IllegalActionException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }
}
