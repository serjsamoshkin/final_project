package web.listners;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.ServiceMapper;
import model.service.authentication.UserService;
import web.pageActivator.ActivePage;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;



@WebListener
public class SessionListener implements HttpSessionListener{
    // TODO убрать
    /*
    Chrome помнит сессию и не дает новую после F5, при этом срабатывает ServletRequestListener.
    Первое обращение после деплойда не ловится, это странно.
    Создается новое соединение при входе с другого браузера или режима инкогнито, дальше все обращения
    к сервлету идут с этого идентификатора сессии.
    Обновление страницы (а так же открытие в новой вкладеке) инициирует ServletRequestListener, но пока не ясно какой
    именно целевой листнер при этом срабатывает.
     */

    private static final Logger logger = LogManager.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

        HttpSession session = httpSessionEvent.getSession();

        try {
            session.setAttribute("pageActivator", new ActivePage(session));
            session.setAttribute("user", ServiceMapper.getMapper().getService(UserService.class).getWrappedDefUser());
        }catch (Exception e){
            logger.error(e);
            session.setAttribute("sessionCriticalError", true);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {}
}
