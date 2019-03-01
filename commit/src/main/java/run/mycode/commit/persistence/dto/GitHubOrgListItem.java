package run.mycode.commit.persistence.dto;

import java.io.Serializable;

public class GitHubOrgListItem implements Serializable {
    private String login;
    private int id;
    private String node_id;
    private String url;
    private String repos_url;
    private String description;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(String reposUrl) {
        this.repos_url = reposUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
