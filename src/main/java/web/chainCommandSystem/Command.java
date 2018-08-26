package web.chainCommandSystem;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Command<T>{

    private ServletContext servletContext;
    private HashMap<T, Command<T>> commandMap;{
        commandMap = new HashMap<>();
    }

    public Command(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Command<T> getCommand(T key ){
        return commandMap.get(key);
    }

    public Map<T, Command> getCommands() {
        return Collections.unmodifiableMap(commandMap);
    }

    public void putCommand(T key, Command<T> command){
        commandMap.put(key, command);
    }

    /**
     * Method is implementation of pattern Chain of responsibility, Command is routed to another command or invoke executeCommand method.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public abstract void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    /**
     * Method is implementation of pattern Command. Each command had to override this method.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public abstract void executeCommand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
