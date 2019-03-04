package run.mycode.commit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
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
    
    @Autowired
    GitHubUserService userService;
    
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
        
        GitHubUser authUser = userService.downloadUserInfo(req.getAccessToken());
        
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
}
