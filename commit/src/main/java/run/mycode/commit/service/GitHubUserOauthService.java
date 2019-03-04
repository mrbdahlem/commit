package run.mycode.commit.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;
import run.mycode.commit.persistence.dto.GitHubUser;
import run.mycode.commit.persistence.repository.GitHubUserRepository;

/**
 * Get user information from GitHub after an oauth login 
 * 
 * @author bdahl
 */
@Service
public class GitHubUserOauthService implements OAuth2UserService<OAuth2UserRequest,OAuth2User> {
    @Autowired
    GitHubUserRepository userRepo;
    
    /**
     * Load and update the registered user from the authenticated user
     * 
     * @param req
     * @return A registered user
     * 
     * @throws OAuth2AuthenticationException 
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) 
            throws OAuth2AuthenticationException {
        
        String userInfoEndpointUri = req.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            // Request the GitHub user information
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + req.getAccessToken()
                .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            
            // Create a user with the loaded information
            GitHubUser authUser = new GitHubUser();
            authUser.setAttributes(userAttributes);
            authUser.setGithubToken(req.getAccessToken().getTokenValue());
            
            // Load the corresponding registered user (if present)
            GitHubUser regUser = userRepo.findById(authUser.getId()).orElse(null);
            
            // If the registered user is not the same as the authenticated user
            if (!authUser.equals(regUser)) {
                // Update the registered user with the authenticated information
                if (regUser != null) {
                    regUser.setAttributes(authUser.getAttributes());
                    regUser.setGithubToken(authUser.getGithubToken());
                }
                else {
                    regUser = authUser;
                }
                
                // Add the user to the repo
                userRepo.save(regUser);
            }            
            
            return regUser;
        }
        
        return null;
    }    
}
