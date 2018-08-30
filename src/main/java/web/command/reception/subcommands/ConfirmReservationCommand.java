package web.command.reception.subcommands;

import model.service.reception.ReservationService;
import util.wrappers.WrappedUser;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.reception.ReceptionCommand;
import model.service.ServiceMapper;
import util.dto.reception.ProcessReceptionInDto;
import util.dto.reception.ProcessReceptionOutDto;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebCommand(urlPattern = "/confirm_reservation",
        parent = ReceptionCommand.class)
public class ConfirmReservationCommand extends RootCommand {

    public ConfirmReservationCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ProcessReceptionInDto.ProcessReceptionInDtoBuilder builder = ProcessReceptionInDto.getBuilder();

        computeIfParameterPresent(request, "day", builder::setDay);
        computeIfParameterPresent(request, "time", builder::setTime);
        computeIfParameterPresent(request, "master", builder::setMaster);
        computeIfParameterPresent(request, "filter_service_opt", builder::setService);

//        if (request.getParameterMap().containsKey("day") && !request.getParameter("day").isEmpty()){
//            builder.setDay(request.getParameter("day"));
//        }
//        if (request.getParameterMap().containsKey("time") && !request.getParameter("time").isEmpty()){
//            builder.setTime(request.getParameter("time"));
//        }
//        if (request.getParameterMap().containsKey("master") && !request.getParameter("master").isEmpty()){
//            builder.setMaster(request.getParameter("master"));
//        }
//        if (request.getParameterMap().containsKey("filter_service_opt") && !request.getParameter("filter_service_opt").isEmpty()){
//            builder.setService(request.getParameter("filter_service_opt"));
//        }

        ProcessReceptionOutDto dto = ServiceMapper.getMapper().getService(ReservationService.class).processReservationRequest(builder.build());

        if (dto.isOk()) {
            if (dto.isReserved()){
                forward("/jsp/reception/reservation_failed.jsp", request, response);
            }else if(dto.isReservedAnotherMaster()){
                forward("/jsp/reception/reservation_another_master.jsp", request, response);
            }else {

                boolean done = ServiceMapper.getMapper().getService(ReservationService.class).
                        confirmReservation(dto, WrappedUser.userOf(request.getSession().getAttribute("user")));

                if (done) {
                    forward("/jsp/reception/reservation_done.jsp", request, response);
                }else {
                    forward("/jsp/reception/reservation_failed.jsp", request, response);
                }
            }
        }else {
            logFullError("error in processReservationRequest method", request);
            redirect(Page.DEF, response);
        }

    }

}
