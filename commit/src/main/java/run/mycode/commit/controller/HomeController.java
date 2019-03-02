package run.mycode.commit.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import run.mycode.commit.persistence.dto.GitHubOrgListItem;
import run.mycode.commit.persistence.dto.User;

@Controller
public class HomeController {

    @Value("${uri.github.user.orgs}")
    private String githubUserOrgsUrl;
    
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    //@Autowired
    //private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping(value = {"", "/", "/index.html"})
    public String showHome(Model model, Authentication auth) {
        
        User user = (User)auth.getPrincipal();//SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + user.getGithubToken());
        
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        
        ResponseEntity<List<GitHubOrgListItem>> response = restTemplate.exchange(githubUserOrgsUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<GitHubOrgListItem>>(){});
        
        List<GitHubOrgListItem> orgs = response.getBody();
        
        model.addAttribute("orgs", orgs);
        
        return "home";
    }
}