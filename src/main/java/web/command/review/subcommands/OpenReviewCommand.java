package web.command.review.subcommands;

import model.service.ServiceMapper;
import model.service.review.ReviewService;
import util.dto.review.OpenReviewOutDto;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.registration.RegistrationCommand;
import web.command.review.ReviewCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/open_review/*",
        parent = ReviewCommand.class)
public class OpenReviewCommand extends RootCommand {

    public OpenReviewCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null){
            redirect(Page.DEF, response);
        }

        OpenReviewOutDto dto = ServiceMapper.getMapper().getService(ReviewService.class).getReviewByToken(token);

        if (dto.isOk()){
            request.setAttribute("reception", dto.getReceptionView());
            request.setAttribute("review_id", dto.getReviewId());
            forward("/jsp/review/review.jsp", request, response);
        }else {
            logFullError("error in getReviewByToken method", request);
            redirect(Page.DEF, response);
        }


    }
}
