package web.command.reception.subcommands;

import util.wrappers.WrappedUser;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.reception.ReceptionCommand;
import model.entity.authentication.User;
import model.service.ServiceMapper;
import util.dto.reception.ProcessReservation.ProcessReceptionInDto;
import util.dto.reception.ProcessReservation.ProcessReceptionOutDto;
import model.service.reception.ReceptionService;

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
                forward("/jsp/reception/reservation_failed.jsp", request, response);
            }else {

                boolean done = ServiceMapper.getMapper().getService(ReceptionService.class).confirmReservation(dto,
                        WrappedUser.userOf(request.getSession().getAttribute("user")));

                if (done) {
                    forward("/jsp/reception/reservation_done.jsp", request, response);
                }else {
                    forward("/jsp/reception/reservation_failed.jsp", request, response);
                }
            }
        }else {
            forward(Page.PAGE_500, request, response);
        }

    }

}
