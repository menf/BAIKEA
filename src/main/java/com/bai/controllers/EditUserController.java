package com.bai.controllers;

import com.bai.models.LoginForm;
import com.bai.models.PasswordChangeForm;
import com.bai.models.User;
import com.bai.models.UserAccountLockForm;
import com.bai.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

import static com.bai.utils.SessionUtil.session;

@Controller
@RequestMapping("/user")
public class EditUserController {
    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        boolean loggedIn = session().getAttribute("loggedIn") == null ? false : (boolean) session().getAttribute("loggedIn");
        User user = (User) session().getAttribute("loggedUser");
        if (loggedIn) {
            model.addAttribute("lockForm", new UserAccountLockForm(user.getAttemptsToLock()));
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
            return "user";
        }
        model.addAttribute("errorMessage", "Log in first!");
        model.addAttribute("loginForm", new LoginForm());
        HttpSession session = session();
        if (session.getAttribute("loggedUser") != null)
            model.addAttribute("isLoggedIn", true);
        return "login";
    }

    @RequestMapping(value = "passwordSubmit", method = RequestMethod.GET)
    public String changePassword(@ModelAttribute PasswordChangeForm passwordChangeForm, Model model) {
        boolean loggedIn = session().getAttribute("loggedIn") == null ? false : (boolean) session().getAttribute("loggedIn");
        User user = (User) session().getAttribute("loggedUser");
        if (!loggedIn) {
            model.addAttribute("errorMessage", "Log in first!");
            model.addAttribute("loginForm", new LoginForm());
            HttpSession session = session();
            if (session.getAttribute("loggedUser") != null)
                model.addAttribute("isLoggedIn", true);
            return "login";
        }
        if (!user.getPassword().equals(passwordChangeForm.getOldPassword()))
            model.addAttribute("errorMessage", "Incorrect password!");
        else if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getNewPasswordConfirm()))
            model.addAttribute("errorMessage", "Passwords must match!");
        else {
            user.setPassword(passwordChangeForm.getNewPassword());
            user = userService.updateUserPassword(user);
            session().setAttribute("loggedUser", user);
            model.addAttribute("successMessage", "Password changed!");
        }

        model.addAttribute("lockForm", new UserAccountLockForm(user.getAttemptsToLock()));
        model.addAttribute("passwordChangeForm", new PasswordChangeForm());
        return "user";
    }

    @RequestMapping(value = "/lockSubmit", method = RequestMethod.GET)
    public String changeLock(@ModelAttribute UserAccountLockForm userAccountLockForm, Model model) {
        boolean loggedIn = session().getAttribute("loggedIn") == null ? false : (boolean) session().getAttribute("loggedIn");
        User user = (User) session().getAttribute("loggedUser");
        if (!loggedIn) {
            model.addAttribute("errorMessage", "Log in first!");
            model.addAttribute("loginForm", new LoginForm());
            HttpSession session = session();
            if (session.getAttribute("loggedUser") != null)
                model.addAttribute("isLoggedIn", true);
            return "login";
        }
        if (userAccountLockForm.getAttemptsToLock() < 0) {
            model.addAttribute("errorMessage", "Invalid value!");
            model.addAttribute("lockForm", new UserAccountLockForm(user.getAttemptsToLock()));
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
            return "user";
        }
        user.setAttemptsToLock(userAccountLockForm.getAttemptsToLock());
        user = userService.updateUser(user);
        session().setAttribute("loggedUser", user);
        model.addAttribute("successMessage", "Invalid login attempts changed!");
        model.addAttribute("lockForm", new UserAccountLockForm(user.getAttemptsToLock()));
        model.addAttribute("passwordChangeForm", new PasswordChangeForm());
        return "user";
    }

}
