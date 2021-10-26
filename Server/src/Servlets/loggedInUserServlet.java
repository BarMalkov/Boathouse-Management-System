package Servlets;

import EnginePackage.EngineClass;
import RowerPackage.Rower;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import static constants.Constants.LOGGED_IN_USER;


@WebServlet(name = "loggedInUserServlet", urlPatterns = "/loggedInUser")
public class loggedInUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String loggedInUser = reader.lines().collect(Collectors.joining());
        System.out.println(loggedInUser);
        getServletContext().setAttribute(LOGGED_IN_USER, serverEngine.getRower(loggedInUser));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Rower loggedInUser = ServletUtils.getLoggedInUser(getServletContext());
        try (PrintWriter out = resp.getWriter()) {
            out.print(loggedInUser.getEmail());
        }
    }
}
