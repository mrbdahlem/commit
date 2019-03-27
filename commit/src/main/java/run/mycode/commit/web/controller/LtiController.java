package run.mycode.commit.web.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth.provider.ConsumerCredentials;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import run.mycode.commit.persistence.model.Assignment;
import run.mycode.commit.persistence.service.IAssignmentService;
import run.mycode.commit.persistence.service.ICourseService;
import run.mycode.commit.service.GitHubService;
import run.mycode.commit.web.dto.RepoDetailedInfo;
import run.mycode.commit.web.util.ErrorView;
import run.mycode.commit.web.util.MessageView;
import run.mycode.lti.launch.model.LtiLaunchData;
import run.mycode.lti.launch.model.LtiLaunchData.InstitutionRole;
import run.mycode.lti.launch.model.LtiSession;

/**
 *
 * @author bdahl
 */

@Controller
@Scope(value = "session")
public class LtiController {
    private static final Logger LOG = LoggerFactory.getLogger(LtiController.class);
    
    @Autowired
    ICourseService courseService;
    
    @Autowired
    GitHubService githubService;
    
    @Autowired
    IAssignmentService assignmentService;
    
    /**
     * The LTI assignment entrypoint. Create a new LTI session with the launch
     * data provided, invalidating any previous session, then display the
     * assignment
     * 
     * @param ltiData the LTI parameters sent with the request
     * @param assignmentId the assignment to display
     * @param auth The current user's authentication
     * @param request the current request
     * 
     * @return The view constructed from the request and launch data
     */
    @PostMapping("/lti/assignment/{aid}")
    public ModelAndView ltiAssignment(@ModelAttribute LtiLaunchData ltiData,
                                      @PathVariable("aid") Long assignmentId,
                                      Authentication auth,
                                      HttpServletRequest request) {
 
        LtiSession ltiSession = buildLtiSession(ltiData, request);
        
        if (ltiSession == null || ltiSession.getLtiLaunchData() == null) {
            if (ltiSession == null) {
                LOG.info("no Lti Session");
            }
            else {
                LOG.info("invalid LTI Session");
            }
            return new ErrorView(HttpStatus.BAD_REQUEST, "No LTI Session!");
        }
        
        String courseKey = ((ConsumerCredentials)auth.getPrincipal()).getConsumerKey();
        
        Assignment a = assignmentService.findById(assignmentId);
        
        if (a == null || !courseKey.equals(a.getCourse().getKey())) {
            return new ErrorView(HttpStatus.UNAUTHORIZED, 
                    "That assignment does not exist or you are not authorized to view it");
        }
        
        List<InstitutionRole> authRoles = ltiSession.getLtiLaunchData().getRolesList();
        
        ModelAndView mv;
        
        if (authRoles.contains(InstitutionRole.Learner)) {
            // Show students their copy of the assignment -or- allow them to create one
            mv = new MessageView("LTI: " + ltiSession.getLtiLaunchData().getRoles(), "", 0);
        }
        else if (authRoles.contains(InstitutionRole.Instructor) ||
                 authRoles.contains(InstitutionRole.ContentDeveloper) ||
                 authRoles.contains(InstitutionRole.Creator)) {
            // Show the original repo in instructor view
            
            mv = new ModelAndView("repoInstructor");
            
            try {
                String repoName = a.getSourceRepoName();
                RepoDetailedInfo repo = new RepoDetailedInfo(githubService.getRepo(repoName));

                mv.addObject("repo", repo);
            }
            catch (IOException IGNORED) {
                
            }
        }
        else {
            return new ErrorView (HttpStatus.FORBIDDEN,
                "This view is not available to " + ltiSession.getLtiLaunchData().getRoles());
        }
        
        
        return mv;
    }
    
    /**
     * Create a new Lti session for the request
     * 
     * @param ltiData the LTI parameters sent with the request
     * @param request the current request
     * 
     * @return the LtiSession data 
     */
    private LtiSession buildLtiSession(LtiLaunchData ltiData, 
            HttpServletRequest request) {
               
        HttpSession session = request.getSession();
        
        session.invalidate();
        
        String eID = ltiData.getUser_id();
        LtiSession newLtiSession = new LtiSession();
        newLtiSession.setEid(eID);
        newLtiSession.setLtiLaunchData(ltiData);
        
        session = request.getSession(true);
        session.setAttribute("LtiSession", newLtiSession);
                
        LOG.info("launching LTI integration as user " + eID);
        
        return newLtiSession;
    }    
}
