package run.mycode.commit.persistence.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.Data;
import run.mycode.commit.persistence.util.Identifiable;

/**
 * An Assignment representing a source repository allowing students to make their
 * own copies of the repo via LTI
 * 
 * @author bdahl
 */
@Data
@Entity
public class Assignment implements Identifiable<Long>, Serializable {
    /**
     * The database id for the assignment
     */
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    /**
     * The display name for the assignment
     */
    @NotNull
    private String name;
    
    /**
     * The owner (instructor) of this assignment
     */
    @ManyToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", nullable=false)
    private GitHubUser owner;
    
    /**
     * The course this assignment was created for
     */
    @ManyToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name="course_id", nullable=false)
    private Course course;
    
    /**
     * The GitHub name/path for the repository (orgOrUserName/repoName)
     */
    private String sourceRepoName;
    
    /**
     * Should student repos be created as private repos
     */
    private boolean makePrivate;
            
    /**
     * Should student repos have issue tracking enabled
     */
    private boolean issuesEnabled;
    
    /**
     * Should student repos have its wiki enabled
     */
    private boolean wikiEnabled;
    
    /**
     * Should students be made administrators of their repos
     */
    private boolean studentMadeAdmin;
}
