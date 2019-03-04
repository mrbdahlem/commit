package run.mycode.commit.controller;

import java.util.HashMap;
import java.util.Map;
import java.security.Principal;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handle login requests
 * 
 * @author bdahl
 */
@Controller
public class LoginController {

    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";
    
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    
    /**
     * Allow the user to login using OAuth2
     * 
     * @param model The data model for the user's session
     * @param principal The user's current info, should be null for initial login
     * 
     * @return the name of the template to display
     */
    @GetMapping("/login.html")
    public String getLoginPage(Model model, final Principal principal) {
        // if the user is already logged in, return to the home page
        if (principal != null) {
            return "redirect:/";
        }
    
        // Add the providers to the current model for display on the template
        model.addAttribute("urls", getLoginUrls());
        
        // Display the login template
        return "login";
    }
    
    
    @RequestMapping(value = {"/logout.html"})
    public String doLogout(Model model, HttpServletRequest request, HttpServletResponse response) {
        
        SecurityContextHolder.clearContext();
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        for (Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }

        // Add the providers to the current model for display on the template
        model.addAttribute("urls", getLoginUrls());
        model.addAttribute("message", "Logged Out");
        
        return "login";
    }
    
    @RequestMapping(value = "/disabled.html")
    public String disabledAccount() {
        return "disabled";
    }
    
    /**
     * Get all of the allowed login providers
     * 
     * @return a mapping between the name and login urls for all providers 
     */
    private Map<String, String> getLoginUrls() {
        
        // Determine all allowed oauth2 providers
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
        
        Iterable<ClientRegistration> clientRegistrations;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
            .as(Iterable.class);
        
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
            
            clientRegistrations.forEach(registration -> 
                    oauth2AuthenticationUrls.put(registration.getClientName(), 
                                    authorizationRequestBaseUri + "/" + 
                                    registration.getRegistrationId()));
            
        }
        
        return oauth2AuthenticationUrls;
    }
}