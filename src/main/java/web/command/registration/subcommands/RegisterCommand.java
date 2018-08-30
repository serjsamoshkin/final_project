package web.command.registration.subcommands;

import model.service.authentication.RoleService;
import util.wrappers.WrappedUser;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.registration.RegistrationCommand;
import model.entity.authentication.User;
import persistenceSystem.RowNotUniqueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.ServiceMapper;
import model.service.authentication.UserService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebCommand(urlPattern = "/register",
        parent = RegistrationCommand.class)
public class RegisterCommand extends RootCommand {

    private static final Logger logger = LogManager.getLogger(RegisterCommand.class);

    public RegisterCommand(ServletContext servletContext) {
        super(servletContext);
    }


    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        String name = request.getParameter("user-name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserService service = ServiceMapper.getMapper().getService(UserService.class);

        User user;
        try{
            service.createUser(name, email, password,
                            ServiceMapper.getMapper().getService(RoleService.class).getRoleUser());
            user = service.getUserByEmail(email).get();
        }catch (RowNotUniqueException e){

            request.setAttribute("user_name_r", name);
            request.setAttribute("user_email_r", email);
            request.setAttribute("incorrect_email", true);

            forward("/registration/show_registration_form", request, response);

            return;
        }

        session.setAttribute("user", WrappedUser.of(user));

        redirect(Page.DEF, response);

    }
}
