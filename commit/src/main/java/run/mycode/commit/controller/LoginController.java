package run.mycode.commit.controller;

import java.util.HashMap;
import java.util.Map;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";
    
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/login.html")
    public String getLoginPage(Model model, final Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
    
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
        
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
            .as(Iterable.class);
        
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
            
            clientRegistrations.forEach(registration -> 
                    oauth2AuthenticationUrls.put(registration.getClientName(), 
                                    authorizationRequestBaseUri + "/" + 
                                    registration.getRegistrationId()));
            
        }
        
        model.addAttribute("urls", oauth2AuthenticationUrls);
        
        return "login";
    }
}