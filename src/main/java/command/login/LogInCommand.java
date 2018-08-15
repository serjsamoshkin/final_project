package command.login;

import chainCommandSystem.annotation.WebCommand;
import command.RootCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/login/*",
        parent = RootCommand.class)
public class LogInCommand extends RootCommand {

    public LogInCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forward("/jsp/login/login.jsp", request, response);
    }

}
