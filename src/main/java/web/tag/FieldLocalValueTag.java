package web.tag;

import model.entity.reception.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.taglibs.standard.tag.common.core.SetSupport;
import persistenceSystem.util.Reflect;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.TagSupport;
import java.lang.reflect.Field;
import java.util.Locale;

public class FieldLocalValueTag extends TagSupport {

    Object value = null;
    String fieldName;
    private static final Logger logger = LogManager.getLogger(FieldLocalValueTag.class);
    private final static Locale localeRu = new Locale("ru", "RU");

    public void setValue(Object value) {
        this.value = value;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public int doEndTag() throws JspException {
        Locale locale = (Locale)pageContext.getSession().getAttribute("locale");
        Class<?> clazz = value.getClass();
        try {
            Field field;
            if (locale.equals(localeRu)){
                try {
                    field = Reflect.getFieldByName(clazz, fieldName + "Ru");
                }catch (Exception e){
                    logger.error(e);
                    field = Reflect.getFieldByName(clazz, fieldName);
                }
            }else {
                field = Reflect.getFieldByName(clazz, fieldName);
            }
            pageContext.getOut().print(Reflect.getFieldValue(clazz, field, value));
        } catch (Exception e) {
            throw new JspException(e.toString());
        }
        return EVAL_PAGE;
    }

}