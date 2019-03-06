/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run.mycode.commit.persistence.service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

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
    
}
