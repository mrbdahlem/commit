package run.mycode.commit.persistence.service;

import java.util.Set;
import run.mycode.commit.persistence.model.Assignment;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;

/**
 *
 * @author bdahl
 */
public interface IAssignmentService {
    public Assignment createAssignment(String name, GitHubUser owner, Course course);
    
    public Set<Assignment> findByCourse(Course course);
    
    public Set<Assignment> findByOwner(GitHubUser owner);
    
    public Assignment update(Assignment a);
}
