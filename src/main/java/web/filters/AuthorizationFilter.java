package web.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.wrappers.WrappedUser;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebFilter(urlPatterns = {"/administrator/*", "/master/*", "/reception/*", "/review/*", "/my_receptions/*"})
public class AuthorizationFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(AuthorizationFilter.class);

    @Override
    public void init(FilterConfig filterConfig){
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        WrappedUser wrappedUser = WrappedUser.of(httpRequest.getSession().getAttribute("user"));

        boolean isAuthorized = false;

        if (wrappedUser.isAuthenticated()){
            switch (httpRequest.getServletPath()){
                case "/administrator":
                    if (wrappedUser.isAdmin()) {
                        isAuthorized = true;
                    }
                    break;
                case "/master": {
                    if (wrappedUser.isMaster()) {
                        isAuthorized = true;
                    }
                    break;
                }
                case "/review":
                case "/my_receptions":
                case "/reception": {
                    if (wrappedUser.isUser()) {
                        isAuthorized = true;
                    }
                    break;
                }
            }
        }

        if (isAuthorized){
            next.doFilter(request, response);
        }else {
            if (wrappedUser.isAuthenticated()) {
                httpResponse.sendRedirect("/jsp/error/error403.jsp");
            }else {
                request.setAttribute("refer", createReferer(httpRequest));
                httpRequest.getServletContext().getRequestDispatcher("/login").forward(request, response);
            }
        }
    }

    private String createReferer(HttpServletRequest request){

        StringBuilder str = new StringBuilder(request.getServletPath());
        str.append(request.getPathInfo() == null ? "" : request.getPathInfo());
        if (request.getQueryString() != null) {
            str.append("?");
            str.append(request.getQueryString());
        }
        return str.toString();
    }

    @Override
    public void destroy() {
    }
}
