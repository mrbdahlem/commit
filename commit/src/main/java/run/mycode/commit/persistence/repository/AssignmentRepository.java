package run.mycode.commit.persistence.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import run.mycode.commit.persistence.model.Assignment;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;

/**
 *
 * @author bdahl
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Long>  {
    public Set<Assignment> findByCourse(Course course);
    public Set<Assignment> findByOwner(GitHubUser owner);
    
}
