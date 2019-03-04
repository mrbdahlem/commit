package run.mycode.commit.security;

import java.io.IOException;
import java.security.Principal;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import run.mycode.commit.persistence.dto.GitHubUser;

/**
 * Check if a user logged in via oauth2 is enabled for access
 * 
 * @author bdahl
 */
public class UserEnabledFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;

        Principal principal = req.getUserPrincipal();

        // If the user is logged in via oauth2
        if (principal != null && principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            
            GitHubUser user = (GitHubUser)token.getPrincipal();
            
            if (!user.isEnabled()) {
                HttpServletResponse res = (HttpServletResponse) response;                

                // Log the user out
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                for (Cookie cookie : req.getCookies()) {
                    cookie.setMaxAge(0);
                }

                // Show disabled error page
                res.sendRedirect("/disabled.html");
                return;
            }
        }

        chain.doFilter(request, response);
    }
    
}
