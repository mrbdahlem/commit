package run.mycode.commit.persistence.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.mycode.commit.persistence.util.Identifiable;

/**
 * A mapping between GitHub org ids and names
 * @author bdahl
 */
@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class OrgName implements Identifiable<Long>, Serializable  {
    @Id
    @NonNull
    private Long id;
    private String name;
}
