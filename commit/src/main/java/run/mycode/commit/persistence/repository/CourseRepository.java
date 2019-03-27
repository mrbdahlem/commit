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
    @Query(value="INSERT INTO course (id, name, owner_id, shared_secret) values (:key, :name, :ownerid, :secret)",
           nativeQuery = true)
    public void createCourse (@Param("key") String key, 
                              @Param("name") String name,
                              @Param("ownerid") Long owner_id,
                              @Param("secret") String secret);
    
    public Set<Course> findByOwner(GitHubUser owner);
    
    @Query(value="SELECT c FROM Course c WHERE c.owner = :owner and c.deleted = 0")
    public Set<Course> findByOwnerNotDeleted(@Param("owner") GitHubUser owner);
    
    @Query(value="SELECT u FROM GitHubUser u, Course c WHERE c.id = :key and u.id = c.owner")
    public GitHubUser getOwnerByKey(@Param("key") String key);
}
