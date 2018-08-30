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
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

@WebCommand(urlPattern = "/",
        parent = Command.class)
public class RootCommand extends Command<String> {

    private static final Logger rootLogger = LogManager.getLogger(RootCommand.class);

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
            redirect(Page.PAGE_500, response);
            throw new ServletException("criticalError in servlet");
        }

        HttpSession session = request.getSession();
        if (session.getAttribute("sessionCriticalError") != null && (boolean) session.getAttribute("sessionCriticalError")) {
            redirect(Page.PAGE_500, response);
            throw new ServletException("sessionCriticalError");
        }

        String path = request.getServletPath();

        ActivePage activePage = (ActivePage) request.getSession().getAttribute("pageActivator");
        activePage.setActivePage(path);

        Command command = getCommand(this, request);
        if (command == this) {
            executeCommand(request, response);
        } else if (command == null) {
            redirect(Page.PAGE_404, response);
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
        redirect(Page.PAGE_404, response);
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


    protected void redirect(Page page, HttpServletResponse response) throws IOException, ServletException {
        redirect(page.url, response);
    }

    protected void redirect(String url, HttpServletResponse response) throws IOException, ServletException {
        // without leading slash will have endless loop
        if (!url.matches("/.*")){
            url = '/' + url;
        }
        response.sendRedirect(url);
    }

    protected void forward(String url, ServletRequest request, ServletResponse response) throws IOException, ServletException {

        // without leading slash will have endless loop
        if (!url.matches("/.*")){
            url = '/' + url;
        }

        request.getRequestDispatcher(url).forward(request, response);
    }

    protected void computeIfParameterPresent(HttpServletRequest request, String parameter, Consumer<String> action){

        if (request.getParameterMap().containsKey(parameter) && !request.getParameter(parameter).isEmpty()){
            action.accept(request.getParameter(parameter));
        }
    }

    protected void logFullError(String text, HttpServletRequest request, Exception e){

        rootLogger.error(getErrorLogText(text, request), e);
    }

    protected void logFullError(String text, HttpServletRequest request){


        rootLogger.error(getErrorLogText(text, request));
    }

    private String getErrorLogText(String text, HttpServletRequest request){
        StringBuilder error = new StringBuilder();
        error.append("Error text: ").append(text).append("\n");
        error.append("HeaderNames: ").append("\n");
        Collections.list(request.getHeaderNames()).forEach(h -> error.append(h).append(": ").append(request.getHeader(h)).append(" "));
        error.append("Session: ").append(request.getSession()).append("\n");
        error.append("Method: ").append(request.getMethod()).append("\n");
        error.append("ServletPath: ").append(request.getServletPath()).append("\n");
        error.append("PathInfo: ").append(request.getPathInfo()).append("\n");
        error.append("QueryString: ").append(request.getQueryString()).append("\n");
        error.append("ParameterMap: ").append("\n");
        request.getParameterMap().forEach((k, v) -> error.append(k).append(": ").append(Arrays.toString(v)));
        error.append("\n");

        return error.toString();
    }

    protected enum Page {
        DEF("/"),
        PAGE_403("/jsp/error/error403.jsp"),
        PAGE_404("/jsp/error/error404.jsp"),
        PAGE_500("/jsp/error/error500.jsp");

        private String url;

        Page(String url) {
            this.url = url;
        }
    }
}
