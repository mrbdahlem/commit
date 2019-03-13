package run.mycode.commit.web.controller;

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
    
    @PostMapping("/course/create")
    public String createCourse(@RequestParam("courseName") String courseName,
            Authentication auth) {
        GitHubUser owner = (GitHubUser)auth.getPrincipal();
        Course newCourse = courseService.createCourse(courseName, owner);
        
        return "redirect:/courses/" + newCourse.getKey();
    }
    
    @Transactional
    @GetMapping("/courses/{cid}")
    public ModelAndView courseInfo(@PathVariable("cid") String courseId,
                                    Authentication auth) {
        ModelAndView view = new ModelAndView("course");
        
        Course c = courseService.getByKey(courseId);
        view.addObject("course", c);
        
        GitHubUser owner = c.getOwner();
        view.addObject("orgs", gitHubService.getOrgs(owner));
        
        view.addObject("userIsOwner", ((GitHubUser)auth.getPrincipal()).getId().equals(owner.getId()));
        view.setStatus(HttpStatus.OK);
        return view;
    }
    
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
            System.out.println(formParams);
            view = new ModelAndView("redirect:./" + courseId + "/updated");
            
            if (formParams.getFirst("courseSecret") != null)
                c.setSharedSecret(formParams.getFirst(courseId));
            
            if (formParams.getFirst("courseName") != null)
                c.setName(formParams.getFirst("courseName"));
            
            if (formParams.getFirst("courseOrganization") != null)
                c.setDefaultAssignmentOrganization(formParams.getFirst("courseOrganization"));
            
            if (formParams.getFirst("studentOrganization") != null)
                c.setStudentOrganization(formParams.getFirst("studentOrganization"));

            c = courseService.update(c);
            System.out.println(c);
        }
        else {
            view = new ModelAndView("error");
            view.addObject("message", "No authorization to update course");
            view.setStatus(HttpStatus.UNAUTHORIZED);
        }
        
        return view;
    }
    
    @GetMapping("/courses/{cid}/updated")
    public ModelAndView courseUpdatedMessage(@PathVariable("cid") String courseId) {
        ModelAndView view = new ModelAndView("courseUpdated");
        view.addObject("courseId", courseId);
        return view;
    }
}
