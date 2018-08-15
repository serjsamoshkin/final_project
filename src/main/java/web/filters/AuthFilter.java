package web.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebFilter("/administrator/*")
public class AuthFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {

        ServletContext context = request.getServletContext();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession();

        @SuppressWarnings("unchecked")
        Map<String, Object> wrappedUser = (Map<String, Object>)session.getAttribute("user");
        boolean isAuthorized = (boolean) wrappedUser.getOrDefault("isAuthorized", false);

        if (isAuthorized){
            next.doFilter(request, response);
        }else {
            context.getRequestDispatcher("/jsp/error/error404.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
