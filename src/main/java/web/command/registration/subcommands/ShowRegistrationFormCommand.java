package web.command.registration.subcommands;

import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.registration.RegistrationCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/show_registration_form/*",
        parent = RegistrationCommand.class)
public class ShowRegistrationFormCommand extends RootCommand {

    public ShowRegistrationFormCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forward("/jsp/registration/registration.jsp", request, response);
    }
}
