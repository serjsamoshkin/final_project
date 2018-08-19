package web.command;

import web.chainCommandSystem.Command;
import web.chainCommandSystem.annotation.WebCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.pageActivator.ActivePage;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebCommand(urlPattern = "/jsp",
        parent = Command.class)
public class RootCommand extends Command<String> {

    protected static final Logger rootLoger = LogManager.getLogger(RootCommand.class);

    // TODO попробовать инкапсулировать request и response в полях.
    // Тогда можно как минимум редирект делать без лишних переменных.

    public RootCommand(ServletContext servletContext) {
        super(servletContext);
    }

    /**
     *
     * {@inheritDoc}
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public final void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletContext context = request.getServletContext();
        if (context.getAttribute("criticalError") != null && (boolean) context.getAttribute("criticalError")) {
            forward(Page.PAGE_500, request, response);
            throw new ServletException("criticalError in servlet");
        }

        HttpSession session = request.getSession();
        if (session.getAttribute("sessionCriticalError") != null && (boolean) session.getAttribute("sessionCriticalError")) {
            forward(Page.PAGE_500, request, response);
            throw new ServletException("sessionCriticalError");
        }

        String path = request.getServletPath();

        ActivePage activePage = (ActivePage) request.getSession().getAttribute("pageActivator");
        activePage.setActivePage(path);

        Command command = getCommand(this, request);
        if (command == this) {
            executeCommand(request, response);
        } else if (command == null) {
            forward(Page.PAGE_404, request, response);
        } else {
            command.execute(request, response);
        }
    }

    /**
     * Method must be overridden in each subclass, otherwise it will be redirected to a 404 page.
     *
     * @param request -
     * @param response -
     * @throws ServletException -
     * @throws IOException -
     */
    @Override
    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forward(Page.PAGE_404, request, response);
    }

    protected Command<String> getCommand(Command<String> command, HttpServletRequest request) {

        Command<String> comm;

        // TODO нет обработки команд 3-го и далее уровня

        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        Class<? extends Command> commandClazz = command.getClass();
        WebCommand webCommand = commandClazz.getAnnotation(WebCommand.class);
        Class<? extends Command> parentClazz = webCommand.parent();
        String urlPattern = webCommand.urlPattern();

        if (parentClazz == Command.class) {
            // root level
            comm = super.getCommand(servletPath);
            if (comm == null) {
                comm = super.getCommand(servletPath + "/*");
            }
        } else if (pathInfo == null || pathInfo.equals("/")) {
            // first level command has handler in executeCommand() body method
            comm = command;
        } else if (urlPattern.equals(pathInfo) || urlPattern.equals(pathInfo + "/*")) {
            // self invoked command, need to execute executeCommand() method
            comm = command;
        } else {
            comm = super.getCommand(pathInfo);
            if (comm == null) {
                comm = super.getCommand(pathInfo + "/*");
            }
        }

        return comm;
    }

    // TODO лучше будет переделать на сервис, все-равно мы используем тут только сервлет контекст
    protected void forward(Page page, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        getServletContext().getRequestDispatcher(page.url).forward(request, response);
    }

    protected void forward(String url, ServletRequest request, ServletResponse response) throws IOException, ServletException {

        // TODO можно переделать на генерацию адреса.
        // Например для каждой команды мы можем выстраивать путь по его @WebCommand(urlPattern, где /jsp это паттерн первого уровня, потом /admin и т.д.
        // without leading slash we will have endless loop
        if (!url.matches("/.*")){
            url = '/' + url;
        }
//        if (!url.matches("/jsp.*")){
//            url = "/jsp" + url;
//        }
        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    protected enum Page {
        DEF("/"),
        PAGE_404("/jsp/error/error404.jsp"),
        PAGE_500("/jsp/error/error500.jsp");

        private String url;

        Page(String url) {
            this.url = url;
        }
    }
}
