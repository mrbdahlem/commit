package run.mycode.commit.web.dto;

import java.io.IOException;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GHOrganization;

/**
 * A DTO for supplying organization information to clients
 * @author bdahl
 */
@Data
@NoArgsConstructor
public class OrgInfo implements Serializable {
    private long id;
    private String name;
    private String shortName;
    
    public OrgInfo(GHOrganization org) {
        this.id = org.getId();
        this.shortName = org.getLogin();
        
        String longName = null;
        try {
            longName = org.getName();
        }
        catch (IOException IGNORED) { }
        
        if (longName == null) {
            this.name = this.shortName;
        }
        else {
            this.name = longName;
        }
    }
}
