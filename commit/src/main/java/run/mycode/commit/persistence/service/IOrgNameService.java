package run.mycode.commit.persistence.service;

import java.util.Collection;
import org.kohsuke.github.GHOrganization;
import run.mycode.commit.persistence.model.OrgName;

/**
 *
 * @author bdahl
 */
public interface IOrgNameService {
    public String getOrgName(long id);
    
    public void setOrgName(long id, String name);
    
    public void updateAll(Collection<GHOrganization> orgs);
    
    public OrgName getOrg(long id);
}
