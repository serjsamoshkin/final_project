package web.command.review.subcommands;

import model.service.ServiceMapper;
import model.service.review.ReviewService;
import web.chainCommandSystem.annotation.WebCommand;
import web.command.RootCommand;
import web.command.review.ReviewCommand;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebCommand(urlPattern = "/send_review/*",
        parent = ReviewCommand.class)
public class SendReviewCommand extends RootCommand {

    public SendReviewCommand(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id;
        String review_id = request.getParameter("review_id");
        if (review_id == null || review_id.isEmpty()){
            redirect(Page.DEF, response);
            return;
        }else {
            try {
                id = Integer.valueOf(review_id);
            }catch (NumberFormatException e){
                // TODO залогировать  все что-можно
                return;
            }
        }

        String text = request.getParameter("comment");
        if (text == null){
            // TODO залогировать  все что-можно
            return;
        }

        boolean isOk =  ServiceMapper.getMapper().getService(ReviewService.class).setReviewCommentById(id, text);

        if (isOk){
            // TODO на страничку благодарности
//            request.setAttribute("reception", dto.getReceptionView());
//            forward("/jsp/review/review.jsp", request, response);
        }else {
            // TODO кинуть на страницу "отзыв уже оставлен".
        }


    }
}
