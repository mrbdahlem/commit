package run.mycode.commit.controller;

import java.util.HashMap;
import java.util.Map;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        
        // Add the providers to the current model for display on the template
        model.addAttribute("urls", oauth2AuthenticationUrls);
        
        // Display the login template
        return "login";
    }
}