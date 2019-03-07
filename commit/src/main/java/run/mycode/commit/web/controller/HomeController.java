package run.mycode.commit.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.service.GitHubUserService;
import run.mycode.commit.service.GitHubService;

@Controller
@Scope("session")
public class HomeController   {
    @Autowired
    private GitHubService github;
    
    @Autowired
    private GitHubUserService userService;
    
    /**
     * Show a home page for a given user
     * 
     * @param model the data model for the session
     * @param auth the current user's authentication
     * 
     * @return the template to show 
     */
    @GetMapping(value = {"", "/", "/index.html"})
    public String showHome(Model model, Authentication auth) {
        
        if (auth.getPrincipal() instanceof GitHubUser) {
            GitHubUser user = (GitHubUser) auth.getPrincipal();
            
            // Load the user's accessible organizations
            model.addAttribute("orgs", github.getOrgs(user));
            
            if (user.getRoleString().contains("ROLE_ADMIN")) {
                model.addAttribute("disabledUsers", userService.findDisabled());
            }
            
            if (user.getRoleString().contains("ROLE_INSTRUCTOR")) {
                
            }
        }
        
        // Show the homepage
        return "home";
    }
}