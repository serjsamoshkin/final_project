package web.command.myReceptions.subcommands;

import model.service.ServiceMapper;
import model.service.reception.ShowUserReceptionsService;
import util.dto.reception.ShowUserReceptionsInDto;
import util.dto.reception.ShowUserReceptionsOutDto;
import util.wrappers.WrappedUser;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.myReceptions.MyReceptions;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/show_my_receptions",
        parent = MyReceptions.class)
public class ShowMyReceptions extends RootCommand {

    public ShowMyReceptions(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ShowUserReceptionsInDto.ShowUserReceptionsInDtoBuilder builder = ShowUserReceptionsInDto.getBuilder();


        builder.setUser(WrappedUser.userOf(request.getSession().getAttribute("user")));

        String page = request.getParameter("page");
        if (page != null && !page.equals("")) {
            builder.setPage(page);
        }

        ShowUserReceptionsOutDto dto =  ServiceMapper.getMapper().getService(ShowUserReceptionsService.class).processShowUserReceptionRequest(builder.build());

        if (dto.isOk()) {

            request.setAttribute("reception_list", dto.getReceptions());
            request.setAttribute("page", dto.getPage());
            request.setAttribute("page_count", dto.getPageCount());

            forward("/jsp/myReceptions/my_receptions.jsp", request, response);
        }else {
            forward(Page.DEF, request, response);
        }

    }
}
