package web.command.myReceptions;

import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/my_receptions/*",
        parent = RootCommand.class)
public class MyReceptions extends RootCommand {

    public MyReceptions(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forward("/my_receptions/show_my_receptions", request, response);
    }
}
