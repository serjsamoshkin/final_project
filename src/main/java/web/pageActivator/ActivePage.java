package web.pageActivator;

import javax.servlet.http.HttpSession;
import java.util.List;

public class ActivePage {
    private List<String> pageList;
    private HttpSession session;

    public ActivePage(HttpSession session) {
        this.pageList = (List<String>) session.getServletContext().getAttribute("pageList");
        this.session = session;
    }

    // TODO перевести
    /**
     * Содержит MAP с ключами WebPattern первого уровня и признаком какая ветка (ключ) активна.
     * Признаком активности служит строка _active добавленная к ключу без слеша.
     *
     * @param pattern
     */
    public void setActivePage(String pattern) {
        // TODO Очередная кривость с добавлением и убвалением слешей и звезд...
        for (String key : pageList) {
            session.setAttribute(
                    key.replace("/*", "")
                            .replace("/", "")
                            + "_active",
                    (key.equals(pattern) || key.equals(pattern + "/*")) ? "active" : ""
            );
        }
    }
}
