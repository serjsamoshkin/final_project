package web.command.admin.subcommands;

import model.entity.reception.Review;
import model.service.ServiceMapper;
import model.service.reception.ShowAdminReceptionsService;
import model.service.review.ReviewService;
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
import java.util.Optional;

@WebCommand(urlPattern = "/show_review",
        parent = AdminCommand.class)
public class ShowReviewCommand extends RootCommand {

    private static final Logger logger = LogManager.getLogger(ShowReviewCommand.class);

    public ShowReviewCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String str_id = request.getParameterMap().getOrDefault("review_id", new String[]{""})[0];
        int id = 0;
        if (str_id.isEmpty()) {
            logFullError("No parameter 'review_id' in show_receptions command executor", request);
            redirect(Page.DEF, response);
            return;
        } else {
            try {
                id = Integer.valueOf(str_id);
            }catch (NumberFormatException e){
                logFullError("incorrect number in 'review_id' parameter in show_receptions command executor", request, e);
                return;
            }
        }
        Optional<Review> reviewOptional = ServiceMapper.getMapper().getService(ReviewService.class).getReviewById(id);
        if (reviewOptional.isPresent()){
            request.setAttribute("reception", reviewOptional.get().getReception());
            request.setAttribute("review", reviewOptional.get());
            forward("/jsp/admin/review.jsp", request, response);
        }else {
            logFullError("No review with id " + id, request);
            redirect(Page.DEF, response);
            return;
        }
    }
}
