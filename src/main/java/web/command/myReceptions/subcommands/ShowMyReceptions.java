package web.command.myReceptions.subcommands;

import model.service.ServiceMapper;
import model.service.reception.ShowUserReceptionsService;
import util.wrappers.ReceptionView;
import util.wrappers.WrappedUser;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.myReceptions.MyReceptions;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebCommand(urlPattern = "/show_my_receptions",
        parent = MyReceptions.class)
public class ShowMyReceptions extends RootCommand {

    public ShowMyReceptions(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        List<ReceptionView> receptions =  ServiceMapper.getMapper().getService(ShowUserReceptionsService.class).processShowUserReceptionRequest(WrappedUser.userOf(request.getSession().getAttribute("user")));

        request.setAttribute("reception_list", receptions);

        forward("/jsp/myReceptions/my_receptions.jsp", request, response);

    }
}
