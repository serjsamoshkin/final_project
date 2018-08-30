package web.command.main;

import util.wrappers.WrappedUser;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/home",
        parent = RootCommand.class)
public class MainPageCommand extends RootCommand {

    public MainPageCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        WrappedUser wrappedUser = WrappedUser.of(request.getSession().getAttribute("user"));

        if (wrappedUser.isAdmin()) {
            forward("/administrator", request, response);
            return;
        } else if (wrappedUser.isMaster()) {
            forward("/master", request, response);
            return;
        }
        forward("/jsp/main.jsp", request, response);
    }



}
