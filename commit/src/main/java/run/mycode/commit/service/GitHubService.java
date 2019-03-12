package run.mycode.commit.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import run.mycode.commit.persistence.model.GitHubUser;

/**
 * A combined library of REST and GraphQL services provided by GitHub.
 * 
 * @author bdahl
 */
@Service
public class GitHubService extends GitHubGraphQLService {
    
    @Autowired
    private ClientRegistrationRepository clientRegRepo;

    public GitHubUser downloadUserInfo(OAuth2AccessToken token) {

        String userInfoEndpointUri = clientRegRepo.findByRegistrationId("github")
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        // Request the GitHub user information
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue());

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
        Map userAttributes = response.getBody();

        // Create a user with the loaded information
        GitHubUser authUser = new GitHubUser();
        authUser.setAttributes(userAttributes);
        authUser.setGithubToken(token.getTokenValue());

        return authUser;
    }
}
