package run.mycode.commit.web.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * A simple error message display view
 * 
 * @author bdahl
 */
public class ErrorView extends ModelAndView {
    public ErrorView(HttpStatus status) {
        super("error");
        this.setStatus(status);
        this.addObject("status", status.value());
        this.addObject("error", status.getReasonPhrase());
    }
    
    public ErrorView(HttpStatus status, String msg) {
        this(status);
        this.addObject("message", msg);
    }
}
