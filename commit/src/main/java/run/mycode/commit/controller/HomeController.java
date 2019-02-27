package run.mycode.commit.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;
import run.mycode.commit.model.GitHubOrgListItem;

@Controller
public class HomeController {

    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping(value = {"", "/", "/index.html"})
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        String userInfoEndpointUri = client.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            
            if (userAttributes.get("name") != null) {
                model.addAttribute("name", userAttributes.get("name"));
            }
            else if (userAttributes.get("login") != null){
                model.addAttribute("name", userAttributes.get("login"));
            }
            else {
                model.addAttribute("name", "Anonymous User");
            }
        }
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
        
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        
        String orgendpoint = client.getClientRegistration()
                                    .getProviderDetails()
                                    .getUserInfoEndpoint()
                                    .getUri();
        
        ResponseEntity<List<GitHubOrgListItem>> response = restTemplate.exchange("https://api.github.com/user/orgs", HttpMethod.GET, entity, new ParameterizedTypeReference<List<GitHubOrgListItem>>(){});
        
        List<GitHubOrgListItem> orgs = response.getBody();
        
        model.addAttribute("orgs", orgs);
        
        return "home";
    }
}