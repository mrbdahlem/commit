package run.mycode.commit.persistence.util;

import java.io.Serializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class UlidGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(
                      SharedSessionContractImplementor session,
                      Object obj) {
             
        // If the object already has an id, return it
        if (obj instanceof Identifiable) {
            Identifiable identifiable = (Identifiable) obj;
            Serializable id = identifiable.getId();
 
            if (id != null) {
                return id;
            }
        }
 
        // Otherwise, return a new Ulid
        return Ulid.generate();
    }
    
}