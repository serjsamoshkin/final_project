package command.locale;

import chainCommandSystem.annotation.WebCommand;
import command.RootCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/set_locale/*",
        parent = RootCommand.class)
public class SetLocale extends RootCommand {

    public SetLocale(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loc = request.getParameter("loc");
        switch (loc) {
            case "en_En":
            case "ru_Ru":
                break;
            default:
                loc = "ru_Ru";
                break;
        }
        getServletContext().setAttribute("language", loc);
        response.sendRedirect(request.getHeader("referer"));
//        forward(request.getHeader("referer"), request, response);
    }
}
