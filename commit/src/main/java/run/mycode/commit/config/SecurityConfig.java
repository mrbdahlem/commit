package run.mycode.commit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.session.SessionManagementFilter;
import run.mycode.commit.security.UserEnabledFilter;
import run.mycode.commit.persistence.service.IGitHubUserService;

@Configuration
@Order(2)
@PropertySource("application.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {    
    @Autowired
    IGitHubUserService githubUserSvc;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/login.html", "/loginFailure", "/disabled.html", "/error",
                         "/*.ico", "/*.png", "/*.svg", "/css/**", "/js/**")
                .permitAll()
            .anyRequest()
                .authenticated()
            .and()
            .oauth2Login()
                .loginPage("/login.html")
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorize-client")
                    .authorizationRequestRepository(authorizationRequestRepository())
                .and()
                .userInfoEndpoint()
                    .userService(githubUserSvc) 
                .and()
                .tokenEndpoint()
                    .accessTokenResponseClient(accessTokenResponseClient())
                .and()
                    .defaultSuccessUrl("/")
                    .failureUrl("/loginFailure")
                .and()
                    .addFilterBefore(new UserEnabledFilter(), SessionManagementFilter.class)
                ;
    }
    
    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        return accessTokenResponseClient;
    }    
}