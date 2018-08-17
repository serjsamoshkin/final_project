package web.tag;

import util.LocalDateTimeFormatter;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class TimeFormatterTag extends SimpleTagSupport {

    private String shortTime;

    public void setShortTime(String shortTime) {
        this.shortTime = shortTime;
    }

    @Override
    public void doTag() throws JspException {

        try {
            Locale locale = (Locale) getJspContext().getAttribute("locale", 3);

            getJspContext().getOut().write(
                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
                            .format(LocalDateTimeFormatter.toLocalTime(shortTime))
            );
        }catch (Exception e){
            throw new JspException(e);
        }


    }

}