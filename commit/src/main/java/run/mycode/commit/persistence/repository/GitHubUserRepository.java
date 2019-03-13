package run.mycode.commit.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import run.mycode.commit.persistence.model.GitHubUser;

/**
 * Load/Save GitHub authenticated user information
 * @author bdahl
 */
@Repository
public interface GitHubUserRepository extends JpaRepository<GitHubUser, Long> { 
    public GitHubUser findByGithubUsername(String name);
    
    public List<GitHubUser> findByEnabled(boolean isEnabled);
}