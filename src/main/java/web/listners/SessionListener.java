package web.listners;


import model.entity.authentication.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.ServiceMapper;
import model.service.authentication.UserService;
import util.wrappers.WrappedUser;
import web.pageActivator.ActivePage;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;



@WebListener
public class SessionListener implements HttpSessionListener{

    private static final Logger logger = LogManager.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

        HttpSession session = httpSessionEvent.getSession();

        try {
            session.setAttribute("pageActivator", new ActivePage(session));
            session.setAttribute("user", WrappedUser.of());//ServiceMapper.getMapper().getService(UserService.class).getWrappedDefUser());
        }catch (Exception e){
            logger.error(e);
            session.setAttribute("sessionCriticalError", true);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {}
}
