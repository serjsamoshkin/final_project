package command.login.subcommands;

import chainCommandSystem.annotation.WebCommand;
import command.RootCommand;
import command.login.LogInCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.ServiceMapper;
import service.authentication.UserService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebCommand(urlPattern = "/logout",
        parent = LogInCommand.class)
public class LogOut extends RootCommand {

    private static final Logger logger = LogManager.getLogger(LogOut.class);

    public LogOut(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        session.setAttribute("user", ServiceMapper.getMapper().getService(UserService.class).getWrappedDefUser());

        forward(Page.DEF, request, response);

    }
}
