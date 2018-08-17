package command.reception.subcommands;

import chainCommandSystem.annotation.WebCommand;
import command.RootCommand;
import command.reception.ReceptionCommand;
import entity.authentication.User;
import entity.model.Reception;
import service.ServiceMapper;
import service.dto.reception.ProcessReservation.ProcessReceptionInDto;
import service.dto.reception.ProcessReservation.ProcessReceptionOutDto;
import service.reception.ReceptionService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@WebCommand(urlPattern = "/confirm_reservation",
        parent = ReceptionCommand.class)
public class ConfirmReservationCommand extends RootCommand {

    public ConfirmReservationCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ProcessReceptionInDto.ProcessReceptionInDtoBuilder builder = ProcessReceptionInDto.getBuilder();

        if (request.getParameterMap().containsKey("day") && !request.getParameter("day").isEmpty()){
            builder.setDay(request.getParameter("day"));
        }
        if (request.getParameterMap().containsKey("time") && !request.getParameter("time").isEmpty()){
            builder.setTime(request.getParameter("time"));
        }
        if (request.getParameterMap().containsKey("master") && !request.getParameter("master").isEmpty()){
            builder.setMaster(request.getParameter("master"));
        }
        if (request.getParameterMap().containsKey("filter_service_opt") && !request.getParameter("filter_service_opt").isEmpty()){
            builder.setService(request.getParameter("filter_service_opt"));
        }

        ProcessReceptionOutDto dto = ServiceMapper.getMapper().getService(ReceptionService.class).processReservationRequest(builder.build());

        if (dto.isOk()) {
            if (dto.isReserved()){
                // TODO кинуть на стричку с извинениями
                forward(Page.PAGE_500, request, response);
            }else {

                ServiceMapper.getMapper().getService(ReceptionService.class).confirmReservation(dto,
                        // TODO заменить на ДТО или другой враппер
                        (User) ((Map<String, Object>)request.getSession().getAttribute("user")).get("obj"));

                forward("/jsp/reception/reservation.jsp", request, response);
            }
        }else {
            forward(Page.PAGE_500, request, response);
        }

    }

}
