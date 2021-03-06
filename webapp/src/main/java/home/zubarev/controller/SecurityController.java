package home.zubarev.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
public class SecurityController {
    @RequestMapping(value = "/login")
    public String login(){
        return "login";
    }
    @RequestMapping(value = "/access-denied")
    public String accessDenied(Principal user, Model model){
        if (user != null){
            String message = "Hi " + user.getName() + ", you do not have permission to access this page!";
            model.addAttribute("message", message);
        }else {
            model.addAttribute("message", "You do not have permission to access this page!");
        }
        return "access-denied";
    }
    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login?logout";
    }
}
