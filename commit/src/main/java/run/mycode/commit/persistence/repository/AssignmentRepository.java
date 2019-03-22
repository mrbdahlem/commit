package run.mycode.commit.persistence.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    @Modifying
    @Query(value="UPDATE assignment set owner_id = :ownerid , course_id = :courseid WHERE id = :assignmentid",
           nativeQuery = true)
    public void setOwnerCourse (@Param("assignmentid") Long id, 
                                @Param("ownerid") Long owner_id,
                                @Param("courseid") String course_id);
}   
