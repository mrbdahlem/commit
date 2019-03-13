package run.mycode.commit.web.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.service.ICourseService;
import run.mycode.commit.persistence.service.IGitHubUserService;

/**
 *
 * @author bdahl
 */
@Controller
public class CourseController {
    @Autowired
    ICourseService courseService;
    
    @Autowired
    IGitHubUserService userService;
    
    @PostMapping("/course/create")
    public String createCourse(@RequestParam("courseName") String courseName,
            Authentication auth) {
        GitHubUser owner = (GitHubUser)auth.getPrincipal();
        Course newCourse = courseService.createCourse(courseName, owner);
        
        return "redirect:/courses/" + newCourse.getKey();
    }
}
