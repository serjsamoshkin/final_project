package web.command.master;

import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/master/*",
        parent = RootCommand.class)
public class MasterCommand extends RootCommand {

    public MasterCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forward("/master/show_schedule", request, response);
    }

}
