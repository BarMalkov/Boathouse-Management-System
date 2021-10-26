package listeners;

import AlertPackage.Alert;
import EnginePackage.EngineClass;
import utils.ServletUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBException;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import static constants.Constants.SERVER_ENGINE_ATTRIBUTE_NAME;
import static constants.Constants.USER_MANAGER_ATTRIBUTE_NAME;

@WebListener("WebApp Context Listener")
public class WebAppContextListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    ServletContext context = servletContextEvent.getServletContext();
    context.setAttribute(SERVER_ENGINE_ATTRIBUTE_NAME, new EngineClass());
    context.setAttribute(USER_MANAGER_ATTRIBUTE_NAME,
            (ServletUtils.getServerEngine(servletContextEvent.getServletContext())).getRower("admin@user"));
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  }
}
