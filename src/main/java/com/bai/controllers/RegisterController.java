package com.bai.controllers;

import com.bai.models.CreateUserForm;
import com.bai.models.User;
import com.bai.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String registerView(Model model) {
        model.addAttribute("createForm", new CreateUserForm());
        return "register";
    }

    @RequestMapping(value = "/submit", method = RequestMethod.GET)
    public String registerSubmit(@ModelAttribute CreateUserForm createForm, Model model) {
        User userResult = userService.findByName(createForm.getUsername());
        model.addAttribute("createForm", createForm);
        if (userResult != null) {
            model.addAttribute("invalidData", true);
            model.addAttribute("errorMessage", "User already exists");
            return "/register";
        }
        int passwordLength = createForm.getPassword().length();
        if (!(passwordLength >= 8 && passwordLength <= 16)) {
            model.addAttribute("invalidData", true);
            model.addAttribute("errorMessage", "Password length must be 8-16 characters.");
            return "/register";
        }
        if (!createForm.getPassword().equals(createForm.getPasswordConfirm())) {
            model.addAttribute("invalidData", true);
            model.addAttribute("errorMessage", "Passwords must match.");
            return "/register";
        }
        userService.createUser(createForm.getUsername(), createForm.getPassword());
        return "redirect:/login";
    }

}
