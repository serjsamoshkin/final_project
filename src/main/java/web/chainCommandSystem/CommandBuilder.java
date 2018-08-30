package web.chainCommandSystem;


import web.chainCommandSystem.annotation.WebCommand;
import org.reflections.Reflections;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Считывает классы аннотированные @WebCommand
 */
public class CommandBuilder {

    public <T> Command<T> build(ServletContext sc, String packageName) {

        Command<T> rootCommand = null;

        Map<Class<? extends Command<T>>, Class<? extends Command<T>>> result = new HashMap<>();

        Reflections ref = new Reflections(packageName);
        for (Class<?> cl : ref.getTypesAnnotatedWith(WebCommand.class)) {
            WebCommand webCommand = cl.getAnnotation(WebCommand.class);

            if (Command.class.isAssignableFrom(cl)) {
                @SuppressWarnings("unchecked")
                Class<? extends Command<T>> castCl = (Class<? extends Command<T>>) cl;
                @SuppressWarnings("unchecked")
                Class<? extends Command<T>> castParent = (Class<? extends Command<T>>) webCommand.parent();
                result.put(castCl, castParent);
            }
        }

        Map<Class<? extends Command<T>>, Command<T>> struct = new HashMap<>();

        int quantity = 0;
        try {
            while (quantity != result.size()) {

                quantity = result.size();

                for (Iterator<Map.Entry<Class<? extends Command<T>>
                        , Class<? extends Command<T>>>> it
                     = result.entrySet().iterator(); it.hasNext(); ) {

                    Map.Entry<Class<? extends Command<T>>, Class<? extends Command<T>>> entry = it.next();

                    Class<? extends Command<T>> clazz = entry.getKey();
                    Class<? extends Command<T>> parent = entry.getValue();

                    if (parent.equals(Command.class)) {

                        rootCommand = clazz.getConstructor(ServletContext.class).newInstance(sc);
                        struct.put(clazz, rootCommand);
                        it.remove();

                        break;
                    } else if (struct.get(parent) != null) {

                        Command<T> command = clazz.getConstructor(ServletContext.class).newInstance(sc);
                        struct.put(clazz, command);
                        it.remove();

                        WebCommand webCommand = clazz.getAnnotation(WebCommand.class);

                        /*
                        A bit strange because I know that the webCommand.urlPattern()'s return
                        value is always a String. But I can change urlPattern on some object with additional
                         functionality, for example with default action (forward to the page).
                         */
                        struct.get(parent).putCommand((T) webCommand.urlPattern(), command);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (quantity != 0) {
            throw new RuntimeException();
        }

        return rootCommand;

    }

}
