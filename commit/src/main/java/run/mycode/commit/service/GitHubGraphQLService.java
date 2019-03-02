package run.mycode.commit.service;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author bdahl
 */
@Service
@Scope("session")
abstract class GitHubGraphQLService extends GitHubRestService {
    private final Authentication auth;
    
    public GitHubGraphQLService() {
        super();
        
        auth = (Authentication)SecurityContextHolder.getContext().getAuthentication();
    }
    
}
