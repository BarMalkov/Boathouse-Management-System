package Servlets;

import EnginePackage.EngineClass;
import Errors.IllegalActionException;
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
import java.util.List;
import java.util.stream.Collectors;


@WebServlet(name = "usersServlet", urlPatterns = "/user")
public class UsersServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        List<Rower> allUsers = serverEngine.getAllUsers();
        allUsers.forEach( user->{
            if(!user.hasPrivateBoat()){
                user.setPrivateBoatID("-");
            }
            if(user.getComment().equals("")){
                user.setComment("-");
            }
        });
        String users = gson.toJson(allUsers);
        resp.setContentType("application/json");
        try(PrintWriter out = resp.getWriter()){
            out.println(users);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String userToRemove = reader.lines().collect(Collectors.joining());
        System.out.println(userToRemove);
        Rower rowerToRemove = gson.fromJson(userToRemove, Rower.class);
        System.out.println(rowerToRemove);
        try {
            serverEngine.removeUser(rowerToRemove, ServletUtils.getAdminUser(getServletContext()));
            serverEngine.saveState();
            try (PrintWriter out = resp.getWriter()) {
                out.print("user deleted successfully");
            }
        } catch (JAXBException | IllegalActionException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }
}
