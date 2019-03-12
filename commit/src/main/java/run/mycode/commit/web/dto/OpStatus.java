package run.mycode.commit.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Transfer object to report operation status to the web frontend
 * 
 * @author bdahl
 */
@Data
@AllArgsConstructor
public class OpStatus {
    private final String operation;
    
    private final String selector;
    
    private final String status;
}
