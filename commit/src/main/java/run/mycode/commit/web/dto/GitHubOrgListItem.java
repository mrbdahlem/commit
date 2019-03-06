package run.mycode.commit.web.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class GitHubOrgListItem implements Serializable {
    private String login;
    private int id;
    private String node_id;
    private String url;
    private String repos_url;
    private String description;
}
