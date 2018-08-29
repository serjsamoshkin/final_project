package web.command.admin.subcommands;

import model.service.ServiceMapper;
import model.service.reception.ShowAdminReceptionsService;
import model.service.reception.ShowUserReceptionsService;
import model.service.util.DataInitializerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.dto.reception.ShowAdminReceptionsInDto;
import util.dto.reception.ShowAdminReceptionsOutDto;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.admin.AdminCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/show_receptions",
        parent = AdminCommand.class)
public class ShowReceptionCommand extends RootCommand {

    private static final Logger logger = LogManager.getLogger(ShowReceptionCommand.class);

    public ShowReceptionCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ShowAdminReceptionsInDto.ShowAdminReceptionsInDtoBuilder builder = ShowAdminReceptionsInDto.getBuilder();

        computeIfParameterPresent(request, "page", builder::setPage);

        ShowAdminReceptionsOutDto dto =  ServiceMapper.getMapper().getService(ShowAdminReceptionsService.class).processShowReceptionRequest(builder.build());

        if (dto.isOk()) {

            request.setAttribute("reception_list", dto.getReceptions());
            request.setAttribute("page", dto.getPage());
            request.setAttribute("page_count", dto.getPageCount());

            forward("/jsp/admin/receptions.jsp", request, response);
        }else {
            logFullError("error in processShowUserReceptionRequest method", request);
            redirect(Page.DEF, response);
        }

    }
}
