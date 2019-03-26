package run.mycode.commit.web.dto;

import java.net.URL;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GHRepository;

/**
 *
 * @author bdahl
 */
@Data
@NoArgsConstructor
public class RepoInfo {
    private long id;
    private String name;
    private String fullName;
    private URL url;
    private String gitUrl; 
    private String description;
    
    
    public RepoInfo(GHRepository repo) {
        this.id = repo.getId();
        this.name = repo.getName();
        this.fullName = repo.getFullName();
        this.url = repo.getHtmlUrl();
        this.gitUrl = repo.getHttpTransportUrl();
        this.description = repo.getDescription();
    }
}
