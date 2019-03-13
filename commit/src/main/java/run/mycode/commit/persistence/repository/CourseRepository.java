package run.mycode.commit.persistence.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;

/**
 *
 * @author bdahl
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    @Modifying
    @Query(value="INSERT INTO COURSE (id, name, owner_id) values (:key, :name, :ownerid)",
           nativeQuery =true)
    public void createCourse (@Param("key") String key, 
                              @Param("name") String name,
                              @Param("ownerid") Long owner_id);
    
    public Set<Course> findByOwner(GitHubUser owner);
}
