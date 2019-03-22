package run.mycode.commit.persistence.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import org.kohsuke.github.GHOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mycode.commit.persistence.model.OrgName;
import run.mycode.commit.persistence.repository.OrgNameRepository;


@Service
public class OrgNameService implements IOrgNameService {
    private static final Logger LOG = LoggerFactory.getLogger(OrgNameService.class);
    
    @Autowired
    OrgNameRepository nameRepo;
    
    /**
     * Get the stored name of a github organization by its id
     * @param id
     * @return 
     */
    @Override
    public String getOrgName(long id) {
        Optional<OrgName> on = nameRepo.findById(id);
        if (on.isPresent()) {
            return on.get().getName();
        }
        else {
            return null;
        }
    }

    /**
     * Save/Update the name of a github organization
     * @param id
     * @param name 
     */
    @Override
    public void setOrgName(long id, String name) {
        OrgName org = nameRepo.findById(id).orElse(new OrgName(id));
        
        if (name == null || name.equals(org.getName())) {
            return;
        }
        
        LOG.debug("Updating org " + id + " name \"" + name + "\"");
        org.setName(name);
        nameRepo.save(org);
    }

    @Override
    public void updateAll(Collection<GHOrganization> orgs) {
        orgs.forEach(org -> setOrgName(org.getId(), org.getLogin()));
    }

    @Override
    public OrgName getOrg(long id) {
        return nameRepo.findById(id).orElse(null);
    }
}
