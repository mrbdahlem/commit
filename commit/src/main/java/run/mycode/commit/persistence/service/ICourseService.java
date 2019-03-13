package run.mycode.commit.persistence.service;

import java.util.Set;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;

/**
 *
 * @author bdahl
 */
public interface ICourseService {
    
    /**
     * Create a course owned by a particular user
     * 
     * @param name the name of the course to create
     * @param owner the user who will own the course
     * 
     * @return the created course 
     */
    public Course createCourse(String name, GitHubUser owner);    
    
    public Course getByKey(String key);
    
    public Set<Course> findByOwner(GitHubUser owner);
    
    public Course update(Course c);
}
