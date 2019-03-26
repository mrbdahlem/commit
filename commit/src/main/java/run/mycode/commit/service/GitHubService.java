package run.mycode.commit.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.HttpConnector;
import org.kohsuke.github.extras.OkHttp3Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.service.IOrgNameService;

/**
 * A combined library of REST and GraphQL services provided by GitHub.
 * 
 * @author bdahl
 */
@Service
@Scope(value="session")
public class GitHubService {
    private static final Logger LOG = LoggerFactory.getLogger(GitHubService.class);
    private static final HttpConnector httpConnector = makeConnector();
    
    private final GitHub github;
    private final GHMyself user;
    
    @Autowired
    private IOrgNameService orgNameService;
    
    /**
     * Create a GitHubService for use by the current user
     * @throws IOException 
     */
    public GitHubService() throws IOException {
        GitHubUser usr = (GitHubUser)SecurityContextHolder.getContext()
                                                          .getAuthentication()
                                                          .getPrincipal();
        
        github = new GitHubBuilder()
                .withOAuthToken(usr.getGithubToken())
                .withConnector(GitHubService.getConnector())
                .build();
        
        user = github.getMyself();
    }
    
    public static HttpConnector getConnector() {
        return httpConnector;
    }
    
    private static HttpConnector makeConnector() {
        File cacheDirectory = new File(System.getProperty("java.io.tmpdir"));
        
        LOG.info("Caching github requests to: " + cacheDirectory.getAbsolutePath());
        
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(LOG::info);
        logging.setLevel(Level.BASIC);
        
        Cache cache = new Cache(cacheDirectory, 10 * 1024 * 1024); // 10MB cache
        
        OkHttpClient client = 
                new OkHttpClient.Builder()
                .addNetworkInterceptor(logging)
                .cache(cache)
                .build();

        OkHttp3Connector ok3hc = new OkHttp3Connector(new OkUrlFactory(client));
        
        return (HttpConnector)ok3hc;
    }
    
    /**
     * Get a list of all GitHub organizations associated with the logged in user
     * and update the stored names for those organizations
     * 
     * @return The list of organizations accessible to the user
     * 
     * @throws IOException 
     */
    public Set<GHOrganization> getOrgs() throws IOException {
        Set<GHOrganization> orgs = user.getAllOrganizations();
        orgNameService.updateAll(orgs);
        return orgs;
    }
    
    public Collection<GHRepository> getRepos(String owner) throws IOException {
        if (owner == null || owner.equals(user.getLogin())) {
            return user.getAllRepositories().values();
        }
        
        GHOrganization org = github.getOrganization(owner);
        
        Collection<GHRepository> repos = new ArrayList<>();
        
        org.listRepositories(100).forEach(repo -> repos.add(repo));
        
        return repos;        
    }
    
    public GHRepository getRepo(String name) throws IOException {
        return github.getRepository(name);
    }
}
