package run.mycode.commit.persistence.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kohsuke.github.GHUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import run.mycode.commit.persistence.util.Identifiable;

/**
 * A web-login user of the commit program
 * 
 * @author bdahl
 */
@Data
@Entity
@Table(name="user")
public class GitHubUser implements OAuth2User, UserDetails, Identifiable<Long>, Serializable {
    @Id
    @Column(unique=true)
    private Long id;        // User id comes from github
    
    @Column(length = 250)
    private String name;
    
    @Column(length = 39)
    private String githubUsername;
    
    @Column(length = 250)
    private String email;
    
    private boolean enabled;
    
    @Column(name="roles")
    @EqualsAndHashCode.Exclude
    private String roleString;
        
    private String githubToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roleString);
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", this.getId());
        attributes.put("name", this.getName());
        attributes.put("login", this.getGithubUsername());
        attributes.put("email", this.getEmail());
        
        return attributes;
    }
    
    /**
     * Copy data into the user
     * @param other the user data to merge into this user
     * @throws java.io.IOException if downloading the other user's info cannot be
     *                             completed
     */
    public void merge(GHUser other) throws IOException {
        this.id = other.getId();
        this.name = other.getName();
        this.githubUsername = other.getLogin();
        this.email = other.getEmail();
    }
    
    public void setLogin(String name) {
        this.githubUsername = name;
    }
    
    public String getLogin() {
        return this.githubUsername;
    }

    @Override
    public String getPassword() {
        return this.githubToken;
    }

    @Override
    public String getUsername() {
        return this.githubUsername;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}