package run.mycode.commit.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import run.mycode.commit.persistence.model.Assignment;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.service.IAssignmentService;
import run.mycode.commit.persistence.service.ICourseService;
import run.mycode.commit.service.GitHubService;
import run.mycode.commit.web.util.ErrorView;

/**
 *
 * @author bdahl
 */
@Controller
@Scope(value="session")
public class AssignmentController {
    private static final Logger LOG = LoggerFactory.getLogger(AssignmentController.class);
    
    @Autowired
    private IAssignmentService assignmentService;
    
    @Autowired
    private ICourseService courseService;
    
    @Autowired
    private GitHubService gitHubService;
    
    
    /**
     * Allow a user to create a new assignment for a course
     * @param assignmentName The name of the assignment to create
     * @param courseKey The identifier for the course to create the assignment for
     * @param auth The authentication of the current user, will become assignment owner
     * @return a redirect to the course edit url
     */
    @PostMapping("/assignment/create")
    public ModelAndView createCourse(@RequestParam("assignmentName") String assignmentName,
                               @RequestParam("courseKey") String courseKey,
                               Authentication auth) {
    
        // Create the course with the current user as its owner
        GitHubUser owner = (GitHubUser)auth.getPrincipal();
        Course course = courseService.getByKey(courseKey);
        
        if (course == null || !owner.getId().equals(course.getOwner().getId())) {
            return new ErrorView(HttpStatus.UNAUTHORIZED, 
                                 "You don't have permission to add assignments this course.");
        }
        
        Assignment newAssignment = assignmentService.createAssignment(assignmentName, owner, course);
        
        LOG.debug("Assignment " + assignmentName + " created for " + 
                  owner.getLogin() + " in " + course.getName());
        
        // Redirect the user's browser to display the new course
        return new ModelAndView("redirect:/assignment/" + newAssignment.getId() + "/edit");
    }
    
    @GetMapping("/assignment/{aid}/edit")
    public ModelAndView editCourse(@PathVariable("aid") Long assignmentId,
                                   Authentication auth) {
        
        ModelAndView view = new ModelAndView("assignmentEdit");
        
        Assignment a = assignmentService.findById(assignmentId);
        view.addObject("assignment", a);
        view.addObject("owner", a.getOwner());
        view.addObject("course", a.getCourse());
        
        return view;
    }
}
