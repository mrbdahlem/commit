package run.mycode.commit.persistence.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.OneToMany;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * A web-login user of the commit program
 * 
 * @author bdahl
 */
@Data
@Entity
public class User implements OAuth2User, Serializable {
    @Transient
    private Map<String, Object> attributes;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(length = 250)
    private String name;
    
    @Column(length = 39)
    private String githubUsername;
    
    @Column(length = 250)
    private String email;
    
    private boolean enabled;
    
    @Column(name="roles")
    private String roleString;
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
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
    
    public void setAttributes(Map<String, Object> attributes) {
        this.id = ((Number)attributes.get("id")).longValue();
        this.name = (String)attributes.get("name");
        this.githubUsername = (String)attributes.get("login");
        this.email = (String)attributes.get("email");
        if (attributes.get("enabled") != null)
            this.enabled = (Boolean)attributes.get("enabled");
        else 
            this.enabled = false;
    }
    
    public void setLogin(String name) {
        this.githubUsername = name;
    }
    
    public String getLogin() {
        return this.githubUsername;
    }
}