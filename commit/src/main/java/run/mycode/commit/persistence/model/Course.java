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

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import run.mycode.commit.persistence.util.Identifiable;

/**
 * The representation of a teacher's course connection between an LTI consumer
 * and GitHub
 * 
 * @author bdahl
 */
@Data
@Entity
public class Course implements Identifiable<String>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ulid")
    @GenericGenerator(
        name = "ulid", 
        strategy = "run.mycode.commit.persistence.util.UlidGenerator"
        )
    @Column(name = "id", length=26)
    private String key;
    
    private String sharedSecret;

    @ManyToOne(cascade = CascadeType.PERSIST,
               fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", nullable=false)
    private GitHubUser owner;

    /**
     * Course name
     */
    private String name;

    /**
     * Default GitHub Organization to find assignments
     */
    private String defaultAssignmentOrganization;
    
    /**
     * GitHub Organization which will host student repositories
     */
    private String studentOrganization;
    
    /**
     * Has this course been deleted?
     */
    @Column(columnDefinition="tinyint(1) default 0")
    private boolean deleted;

    @Override
    public String getId() {
        return getKey();
    }
}