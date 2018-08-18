package command.admin.subcommands;

import web.chainCommandSystem.annotation.WebCommand;
import command.RootCommand;
import command.admin.AdminCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.ServiceMapper;
import service.initializer.DataInitializerService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/init",
        parent = AdminCommand.class)
public class InitData extends RootCommand {

    private static final Logger logger = LogManager.getLogger(InitData.class);

    public InitData(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DataInitializerService initializer =  ServiceMapper.getMapper().getService(DataInitializerService.class);

        initializer.initIfNeed();

        forward(Page.DEF, request, response);

    }
}
