package run.mycode.commit.persistence.dto;


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

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ulid")
    @GenericGenerator(
        name = "ulid", 
        strategy = "run.mycode.commit.persistence.generator.UlidGenerator"
        )
    @Column(name = "id", length=26)
    private String key;
    
    @NotNull
    private String sharedSecret;

    @ManyToOne(cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    @NotNull
    private User owner;

    private String name;

    private String githubOrganization;
}