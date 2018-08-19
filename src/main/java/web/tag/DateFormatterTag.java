package web.tag;

import util.datetime.LocalDateTimeFormatter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateFormatterTag extends SimpleTagSupport {

    private String shortDate;

    public void setShortDate(String shortDate) {
        this.shortDate = shortDate;
    }

    @Override
    public void doTag() throws JspException {

        try {
            Locale locale = (Locale) getJspContext().getAttribute("locale", 3);
            getJspContext().getOut().write(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
                            .format(LocalDateTimeFormatter.toLocalDate(shortDate))
            );
        }catch (Exception e){
            throw new JspException(e);
        }


    }

}