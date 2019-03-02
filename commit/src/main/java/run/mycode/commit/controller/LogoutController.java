package run.mycode.commit.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author bdahl
 */
@Controller
public class LogoutController {

    @RequestMapping(value = {"/logout.html"})
    public String logoutDo(HttpServletRequest request, HttpServletResponse response) {
        
        SecurityContextHolder.clearContext();
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        for (Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }

        return "logout";
    }
}
