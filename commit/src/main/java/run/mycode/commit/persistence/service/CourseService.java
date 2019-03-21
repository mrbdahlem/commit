package run.mycode.commit.persistence.service;

import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mycode.commit.persistence.util.Ulid;
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
        String secret = UUID.randomUUID().toString();
        courseRepo.createCourse(key, name, owner.getId(), secret);
        
        return courseRepo.findById(key).orElse(null);
    }

    @Override
    public Set<Course> findByOwner(GitHubUser owner) {
        return courseRepo.findByOwner(owner);
    }

    @Override
    public Set<Course> findByOwnerNotDeleted(GitHubUser owner) {
        return courseRepo.findByOwnerNotDeleted(owner);
    }

    @Override
    public Course getByKey(String key) {
        return courseRepo.findById(key).orElse(null);
    }

    @Override
    public Course update(Course c) {
        return courseRepo.save(c);
    }
}
