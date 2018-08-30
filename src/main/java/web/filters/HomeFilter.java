package web.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.wrappers.WrappedUser;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/home"})
public class HomeFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(HomeFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        WrappedUser wrappedUser = WrappedUser.of(httpRequest.getSession().getAttribute("user"));

        if (wrappedUser.isAdmin()) {
            httpResponse.sendRedirect("/administrator");
            return;
        } else if (wrappedUser.isMaster()) {
            httpResponse.sendRedirect("/master");
            return;
        }

        next.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}

