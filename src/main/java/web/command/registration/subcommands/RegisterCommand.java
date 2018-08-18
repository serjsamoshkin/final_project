package command.registration.subcommands;

import web.chainCommandSystem.annotation.WebCommand;
import command.RootCommand;
import command.registration.RegistrationCommand;
import model.entity.authentication.User;
import persistenceSystem.RowNotUniqueException;
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

        UserService userService = ServiceMapper.getMapper().getService(UserService.class);

        User user;
        try{
            user = userService.createUser(name, email, password);
        }catch (RowNotUniqueException e){

            // TODO Перенаправить на ту же страницу
            /* response.sendRedirect(req.getHeader("referer")) - дает весь путь, (включая название приложения от локалхост)
            Если будет механизм определения относительного пути команды, то можно будет брать только имя jsp страницы и
            редиректить на нее.
             */

            request.setAttribute("user_name_r", name);
            request.setAttribute("user_email_r", email);
            request.setAttribute("incorrect_email", true);

            forward("/registration/show_registration_form", request, response);

            return;
        }

        session.setAttribute("user", userService.getWrappedUser(user));

        forward(Page.DEF, request, response);

    }
}
