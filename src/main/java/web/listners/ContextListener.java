package web.listners;

import web.chainCommandSystem.Command;
import web.chainCommandSystem.CommandBuilder;
import model.dao.DaoMapper;
import persistenceSystem.sql.MySqlJDBCDaoController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.ServiceMapper;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


@WebListener
public class ContextListener implements ServletContextListener{

    private static final Logger logger = LogManager.getLogger(ContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent){

        ServletContext context = servletContextEvent.getServletContext();
        //System.setProperty("rootPath", context.getRealPath("/"));

        DaoMapper.buildMapper(new MySqlJDBCDaoController());

        if (context.getAttribute("isInitialized") != null) {
            //  This method is called several times when the application is started,
            // so I think no need to do init work more than once
            return;
        }else {
            context.setAttribute("isContextInitialized", true);
        }

        // TODO подумать как не хранить в контексте
        Command<String> rootCommand = new CommandBuilder().build(context, "web.command");

        // TODO ничего не хранить в контексте
        context.setAttribute("rootCommand", rootCommand);

        // TODO не красиво
        /*
        Adds all first level commands (WebPatterns) to List. This attribute will be used to maintain current active page.
        More in ActivePage class.
         */
        context.setAttribute("pageList", new ArrayList<>(rootCommand.getCommands().keySet()));

        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/beauty_saloon");

            // we'll also catch SQL exceptions here.
            Connection con = ds.getConnection();
            con.close();

            /*Run after DaoMapper*/
            ServiceMapper.buildMapper(context, ds);

        } catch (NamingException e) {
            logger.error("InitialContext error: db init, jdbc/beauty_saloon", e);
            context.setAttribute("criticalError", true);
            throw new RuntimeException(e);
        } catch (SQLException e) {
            logger.error("SQL error: db init, jdbc/beauty_saloon", e);
            context.setAttribute("criticalError", true);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
