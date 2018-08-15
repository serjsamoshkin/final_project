package command.reception.subcommands;

import chainCommandSystem.annotation.WebCommand;
import command.RootCommand;
import command.reception.ReceptionCommand;
import entity.model.Master;
import service.ServiceMapper;
import service.reception.ReceptionService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@WebCommand(urlPattern = "/process_reservation",
        parent = ReceptionCommand.class)
public class ProcessReservationCommand extends RootCommand {

    public ProcessReservationCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int id = 0;
        Date date;
        if (request.getParameterMap().containsKey("master") && request.getParameterMap().containsKey("time")){
            try {
                id = Integer.valueOf(request.getParameter("master"));
                date = java.sql.Timestamp.valueOf(request.getParameter("time"));
            }catch (Exception e){
                rootLoger.error(e);
                forward(Page.DEF, request, response);
                return;
            }
        }else {
            rootLoger.error("Incorrect parameters in ProcessReservationCommand: " + request.getParameterMap().toString());
            forward(Page.DEF, request, response);
            return;
        }

        Optional<Master> master = ServiceMapper.getMapper().getService(ReceptionService.class).getMaster(id);

        //request.setAttribute("masters_schedule", ServiceMapper.getMapper().getService(ReceptionService.class).getMastersSchedule(LocalDate.now()));

        forward(Page.DEF, request, response);

    }

}
