package web.command.master.subcommands;

import model.entity.authentication.User;
import model.service.ServiceMapper;
import model.service.reception.MasterReceptionService;
import model.service.reception.ShowReceptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.dto.reception.ShowMasterSchedule.ShowMasterScheduleInDto;
import util.dto.reception.ShowMasterSchedule.ShowMasterScheduleOutDto;
import util.wrappers.WrappedUser;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.admin.AdminCommand;
import web.command.master.MasterCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebCommand(urlPattern = "/show_schedule",
        parent = MasterCommand.class)
public class ShowSchedule extends RootCommand {

    private static final Logger logger = LogManager.getLogger(ShowSchedule.class);

    public ShowSchedule(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        MasterReceptionService service = ServiceMapper.getMapper().getService(MasterReceptionService.class);

        ShowMasterScheduleInDto.ShowMasterScheduleInDtoBuilder builder = ShowMasterScheduleInDto.getBuilder();
        builder.setDate(LocalDate.now());
        builder.setUser(WrappedUser.userOf(request.getSession().getAttribute("user")));

        ShowMasterScheduleOutDto dto = service.getDailyMasterSchedule(builder.build());

        if (dto.isOk()) {

            request.setAttribute("master_schedule", dto.getSchedule());

            forward("/jsp/master/daily_receptions.jsp", request, response);

        } else {
            forward(Page.DEF, request, response);
        }


    }
}
