package run.mycode.commit.persistence.dto;

import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ColumnDefault;

import lombok.Data;

@Data
@Entity
public class User {
    public enum Role {INSTRUCTOR, ADMIN};
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(length = 250)
    private String name;
    
    @NotNull
    @Column(length = 39)
    private String githubUsername;
    
    @NotNull
    @Column(length = 250)
    private String email;
    
    private boolean enabled;
    
    @Column(name="roles")
    private String roleString;
    
    @Transient
    public List<Role> roles;
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Course> courses;
    
    private String githubToken;
}