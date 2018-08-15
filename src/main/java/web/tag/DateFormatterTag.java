package web.tag;

import util.properties.DateTimePatternsPropertiesReader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.time.LocalDate;
import java.time.LocalTime;
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
            DateTimeFormatter fmtIn = DateTimeFormatter.ofPattern(
                    DateTimePatternsPropertiesReader.getInstance().getPropertyValue("date_pattern"));
            DateTimeFormatter fmtOut =  DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);

            LocalDate localDate = LocalDate.parse(shortDate, fmtIn);
            getJspContext().getOut().write(fmtOut.format(localDate));
        }catch (Exception e){
            throw new JspException(e);
        }


    }

}