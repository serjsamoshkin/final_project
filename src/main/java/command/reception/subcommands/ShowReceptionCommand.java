package command.reception.subcommands;

import chainCommandSystem.annotation.WebCommand;
import command.RootCommand;
import command.reception.ReceptionCommand;
import entity.model.Service;
import service.ServiceMapper;
import service.dto.reception.ShowReceptionInDto;
import service.dto.reception.ShowReceptionOutDto;
import service.reception.ReceptionService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@WebCommand(urlPattern = "/show_receptions",
        parent = ReceptionCommand.class)
public class ShowReceptionCommand extends RootCommand {

    public ShowReceptionCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ShowReceptionInDto.ShowReceptionInDtoBuilder builder = ShowReceptionInDto.getBuilder();

        // TODO можно вынести в проверку и получение параметра в рутовый класс
        if (request.getParameterMap().containsKey("day") && !request.getParameter("day").isEmpty()){
            builder.setDay(request.getParameter("day"));
        }
        if (request.getParameterMap().containsKey("filter_service_opt") && !request.getParameter("filter_service_opt").isEmpty()){
            builder.setService(request.getParameter("filter_service_opt"));
        }

        ShowReceptionOutDto dto = ServiceMapper.getMapper().getService(ReceptionService.class).processShowReceptionRequest(builder.build());

        if (dto.isOk()) {
            request.setAttribute("masters_schedule", dto.getMastersSchedule());
            request.setAttribute("service_map", dto.getServiceMap());
            request.setAttribute("reservation_day", dto.getReservationDay());
            request.setAttribute("reservation_day_txt", dto.getReservationDayTxt());
            request.setAttribute("next_day", dto.getNextDay());
            request.setAttribute("previous_day", dto.getPreviousDay());

            forward("/jsp/reception/receptions.jsp", request, response);
        }else {
            forward(Page.PAGE_500, request, response);
        }
    }
}
