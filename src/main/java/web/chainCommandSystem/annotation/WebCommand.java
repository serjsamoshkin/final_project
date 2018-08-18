package web.chainCommandSystem.annotation;

import web.chainCommandSystem.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebCommand {
    String urlPattern();
    Class<? extends Command> parent();
}
