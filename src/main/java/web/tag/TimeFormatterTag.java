package web.tag;

import util.properties.DateTimePatternsPropertiesReader;

import java.io.IOException;
import java.time.LocalTime;
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
            DateTimeFormatter fmtIn = DateTimeFormatter.ofPattern(
                    DateTimePatternsPropertiesReader.getInstance().getPropertyValue("time_pattern"));
            DateTimeFormatter fmtOut =  DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);

            LocalTime localTime = LocalTime.parse(shortTime, fmtIn);
            getJspContext().getOut().write(fmtOut.format(localTime));
        }catch (Exception e){
            throw new JspException(e);
        }


    }

}