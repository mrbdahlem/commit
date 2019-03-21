package run.mycode.commit.web.controller;

import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.service.ICourseService;
import run.mycode.commit.persistence.service.IGitHubUserService;
import run.mycode.commit.service.GitHubService;
import run.mycode.commit.web.util.ErrorView;

/**
 *
 * @author bdahl
 */
@Controller
public class CourseController {
    @Autowired
    private ICourseService courseService;
    
    @Autowired
    private GitHubService gitHubService;
    
    @Autowired
    IGitHubUserService userService;
    
    /**
     * Allow a user to create a new lti course
     * @param courseName The name of the course to create
     * @param auth The authentication of the current user, will become course owner
     * @return a redirect to the course edit url
     */
    @PostMapping("/course/create")
    public String createCourse(@RequestParam("courseName") String courseName,
            Authentication auth) {
        
        // Create the course with the current user as its owner
        GitHubUser owner = (GitHubUser)auth.getPrincipal();
        Course newCourse = courseService.createCourse(courseName, owner);
        
        // Redirect the user's browser to display the new course
        return "redirect:/courses/" + newCourse.getKey();
    }
    
    /**
     * Display an lti course's information, to allow updating by course owner
     * @param courseId the course identifier
     * @param auth The current user, will allow updating the course if owner
     * @return a view to display the course
     */
    @Transactional
    @GetMapping("/courses/{cid}")
    public ModelAndView courseInfo(@PathVariable("cid") String courseId,
                                    Authentication auth) {
        ModelAndView view;
        
        
        // Lookup the course metadata based on the supplied id
        Course c = courseService.getByKey(courseId);
        
        // If there is no course with that id, display an error
        if (c == null) {
            view = new ModelAndView("error");
            view.setStatus(HttpStatus.NOT_FOUND);
            view.addObject("error", "Course not found");
            return view;
        }
        
        GitHubUser user = (GitHubUser)auth.getPrincipal();
        GitHubUser owner = c.getOwner();
        
        // If the user is not the course owner or an admin, don't display the
        // course
        if (!(user.getId().equals(owner.getId()) ||
              user.getRoleString().contains("ROLE_ADMIN"))) {
            view = new ErrorView(HttpStatus.UNAUTHORIZED, 
                                 "You don't have permission to view this course.");
            return view;        
        }
        
        // Otherwise, display the course's metadata
        view = new ModelAndView("course");
        view.addObject("course", c);
        
        // Add the user's accessible github organizations to allow updating
        view.addObject("orgs", gitHubService.getOrgs(owner));
        
        view.addObject("userIsOwner", user.getId().equals(owner.getId()));
        view.setStatus(HttpStatus.OK);
        
        return view;
    }
    
    /**
     * Update an lti course's information
     * @param courseId the course to update
     * @param formParams the data to use when updating
     * @param auth 
     * @return 
     */
    @Transactional
    @PostMapping(value="/courses/{cid}", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView editCourse(@PathVariable("cid") String courseId,
                                   @RequestBody MultiValueMap<String, String> formParams,
                                   Authentication auth) {
        ModelAndView view;
        
        Course c = courseService.getByKey(courseId);
        GitHubUser owner = c.getOwner();

        if (((GitHubUser)auth.getPrincipal()).getId().equals(owner.getId()) && 
                c.getKey().equals(formParams.getFirst("courseKey"))) {
            view = new ModelAndView("redirect:./" + courseId + "/updated");
            
            // Only update the shared secret if it is present and a valid UUID
            String cs = formParams.getFirst("courseSecret");
            if (cs != null && !cs.isEmpty()) {
                try{
                    UUID uuid = UUID.fromString(cs);
                    if (!c.getSharedSecret().equals(uuid.toString()))
                        c.setSharedSecret(uuid.toString());
                } catch (IllegalArgumentException IGNORED){
                    // ignore the case where string is not valid UUID 
                }
            }
            
            // Only update data if it the params are not null and not empty
            String cn = formParams.getFirst("courseName");
            if (cn != null && !cn.isEmpty())
                c.setName(cn);
            
            String cOrg = formParams.getFirst("courseOrganization"); 
            if (cOrg != null && !cOrg.isEmpty())
                c.setDefaultAssignmentOrganization(cOrg);
            
            String sOrg = formParams.getFirst("studentOrganization"); 
            if (sOrg != null && !sOrg.isEmpty())
                c.setStudentOrganization(sOrg);

            courseService.update(c);
        }
        else {
            view = new ModelAndView("error");
            view.addObject("message", "No authorization to update course");
            view.setStatus(HttpStatus.UNAUTHORIZED);
        }
        
        return view;
    }
    
    /**
     * Display a message that the course was updated
     * @param courseId the course that was updated
     * @return the informational view to display
     */
    @GetMapping("/courses/{cid}/updated")
    public ModelAndView courseUpdatedMessage(@PathVariable("cid") String courseId) {
        ModelAndView view = new ModelAndView("courseUpdated");
        view.addObject("courseId", courseId);
        return view;
    }
}
