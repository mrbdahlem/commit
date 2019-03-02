package run.mycode.commit.service;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;
import run.mycode.commit.persistence.dto.User;

/**
 *
 * @author bdahl
 */
public class GitHubUserService implements OAuth2UserService<OAuth2UserRequest,OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) 
            throws OAuth2AuthenticationException {
        
        String userInfoEndpointUri = req.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + req.getAccessToken()
                .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            
            User user = new User();
            
            user.setAttributes(userAttributes);
            user.setGithubToken(req.getAccessToken().getTokenValue());
            user.setRoleString("INSTRUCTOR");
            
            return user;
        }
        
        return null;
    }    
}
