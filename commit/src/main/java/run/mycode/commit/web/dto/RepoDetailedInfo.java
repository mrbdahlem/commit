package run.mycode.commit.web.dto;

import java.io.IOException;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GHRepository;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class RepoDetailedInfo extends RepoInfo {
    private Date lastUpdated;
    
    public RepoDetailedInfo(GHRepository repo) throws IOException {
        super(repo);
        
        this.lastUpdated = repo.getUpdatedAt();
    }
}