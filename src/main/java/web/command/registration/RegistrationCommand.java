package command.registration;

import web.chainCommandSystem.annotation.WebCommand;
import command.RootCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/registration/*",
        parent = RootCommand.class)
public class RegistrationCommand extends RootCommand {

    public RegistrationCommand(ServletContext servletContext) {
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
