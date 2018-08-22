package web.command.login.subcommands;

import util.wrappers.WrappedUser;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.login.LogInCommand;
import model.entity.authentication.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.ServiceMapper;
import model.service.authentication.UserService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@WebCommand(urlPattern = "/confirm",
        parent = LogInCommand.class)
public class ConfirmLogInCommand extends RootCommand {

    private static final Logger logger = LogManager.getLogger(ConfirmLogInCommand.class);

    public ConfirmLogInCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");

        if (email == null){
            response.sendRedirect("./");
            return;
        }

        String password = request.getParameter("password");
        UserService service = ServiceMapper.getMapper().getService(UserService.class);

        Optional<User> user = service.getAuthenticatedUser(email, password);

        if (user.isPresent()){
            WrappedUser wrappedUser = WrappedUser.of(user.get());
            request.getSession().setAttribute("user", wrappedUser);
            forward(Page.DEF, request, response);
        }else {
            // TODO поулчить страничку из хидера
            request.setAttribute("user_not_found", true);
            request.setAttribute("email_r", email);
            forward("/jsp/login/login.jsp", request, response);
        }
    }
}
