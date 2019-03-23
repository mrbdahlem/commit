package run.mycode.commit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger LOG = LoggerFactory.getLogger(LtiLaunchKeyService.class);
    
    @Autowired
    private ICourseService courseService;
    
    @Override
    public String findSecretForKey(String key) {
        Course course = courseService.getByKey(key);
        
        if (course == null) {
            LOG.info("Invalid LTI course request " + key);
            return null;
        }
        else {
            LOG.info("Found secret for course " + key);
            return course.getSharedSecret();
        }

    }
    
}
