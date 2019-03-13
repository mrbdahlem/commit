package run.mycode.commit.persistence.service;

import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mycode.commit.persistence.generator.Ulid;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.repository.CourseRepository;
import run.mycode.commit.persistence.repository.GitHubUserRepository;

/**
 * A service for creating and accessing courses to interact with GitHub
 * 
 * @author bdahl
 */
@Service
public class CourseService implements ICourseService {
    @Autowired
    CourseRepository courseRepo;
    
    @Autowired
    GitHubUserRepository userRepo;
    
    @Transactional
    @Override
    public Course createCourse(String name, GitHubUser owner) {
        String key = Ulid.generate();
        courseRepo.createCourse(key, name, owner.getId());
        
        return courseRepo.findById(key).orElse(null);
    }

    @Override
    public Set<Course> findByOwner(GitHubUser owner) {
        return courseRepo.findByOwner(owner);
    }
}
