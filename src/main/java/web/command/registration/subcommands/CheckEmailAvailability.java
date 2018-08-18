package command.registration.subcommands;

import web.chainCommandSystem.annotation.WebCommand;
import command.RootCommand;
import command.registration.RegistrationCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.ServiceMapper;
import service.authentication.UserService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebCommand(urlPattern = "/check-email",
        parent = RegistrationCommand.class)
public class CheckEmailAvailability extends RootCommand {

    private static final Logger logger = LogManager.getLogger(CheckEmailAvailability.class);

    public CheckEmailAvailability(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        ServiceMapper.getMapper().getService(UserService.class)
                .getUserByEmail(request.getParameter("email"))
                .ifPresent((s) -> writer.write("taken"));
    }
}
