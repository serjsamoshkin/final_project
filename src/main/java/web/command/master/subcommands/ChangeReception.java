package web.command.master.subcommands;

import model.service.ServiceMapper;
import model.service.reception.MasterReceptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.dto.reception.ChangeReceptionInDto;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.master.MasterCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/change_reception",
        parent = MasterCommand.class)
public class ChangeReception extends RootCommand {

    private static final Logger logger = LogManager.getLogger(ChangeReception.class);

    public ChangeReception(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ChangeReceptionInDto.ChangeReceptionInDtoBuilder builder = ChangeReceptionInDto.getBuilder();

        computeIfParameterPresent(request, "id", builder::setId);
        computeIfParameterPresent(request, "status", builder::setStatus);
        computeIfParameterPresent(request, "version", builder::setVersion);

//        if (request.getParameterMap().containsKey("id") && !request.getParameter("id").isEmpty()){
//            builder.setId(request.getParameter("id"));
//        }
//
//        if (request.getParameterMap().containsKey("status") && !request.getParameter("status").isEmpty()){
//            builder.setStatus(request.getParameter("status"));
//        }
//
//        if (request.getParameterMap().containsKey("version") && !request.getParameter("version").isEmpty()){
//            builder.setVersion(request.getParameter("version"));
//        }

        boolean isOk = ServiceMapper.getMapper().getService(MasterReceptionService.class).changeReception(builder.build());

        if (!isOk) {
            logFullError("error in changeReception method", request);
            redirect(Page.DEF, response);
        }else {
            redirect("/master", response);
        }



    }
}
