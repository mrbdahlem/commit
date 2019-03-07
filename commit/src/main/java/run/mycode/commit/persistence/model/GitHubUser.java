package run.mycode.commit.persistence.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * A web-login user of the commit program
 * 
 * @author bdahl
 */
@Data
@Entity
@Table(name="user")
public class GitHubUser implements OAuth2User, UserDetails, Serializable {
    @Transient
    @EqualsAndHashCode.Exclude
    private Map<String, Object> attributes;
    
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
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude 
    private List<Course> courses;
    
    private String githubToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roleString);
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (this.attributes == null) {
                this.attributes = new HashMap<>();
                this.attributes.put("id", this.getId());
                this.attributes.put("name", this.getName());
                this.attributes.put("login", this.getGithubUsername());
                this.attributes.put("email", this.getEmail());
                this.attributes.put("enabled", this.enabled);
        }
        return attributes;
    }
    
    /**
     * Copy data into the user
     * @param attributes 
     */
    public void setAttributes(Map<String, Object> attributes) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        
        this.attributes.putAll(attributes);
        
        if (attributes.containsKey("id"))
            this.id = ((Number)attributes.get("id")).longValue();

        if (attributes.containsKey("name"))
            this.name = (String)attributes.get("name");
        
        if (attributes.containsKey("login"))
            this.githubUsername = (String)attributes.get("login");
        
        if (attributes.containsKey("email"))
            this.email = (String)attributes.get("email");
        
        if (attributes.get("enabled") != null)
            this.enabled = (Boolean)attributes.get("enabled");
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