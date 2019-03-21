package run.mycode.commit.service;

import run.mycode.commit.persistence.model.GitHubUser;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import run.mycode.commit.web.dto.GitHubOrgListItem;

/**
 *
 * @author bdahl
 */
@Service
abstract class GitHubRestService {    
    
    @Value("${uri.github.user.orgs}")
    private String githubUserOrgsUrl;
       
    /**
     * Retrieve a list of the organizations accessible to the user
     * @param user
     * @return 
     */
    public Collection<GitHubOrgListItem> getOrgs(GitHubUser user) {
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
