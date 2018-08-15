package command.registration.subcommands;

import chainCommandSystem.annotation.WebCommand;
import command.RootCommand;
import command.registration.RegistrationCommand;

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
        // TODO Переделать на объект, обязательно нужно делать лидирующий слеш, без него будет зацикливание на сервелете
        String url = "/jsp/registration/registration.jsp";
        // TODO переделать на объект редирект
        request.getRequestDispatcher(url).forward(request, response);
    }
}
