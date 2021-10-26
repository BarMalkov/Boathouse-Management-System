package Servlets;


import EnginePackage.EngineClass;
import Errors.IllegalActionException;
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

@WebServlet(name = "importExportBoatsServlet", urlPatterns = "/importExportBoats")
public class ImportExportBoatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        String boatsData = "";
        try {
            boatsData = serverEngine.exportBoats();
            System.out.println(boatsData);
        } catch (JAXBException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.println(e.getMessage());
            }
        }
        resp.setContentType("text/xml");
        try (PrintWriter out = resp.getWriter()) {
            out.println(boatsData);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineClass serverEngine = ServletUtils.getServerEngine(getServletContext());
        BufferedReader reader = req.getReader();
        String boatsData = reader.lines().collect(Collectors.joining());
        System.out.println(boatsData);
        try {
            serverEngine.importBoats(boatsData);
            serverEngine.saveState();
            try (PrintWriter out = resp.getWriter()) {
                out.print("the boats were added to the system successfully");
            }
        } catch (JAXBException | IllegalActionException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
        try {
            serverEngine.saveState();
        } catch (JAXBException e) {
            try (PrintWriter out = resp.getWriter()) {
                out.print(e.getMessage());
            }
        }
    }


}
