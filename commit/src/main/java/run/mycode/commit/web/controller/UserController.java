package run.mycode.commit.web.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.service.ICourseService;
import run.mycode.commit.persistence.service.IOrgNameService;
import run.mycode.commit.service.GitHubService;
import run.mycode.commit.web.dto.OrgInfo;
import run.mycode.commit.web.dto.RepoInfo;
import run.mycode.lti.launch.exception.NoLtiSessionException;
import run.mycode.lti.launch.service.LtiSessionService;

/**
 *
 * @author bdahl
 */
@Controller
@Scope(value = "session")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private ICourseService courseService;
    @Autowired
    private IOrgNameService orgNameService;
    @Autowired
    private LtiSessionService ltiSessionService;
    
    @PostMapping("/user/orgs")
    @ResponseBody
    public Collection<OrgInfo> getOrgs(Authentication auth) 
            throws NoLtiSessionException {
          
        try {
            GitHubService gitHubService = new GitHubService(courseService,
                    orgNameService, ltiSessionService, auth);
             
            Set<GHOrganization> orgs = gitHubService.getOrgs();
            
            return orgs.stream().map(org -> new OrgInfo(org)).collect(Collectors.toSet());
        }
        catch (IOException e) {
            return null;
        }
    }
    
    @PostMapping("/user/orgRepos/{orgname}")
    @ResponseBody
    public Collection<RepoInfo> getRepos(@PathVariable("orgname") String org,
                                         Authentication auth) 
            throws NoLtiSessionException {
        
        
        try {
            GitHubService gitHubService = new GitHubService(courseService, 
                    orgNameService, ltiSessionService, auth);
            
            Collection<GHRepository> repos = gitHubService.getRepos(org);
        
            return repos.stream().map(repo -> new RepoInfo(repo)).collect(Collectors.toSet());
        }
        catch (IOException e) {
            return null;
        }
    }
}
