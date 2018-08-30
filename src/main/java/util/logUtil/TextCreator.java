package util.logUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

public class TextCreator {

    public static String getErrorLogText(String text, HttpServletRequest request){
        StringBuilder error = new StringBuilder();
        error.append("Error text: ").append(text).append(" ").append("\n");
        error.append("HeaderNames: ").append(" ").append("\n");
        Collections.list(request.getHeaderNames()).forEach(h -> error.append(h).append(": ").append(request.getHeader(h)).append(" ").append("\n"));
        error.append("Session: ").append(request.getSession()).append(" ").append("\n");
        error.append("Session parameters: ").append(" ").append("\n");
        Collections.list(request.getSession().getAttributeNames()).forEach(e -> error.append(e).append(": ").append(request.getSession().getAttribute(e)).append(" ").append("\n"));
        error.append("Method: ").append(request.getMethod()).append(" ").append("\n");
        error.append("ServletPath: ").append(request.getServletPath()).append(" ").append("\n");
        error.append("PathInfo: ").append(request.getPathInfo()).append(" ").append("\n");
        error.append("QueryString: ").append(request.getQueryString()).append(" ").append("\n");
        error.append("ParameterMap: ").append(" ").append("\n");
        request.getParameterMap().forEach((k, v) -> error.append(k).append(": ").append(Arrays.toString(v)));
        error.append(" ").append("\n");

        return error.toString();
    }

}
