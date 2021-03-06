package run.mycode.commit.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth.provider.ConsumerCredentials;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.service.IOrgNameService;
import run.mycode.commit.persistence.service.ICourseService;
import run.mycode.lti.launch.exception.NoLtiSessionException;
import run.mycode.lti.launch.model.LtiLaunchData.InstitutionRole;
import run.mycode.lti.launch.model.LtiSession;
import run.mycode.lti.launch.service.LtiSessionService;

/**
 * A combined library of REST and GraphQL services provided by GitHub.
 *
 * @author bdahl
 */
public class GitHubService {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubService.class);
    private static final HttpConnector httpConnector = makeConnector();

    private final GitHub github;
    private final GHMyself user;

    private final IOrgNameService orgNameService;

    /**
     * Create a GitHubService for use by the current user
     *
     * @param courseService
     * @param orgNameService
     * @param ltiSessionService
     * @param auth
     * 
     * @throws IOException
     * @throws run.mycode.lti.launch.exception.NoLtiSessionException
     */
    public GitHubService(ICourseService courseService,
                         IOrgNameService orgNameService,
                         LtiSessionService ltiSessionService,
                         Authentication auth) throws IOException, NoLtiSessionException {

        this.orgNameService = orgNameService;
        
        Object usr = auth.getPrincipal();

        GitHubUser gUser;

        if (usr instanceof GitHubUser) {
            gUser = (GitHubUser) usr;
        } else if (usr instanceof ConsumerCredentials) {
            LtiSession ltiSession = ltiSessionService.getLtiSession();

            List<InstitutionRole> authRoles = ltiSession.getLtiLaunchData().getRolesList();
            //TODO: get the user's github token to create the github instance
            ConsumerCredentials creds = (ConsumerCredentials) usr;
            String courseKey = creds.getConsumerKey();

            //TODO: add other roles
            if (authRoles.contains(InstitutionRole.Instructor)
                    || authRoles.contains(InstitutionRole.ContentDeveloper)
                    || authRoles.contains(InstitutionRole.Creator)) {
                gUser = courseService.getOwnerByKey(courseKey);
                //gUser = course.getOwner();
            }
            else if (authRoles.contains(InstitutionRole.Learner)) {
                throw new IOException("Not implemented");
            }
            else {
                throw new IOException();
            }
        } else {
            //FIX: need to throw a better exception
            throw new IOException();
        }

        github = new GitHubBuilder()
                .withOAuthToken(gUser.getGithubToken())
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

        OkHttpClient client
                = new OkHttpClient.Builder()
                        .addNetworkInterceptor(logging)
                        .cache(cache)
                        .build();

        OkHttp3Connector ok3hc = new OkHttp3Connector(new OkUrlFactory(client));

        return (HttpConnector) ok3hc;
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
