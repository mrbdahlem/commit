package run.mycode.commit.web.controller;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import run.mycode.commit.web.util.ErrorView;
import run.mycode.lti.launch.model.LtiLaunchData;
import run.mycode.lti.launch.model.LtiSession;

/**
 *
 * @author bdahl
 */

@Controller
@Scope(value = "session")
public class LtiController {
    private static final Logger LOG = LoggerFactory.getLogger(LtiController.class);
    
    @Autowired
    private LtiSession ltiSession;
    
    @RequestMapping("/lti/**")
    public ModelAndView ltiLaunch() {
        if (ltiSession == null || ltiSession.getLtiLaunchData() == null) {
            if (ltiSession == null) {
                LOG.info("no Lti Session");
            }
            else {
                LOG.info("invalid LTI Session");
            }
            return new ErrorView(HttpStatus.FORBIDDEN, "No LTI Session!");
        }
        
        ModelAndView mv = new ModelAndView("message");
        mv.addObject("message", "LTI!");
        return mv;
    }
    
    @Bean
    @Scope(value="session")
    public LtiSession buildLtiSession(@ModelAttribute LtiLaunchData ltiData, HttpSession session) {
        session.invalidate();
        
        LOG.debug("launch!");
        String eID = ltiData.getUser_id();
        LtiSession newLtiSession = new LtiSession();
        newLtiSession.setEid(eID);
        newLtiSession.setLtiLaunchData(ltiData);
        
        System.out.println(ltiData);
        
//        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpSession newSession = sra.getRequest().getSession();
//        newSession.setAttribute(LtiSession.class.getName(), newLtiSession);
        LOG.info("launching LTI integration as user " + eID);
        
        return newLtiSession;
    }
}
