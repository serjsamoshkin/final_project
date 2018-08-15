package web.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

@WebFilter("/*")
public class LocalizationFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
            throws IOException, ServletException {

        HttpServletResponse servletResponse = (HttpServletResponse) response;
        HttpServletRequest servletRequest = (HttpServletRequest)request;
        HttpSession session = servletRequest.getSession();

        if (request.getParameter("lang") != null){
            String lang = request.getParameter("lang");
            Locale locale;
            switch (lang) {
                case "en":
                    locale = new Locale("en", "EN");
                    lang = "en_En";
                    break;
                case "ru":
                    lang = "ru_RU";
                    locale = new Locale("ru", "RU");
                    break;
                default:
                    lang = "";
                    locale = Locale.getDefault();
                    break;
            }
            servletRequest.getSession().setAttribute("language", lang);
            servletRequest.getSession().setAttribute("locale", locale);

            servletResponse.sendRedirect(servletRequest.getHeader("referer"));
            return;

        }

        if (session.getAttribute("language") == null ){
            session.setAttribute("language", "");
            session.setAttribute("locale", Locale.getDefault());
        }

        next.doFilter(request, response);
    }

    public void destroy() {
    }
}
