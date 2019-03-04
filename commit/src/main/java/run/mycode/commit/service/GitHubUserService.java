package run.mycode.commit.service;

import java.util.List;
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
import run.mycode.commit.persistence.dto.GitHubUser;
import run.mycode.commit.persistence.repository.GitHubUserRepository;

/**
 *
 * @author bdahl
 */
@Service
public class GitHubUserService {

    @Autowired
    GitHubUserRepository userRepo;

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

    public List<GitHubUser> findDisabled() {
        return userRepo.findByEnabled(false);
    }
    
    public void enableUser(GitHubUser user) {
        user.setEnabled(true);
        userRepo.save(user);
    }
}
