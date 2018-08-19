package web.command.reception;

import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/reception/*",
        parent = RootCommand.class)
public class ReceptionCommand extends RootCommand {

    public ReceptionCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forward(Page.PAGE_404, request, response);
    }

}
