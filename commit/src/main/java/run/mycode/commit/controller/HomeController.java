package run.mycode.commit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import run.mycode.commit.service.GitHubService;

@Controller
@Scope("session")
public class HomeController   {
    @Autowired
    private GitHubService github;
    
    /**
     * Show a home page for a given user
     * 
     * @param model the data model for the session
     * 
     * @return the template to show 
     */
    @GetMapping(value = {"", "/", "/index.html"})
    public String showHome(Model model) {
        // Load the user's accessible organizations
        model.addAttribute("orgs", github.getOrgs());
        
        // Show the homepage
        return "home";
    }
}