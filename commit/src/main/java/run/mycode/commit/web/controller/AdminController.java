package run.mycode.commit.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import run.mycode.commit.persistence.service.IGitHubUserService;
import run.mycode.commit.web.dto.OpStatus;

/**
 * Admin Rest API controller
 * @author bdahl
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private IGitHubUserService userService;
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value="/user/{username}")
    @ResponseBody
    public OpStatus deleteUser(@PathVariable("username") String username) {
        if (userService.deleteUser(username)) {
            return new OpStatus("delete", username, "deleted");
        }
        else {
            return new OpStatus("delete", username, "failed");
        }
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value="/user/{username}/enable")
    @ResponseBody
    public OpStatus enableUser(@PathVariable("username") String username) {
        if (userService.enableUser(username)) {
            return new OpStatus("enable", username, "enabled");
        }
        else {
            return new OpStatus("enable", username, "failed");
        }
    }
}
