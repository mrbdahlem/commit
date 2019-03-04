package run.mycode.commit.persistence.repository;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import run.mycode.commit.persistence.dto.GitHubUser;

/**
 * Load/Save GitHub authenticated user information
 * @author bdahl
 */
@Repository
@Transactional
public interface GitHubUserRepository extends JpaRepository<GitHubUser, Long> { 
    public GitHubUser findByGithubUsername(String name);
    
    public List<GitHubUser> findByEnabled(boolean isEnabled);
}