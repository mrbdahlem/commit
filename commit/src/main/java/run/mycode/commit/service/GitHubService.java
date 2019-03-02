package run.mycode.commit.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * A combined library of REST and GraphQL services provided by GitHub.
 * 
 * @author bdahl
 */
@Service
@Scope("session")
public class GitHubService extends GitHubGraphQLService {
    
}
