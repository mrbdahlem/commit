package run.mycode.commit.service;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import run.mycode.commit.persistence.dto.GitHubOrgListItem;
import run.mycode.commit.persistence.dto.User;

/**
 *
 * @author bdahl
 */
@Service
@Scope("session")
abstract class GitHubRestService {    
    
    @Value("${uri.github.user.orgs}")
    private String githubUserOrgsUrl;
    
    private final Authentication auth;
    
    public GitHubRestService() {
        super();
        
        auth = (Authentication)SecurityContextHolder.getContext().getAuthentication();
    }
    
    /**
     * Retrieve a list of the organizations accessible to the user
     * @param user
     * @param url The user organization REST URL
     * @return 
     */
    public Collection<GitHubOrgListItem> getOrgs() {
        User user = (User)auth.getPrincipal();
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + user.getGithubToken());
        
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        
        ResponseEntity<List<GitHubOrgListItem>> response =
                restTemplate.exchange(githubUserOrgsUrl, HttpMethod.GET, entity, 
                        new ParameterizedTypeReference<List<GitHubOrgListItem>>(){});
        
        List<GitHubOrgListItem> orgs = response.getBody();
        
        return orgs;
    }
}