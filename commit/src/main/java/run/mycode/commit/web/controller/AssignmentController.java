package run.mycode.commit.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
@Scope(value = "session")
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
     *
     * @param assignmentName The name of the assignment to create
     * @param courseKey The identifier for the course to create the assignment
     * for
     * @param auth The authentication of the current user, will become
     * assignment owner
     * @return a redirect to the course edit url
     */
    @PostMapping("/assignment/create")
    public ModelAndView createCourse(@RequestParam("assignmentName") String assignmentName,
            @RequestParam("courseKey") String courseKey,
            Authentication auth) {

        // Create the course with the current user as its owner
        GitHubUser owner = (GitHubUser) auth.getPrincipal();
        Course course = courseService.getByKey(courseKey);

        if (course == null || !owner.getId().equals(course.getOwner().getId())) {
            return new ErrorView(HttpStatus.UNAUTHORIZED,
                    "You don't have permission to add assignments this course.");
        }

        Assignment newAssignment = assignmentService.createAssignment(assignmentName, owner, course);

        LOG.debug("Assignment " + assignmentName + " created for "
                + owner.getLogin() + " in " + course.getName());

        // Redirect the user's browser to display the new course
        return new ModelAndView("redirect:/assignment/" + newAssignment.getId() + "/edit");
    }

    @GetMapping("/assignment/{aid}/edit")
    public ModelAndView editCourse(@PathVariable("aid") Long assignmentId,
            Authentication auth,
            HttpServletRequest request) {
        
        GitHubUser user = (GitHubUser) auth.getPrincipal();

        ModelAndView view = new ModelAndView("assignmentEdit");

        Assignment a = assignmentService.findById(assignmentId);
        Course c = a.getCourse();

        view.addObject("assignment", a);
        view.addObject("owner", a.getOwner());
        view.addObject("course", a.getCourse());

        view.addObject("baseURL", getAppUrl(request));

        view.addObject("userIsOwner", user.getId().equals(a.getOwner().getId()));

        return view;
    }

    @PostMapping(value = "/assignment/{aid}/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView updateCourse(@PathVariable("aid") Long assignmentId,
            @RequestBody MultiValueMap<String, String> formParams,
            Authentication auth,
            HttpServletRequest request) {

        GitHubUser user = (GitHubUser) auth.getPrincipal();

        Assignment a = assignmentService.findById(assignmentId);

        if (!user.getId().equals(a.getOwner().getId())) {
            return new ErrorView(HttpStatus.UNAUTHORIZED,
                    "No authorization to update course");
        }

        // Only update data if it the params are not null and not empty
        String fp = formParams.getFirst("assignmentName");
        if (fp != null && !fp.trim().isEmpty()) {
            a.setName(fp.trim());
        }

        fp = formParams.getFirst("sourceRepo");
        if (fp != null && !fp.trim().isEmpty()) {
            a.setSourceRepoName(fp.trim());
        }

        // Boolean (checkbox) values are present only if they are true
        fp = formParams.getFirst("allowStudentSubmissions");
        a.setAllowStudentSubmissions(fp != null);

        fp = formParams.getFirst("studentMadeAdmin");
        a.setStudentMadeAdmin(fp != null);

        fp = formParams.getFirst("makePrivate");
        a.setMakePrivate(fp != null);

        fp = formParams.getFirst("issuesEnabled");
        a.setIssuesEnabled(fp != null);

        fp = formParams.getFirst("wikiEnabled");
        a.setWikiEnabled(fp != null);

        assignmentService.update(a);

        ModelAndView view = new ModelAndView("assignmentEdit");

        view.addObject("message", "Updated");
        view.addObject("assignment", a);
        view.addObject("owner", a.getOwner());
        view.addObject("course", a.getCourse());

        view.addObject("baseURL", getAppUrl(request));

        view.addObject("userIsOwner", user.getId().equals(a.getOwner().getId()));

        return view;
    }

    private static String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName();
    }
}
