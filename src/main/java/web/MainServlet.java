package web;


import web.command.RootCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "MainServlet",
        urlPatterns = {"/init", "/registration/*", "/reception/*", "/login/*",
                "/set_locale", "/administrator/*", "/master/*", "/my_receptions/*", "/review/*"})
public class MainServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Override
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        @SuppressWarnings("unchecked")
        RootCommand rootCommand = (RootCommand) getServletContext().getAttribute("rootCommand");
        rootCommand.execute(request, response);

    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        @SuppressWarnings("unchecked")
        RootCommand rootCommand = (RootCommand) getServletContext().getAttribute("rootCommand");
        rootCommand.execute(request, response);

    }
}