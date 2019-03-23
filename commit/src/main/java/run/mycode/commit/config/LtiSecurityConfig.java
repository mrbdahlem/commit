/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run.mycode.commit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.nonce.InMemoryNonceServices;
import org.springframework.security.oauth.provider.token.InMemoryProviderTokenServices;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import run.mycode.lti.launch.oauth.LtiAuthenticationHandler;
import run.mycode.lti.launch.oauth.LtiConsumerDetailsService;

/**
 *
 * @author bdahl
 */
@Configuration
@Order(1)
public class LtiSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private LtiConsumerDetailsService consumerDetailsService;
    @Autowired
    private LtiAuthenticationHandler authenticationHandler;
    @Autowired
    private OAuthProviderTokenServices oauthProviderTokenServices;
      
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/lti/**")
            .and()
            .antMatcher("/lti/**")
                .headers().frameOptions().disable()
            .and()
                .addFilterBefore(configureProcessingFilter(), OAuth2LoginAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers("/lti/**")
                .permitAll()
                ;
    }
    
    
    @Bean(name = "oauthProviderTokenServices")
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        // NOTE: we don't use the OAuthProviderTokenServices for 0-legged but it cannot be null
        return new InMemoryProviderTokenServices();
    }
    
    private ProtectedResourceProcessingFilter configureProcessingFilter() {
            //Set up nonce service to prevent replay attacks.
            InMemoryNonceServices nonceService = new InMemoryNonceServices();
            nonceService.setValidityWindowSeconds(600);

            ProtectedResourceProcessingFilter processingFilter = new ProtectedResourceProcessingFilter();
            processingFilter.setAuthHandler(authenticationHandler);
            processingFilter.setConsumerDetailsService(consumerDetailsService);
            processingFilter.setNonceServices(nonceService);
            processingFilter.setTokenServices(oauthProviderTokenServices);
            return processingFilter;
        }
}
