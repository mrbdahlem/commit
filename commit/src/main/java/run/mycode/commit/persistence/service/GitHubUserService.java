package run.mycode.commit.persistence.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import run.mycode.commit.persistence.repository.GitHubUserRepository;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.service.GitHubService;

/**
 * Get user information from GitHub after an oauth login 
 * 
 * @author bdahl
 */
@Service
public class GitHubUserService implements IGitHubUserService {
    
    @Autowired
    GitHubUserRepository userRepo;
    
    @Autowired
    GitHubService githubService;
    
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
        
        GitHubUser authUser = githubService.downloadUserInfo(req.getAccessToken());
        
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
            regUser = userRepo.save(regUser);
        }            

        return regUser;
    }   

    /**
     * Get a list of all disabled users
     * 
     * @return the list of users that have not been enabled
     */
    @Override
    public List<GitHubUser> findDisabled() {
        return userRepo.findByEnabled(false);
    }
    
    /**
     * Activate a user account
     * 
     * @param user the user to activate
     */
    public void enableUser(GitHubUser user) {
        user.setEnabled(true);
        userRepo.save(user);
    }

    @Override
    public boolean deleteUser(String username) {
        GitHubUser user = userRepo.findByGithubUsername(username);
        if (user == null) {
            return false;
        }
        
        userRepo.delete(user);
        return true;
    }

    @Override
    public boolean enableUser(String username) {
        GitHubUser user = userRepo.findByGithubUsername(username);
        if (user == null) {
            return false;
        }
        
        // Make sure the user defaults to an instructor
        if (user.getRoleString() == null || user.getRoleString().isEmpty()) {
            user.setRoleString("ROLE_INSTRUCTOR");
        }
        
        user.setEnabled(true);
        userRepo.save(user);
        return true;
    }

    @Override
    public GitHubUser findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }
}
