/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run.mycode.commit.persistence.service;

import java.util.List;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import run.mycode.commit.persistence.model.GitHubUser;

/**
 * Service Interface to load user information for users authenticated via oauth2
 * 
 * @author bdahl
 */
public interface IGitHubUserService  extends OAuth2UserService<OAuth2UserRequest,OAuth2User> {

    /**
     * Load and update the registered user from the authenticated user
     *
     * @param req
     * @return A registered user
     *
     * @throws OAuth2AuthenticationException
     */
    @Override
    OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException;
    
    /**
     * Get a list of all disabled users
     * 
     * @return the list of users that have not been enabled
     */
    public List<GitHubUser> findDisabled();
    
    /**
     * Delete a GitHub user with a given username
     * @param username the username of the user to delete
     * @return true if the user was deleted, false if the user was not found
     * or could not be deleted
     */
    public boolean deleteUser(String username);
    
    /**
     * Enable a GitHub user with a given username
     * @param username the username of the user to enable
     * @return true if the user was enabled, false if the user was not found
     * or could not be enabled
     */
    public boolean enableUser(String username);
}
