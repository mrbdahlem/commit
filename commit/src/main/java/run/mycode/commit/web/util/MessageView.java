/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run.mycode.commit.web.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author bdahl
 */
public class MessageView extends ModelAndView {
    public MessageView(String msg, String redirect) {
        super("message");
        this.setStatus(HttpStatus.OK);
        this.addObject("message", msg);
        this.addObject("redir", redirect);
    }
}