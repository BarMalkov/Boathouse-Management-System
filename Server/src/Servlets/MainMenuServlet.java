package Servlets;

import RowerPackage.Rower;
import utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "mainMenu", urlPatterns = "/mainMenu")
public class MainMenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Rower loggedInUser = ServletUtils.getLoggedInUser(getServletContext());
        try (PrintWriter out = resp.getWriter()) {
            out.print(loggedInUser.isManager());
        }
    }
}
