
package Servlets;

import EnginePackage.EngineClass;
import constants.Constants;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.PASSWORD;
import static constants.Constants.USERNAME;


@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private final String MAIN_MENU = "menu.html";
    private final String SIGN_IN_URL = "login.html";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getSession().invalidate();
        resp.sendRedirect(SIGN_IN_URL);
        return;

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());

        if (usernameFromSession != null) {
            response.sendRedirect(MAIN_MENU);
            return;
        }

        String usernameFromParameter = request.getParameter(USERNAME);
        String passwordFromParameter = request.getParameter(PASSWORD);
        if (usernameFromParameter == null || usernameFromParameter.trim().isEmpty()) {
            response.sendRedirect(SIGN_IN_URL);
        }
        else {
            usernameFromParameter = usernameFromParameter.trim();

            if (serverEngine.isUserExist(usernameFromParameter)) {
                if(serverEngine.isPasswordMatchEmail(usernameFromParameter, passwordFromParameter)){
                    request.getSession(true).setAttribute(USERNAME, usernameFromParameter);
                    getServletContext().setAttribute(Constants.LOGGED_IN_USER, serverEngine.getRower(usernameFromParameter));
                    response.sendRedirect(MAIN_MENU);
                }
                else{
                    response.sendRedirect(SIGN_IN_URL);
                }
            }
            else {
                response.sendRedirect(SIGN_IN_URL);
            }
        }
    }
}

