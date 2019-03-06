package run.mycode.commit.web.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *  A new course creation request transfer object
 * 
 *  @author bdahl
 */
@Data
public class NewCourseDTO {
    @NotNull
    @NotEmpty
    private String name;
}
