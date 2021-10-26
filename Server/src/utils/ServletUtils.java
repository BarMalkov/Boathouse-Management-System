package utils;

import EnginePackage.EngineClass;
import RowerPackage.Rower;
import constants.Constants;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static constants.Constants.*;

public class ServletUtils {
	public static EngineClass getServerEngine(ServletContext servletContext) {
		return (EngineClass) servletContext.getAttribute(Constants.SERVER_ENGINE_ATTRIBUTE_NAME);
	}

	public static Rower getLoggedInUser(ServletContext servletContext) {
		return (Rower) servletContext.getAttribute(LOGGED_IN_USER);
	}


	public static int getIntParameter(HttpServletRequest request, String parameterName) {
		String value = request.getParameter(parameterName);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
				System.out.println("Error parsing parameter " + parameterName + ". Expected a number but value was " + value);
			}
		}
		return INT_PARAMETER_ERROR;
	}

    public static Rower getAdminUser(ServletContext servletContext) {
		return (Rower)servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }
}
