package web.pageActivator;

import javax.servlet.http.HttpSession;
import java.util.List;

public class ActivePage {
    private List<String> pageList;
    private HttpSession session;

    @SuppressWarnings("unchecked")
    public ActivePage(HttpSession session) {
        this.pageList = (List<String>) session.getServletContext().getAttribute("pageList");
        this.session = session;
    }

    /**
     * Contains a map with keys WebPatters of the first level commands. The value
     * of the pair is a mark that this page (and it's sub pages) is active.
     * The mark of the activity is a string *command_url_pattern* plus '_active' string
     * (for example administrator_active)
     *
     * @param pattern - the value of the WebPattern annotation of the activated first level command
     */
    public void setActivePage(String pattern) {
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
