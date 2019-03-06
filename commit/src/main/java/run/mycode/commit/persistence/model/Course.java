package run.mycode.commit.persistence.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

/**
 * The representation of a teacher's course connection between an LTI consumer
 * and GitHub
 * 
 * @author bdahl
 */
@Data
@Entity
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ulid")
    @GenericGenerator(
        name = "ulid", 
        strategy = "run.mycode.commit.persistence.generator.UlidGenerator"
        )
    @Column(name = "id", length=26)
    private String key;
    
    private String sharedSecret;

    @ManyToOne(cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private GitHubUser owner;

    private String name;

    private String githubOrganization;
}