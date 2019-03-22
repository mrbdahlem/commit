package run.mycode.commit.web.controller;

import java.io.IOException;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.service.IAssignmentService;
import run.mycode.commit.persistence.service.ICourseService;
import run.mycode.commit.persistence.service.IOrgNameService;
import run.mycode.commit.service.GitHubService;
import run.mycode.commit.web.util.ErrorView;
import run.mycode.commit.web.util.MessageView;

/**
 *
 * @author bdahl
 */
@Controller
@Scope(value="session")
public class CourseController {
    private static final Logger LOG = LoggerFactory.getLogger(CourseController.class);
    
    @Autowired
    private ICourseService courseService;
    
    @Autowired
    private IAssignmentService assignmentService;
    
    @Autowired
    private GitHubService gitHubService;
    
    @Autowired
    private IOrgNameService orgNameService;
    
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
        
        LOG.debug("Course " + courseName + " created for " + owner.getLogin());
        
        // Redirect the user's browser to display the new course
        return "redirect:/course/" + newCourse.getKey() + "/edit";
    }
    
    /**
     * Display an lti course's information, to allow updating by course owner
     * @param courseId the course identifier
     * @param auth The current user, will allow updating the course if owner
     * @return a view to display the course
     */
    @Transactional
    @GetMapping("/course/{cid}/edit")
    public ModelAndView courseInfo(@PathVariable("cid") String courseId,
                                    Authentication auth) {
        // Lookup the course metadata based on the supplied id
        Course c = courseService.getByKey(courseId);
        
        // If there is no course with that id, display an error
        if (c == null) {
            return new ErrorView(HttpStatus.NOT_FOUND, "Course not found");
        }
        
        GitHubUser user = (GitHubUser)auth.getPrincipal();
        GitHubUser owner = c.getOwner();
        
        // If the user is not the course owner or an admin, don't display the
        // course
        if (!(user.getId().equals(owner.getId()) ||
              user.getRoleString().contains("ROLE_ADMIN"))) {
            return new ErrorView(HttpStatus.UNAUTHORIZED, 
                                 "You don't have permission to view this course.");
        }
        
        // Otherwise, display the course's metadata
        ModelAndView view = new ModelAndView("courseEdit");
        view.addObject("course", c);
        
        // Add the user's accessible github organizations to allow updating
        try {
            view.addObject("orgs", gitHubService.getOrgs());
        }
        catch (IOException IGNORED) {};
        
        view.addObject("userIsOwner", user.getId().equals(owner.getId()));
        
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
    @PostMapping(value="/course/{cid}/edit", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView editCourse(@PathVariable("cid") String courseId,
                                   @RequestBody MultiValueMap<String, String> formParams,
                                   Authentication auth) {
        
        GitHubUser user = (GitHubUser)auth.getPrincipal();
        
        Course c = courseService.getByKey(courseId);
        
        // If there is no course with that id, display an error
        if (c == null) {
            return new ErrorView(HttpStatus.NOT_FOUND, "Course not found");
        }
        
        GitHubUser owner = c.getOwner();

        if (!user.getId().equals(owner.getId()) || 
                !c.getKey().equals(formParams.getFirst("courseKey"))) {
            return new ErrorView(HttpStatus.UNAUTHORIZED,
                    "No authorization to update course");
        }
        else {
            
            // Only update the shared secret if it is present and a valid UUID
            String cs = formParams.getFirst("courseSecret");
            if (cs != null && !cs.isEmpty()) {
                try{
                    // use the canonical uuid format for the updated shared secret
                    UUID uuid = UUID.fromString(cs);
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
            if (cOrg != null) {
                if (cOrg.trim().isEmpty()) {
                    c.setDefaultAssignmentOrganization(null);
                }
                else {
                    c.setDefaultAssignmentOrganization(orgNameService.getOrg(Long.parseLong(cOrg)));
                }
            }
            
            String sOrg = formParams.getFirst("studentOrganization"); 
            if (sOrg != null) {
                if (sOrg.trim().isEmpty()) {
                    c.setStudentOrganization(null);
                }
                else {
                    c.setStudentOrganization(orgNameService.getOrg(Long.parseLong(sOrg)));
                }
            }
            
            courseService.update(c);
            
            LOG.debug("Course " + c.getName() + " updated for " + owner.getLogin());
            
            return new MessageView("Course Updated", "./edit");
        }
        
    }
    
    /**
     * Delete an lti course
     * @param courseId the course to update
     * @param auth 
     * @return 
     */
    @Transactional
    @DeleteMapping(value="/course/{cid}")
    public ModelAndView deleteCourse(@PathVariable("cid") String courseId,
                                        Authentication auth) {
        GitHubUser user = (GitHubUser)auth.getPrincipal();
        
        Course c = courseService.getByKey(courseId);
        
        // If there is no course with that id, display an error
        if (c == null) {
            return new ErrorView(HttpStatus.NOT_FOUND, "Course not found");
        }
        
        GitHubUser owner = c.getOwner();

        if (user.getId().equals(owner.getId())) {
            c.setDeleted(true);
            courseService.update(c);
            
            LOG.debug("Course " + c.getName() + " deleted for " + owner.getLogin());
            
            return new MessageView("Course Deleted", "/");
        }
        else {
            return new ErrorView(HttpStatus.UNAUTHORIZED, 
                                 "No authorization to delete course");
        }
    }
    
    @Transactional
    @GetMapping(value="/course/{cid}")
    public ModelAndView courseAssignments(@PathVariable("cid") String courseId,
                                        Authentication auth) {
        GitHubUser user = (GitHubUser)auth.getPrincipal();
        
        Course c = courseService.getByKey(courseId);
        
        // If there is no course with that id, display an error
        if (c == null) {
            return new ErrorView(HttpStatus.NOT_FOUND, "Course not found");
        }
        
        GitHubUser owner = c.getOwner();

        // If the user is not the course owner or an admin, don't display the
        // course
        if (!(user.getId().equals(owner.getId()) ||
              user.getRoleString().contains("ROLE_ADMIN"))) {
            return new ErrorView(HttpStatus.UNAUTHORIZED, 
                                 "You don't have permission to view this course.");
        }
        
        // Otherwise, display the course's metadata
        ModelAndView view = new ModelAndView("courseAssignments");
        view.addObject("course", c);
        
        // Add the course's assignment list
        view.addObject("assignmentList", assignmentService.findByCourse(c));
        
        view.addObject("userIsOwner", user.getId().equals(owner.getId()));
        
        return view;
    }
}
