package com.bai.controllers;

import com.bai.models.LoginForm;
import com.bai.models.User;
import com.bai.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        System.out.println("LOGINFORM");
        HttpSession session = MessagesController.session();
        if (session.getAttribute("loggedUser") != null)
            model.addAttribute("isLoggedIn", true);
        return "login";
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String loginSubmit(@ModelAttribute LoginForm loginForm, Model model) {
        System.out.println("### LOGINFORM IN IN");
        HttpSession session = MessagesController.session();
        User user = userService.authenticate(loginForm.getUsername(), loginForm.getPassword());
        if (user != null) {
            System.out.println("### LOGINFORM WEJSZLO");
            session.setAttribute("loggedUser", user);
            session.setAttribute("loggedIn", true);
            return "redirect:/messages";
        } else {
            System.out.println("### LOGINFORM NIE PYKLO");
        }
        System.out.println("Login successful");
        return "login";
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public String logOut(@ModelAttribute LoginForm loginForm, Model model) {
        HttpSession session = MessagesController.session();
        session.setAttribute("loggedUser", null);
        session.setAttribute("loggedIn", false);
        System.out.println("Logout successful");
        return "login";
    }

}
