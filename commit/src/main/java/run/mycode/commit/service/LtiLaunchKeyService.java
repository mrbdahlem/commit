package run.mycode.commit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.service.ICourseService;

/**
 * Retrieve the secret associated with a course
 * @author bdahl
 */
@Service
public class LtiLaunchKeyService implements run.mycode.lti.launch.service.LtiLaunchKeyService {
    
    @Autowired
    private ICourseService courseService;
    
    @Override
    public String findSecretForKey(String key) {
        Course course = courseService.getByKey(key);
        
        return course.getSharedSecret();
    }
    
}
