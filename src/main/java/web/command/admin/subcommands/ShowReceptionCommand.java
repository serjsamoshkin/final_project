package web.command.admin.subcommands;

import model.service.ServiceMapper;
import model.service.reception.ShowAdminReceptionsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.criteria.CriteriaBuilder;
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
import java.util.HashMap;
import java.util.Map;

@WebCommand(urlPattern = "/show_receptions",
        parent = AdminCommand.class)
public class ShowReceptionCommand extends RootCommand {

    private static final Logger logger = LogManager.getLogger(ShowReceptionCommand.class);

    private static final Map<CriteriaBuilder.Order, String> orderMap;
    static {
        orderMap = new HashMap<>();
        orderMap.put(CriteriaBuilder.Order.ASC, "▼");
        orderMap.put(CriteriaBuilder.Order.DESC, "▲");

    }

    public ShowReceptionCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ShowAdminReceptionsInDto.ShowAdminReceptionsInDtoBuilder builder = ShowAdminReceptionsInDto.getBuilder();

        computeIfParameterPresent(request, "page", builder::setPage);
        computeIfParameterPresent(request, "sort_by", builder::setSortBy);
        computeIfParameterPresent(request, "direction", builder::setDirection);

        ShowAdminReceptionsOutDto dto =  ServiceMapper.getMapper().getService(ShowAdminReceptionsService.class).processShowReceptionRequest(builder.build());

        Map<String, String> signMap = new HashMap<>();
        signMap.put("reception_day", "↕");
        signMap.put("reception_time", "↕");

        if (dto.isOk()) {
            request.setAttribute("reception_list", dto.getReceptions());
            request.setAttribute("page", dto.getPage());
            request.setAttribute("page_count", dto.getPageCount());

            if (dto.getSortingField().isPresent() && dto.getOrder().isPresent()) {
                signMap.put(dto.getSortingField().get(), orderMap.get(dto.getOrder().get()));
                request.setAttribute("current_sort_field", dto.getSortingField().get());
                request.setAttribute("current_order", dto.getOrder().get().toString().toLowerCase());
            }

            request.setAttribute("signMap", signMap);

            forward("/jsp/admin/receptions.jsp", request, response);
        }else {
            logFullError("error in processShowUserReceptionRequest method", request);
            redirect(Page.DEF, response);
        }

    }
}
