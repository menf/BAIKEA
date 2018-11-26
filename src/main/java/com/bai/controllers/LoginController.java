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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.bai.utils.SessionUtil.session;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        HttpSession session = session();
        if (session.getAttribute("loggedUser") != null)
            return "redirect:/user";
        return "login";
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String loginSubmit(@ModelAttribute LoginForm loginForm, Model model) {
        HttpSession session = session();
        User user = userService.authenticate(loginForm.getUsername(), loginForm.getPassword());
        Optional<User> userExists = userService.getUserRepository().findUserByName(loginForm.getUsername());
        if (isSessionLocked(model))
            return "login";
        if (user == null) {
            if (userExists.isPresent() && isAccountLocked(userExists.get(), model))
                return "login";
            return handleInvalidLoginAttempt(loginForm, model);
        }
        if (user.getInvalidLoginAttempts() > 0) {
            if (isAccountLocked(user, model))
                return "login";
        }
        session.setAttribute("invalidAttempts", user.getInvalidLoginAttempts());
        user.setInvalidLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        user = userService.createOrUpdate(user);
        session.setAttribute("loggedUser", user);
        session.setAttribute("loggedIn", true);
        session.setAttribute("sessionInvalidAttempts", 0);
        return "redirect:/messages";
    }

    private String handleInvalidLoginAttempt(LoginForm loginForm, Model model) {
        User user = userService.findByName(loginForm.getUsername());
        String errorMessage = "Wrong username or password!";
        int attemptsToLock = user == null ? 0 : user.getAttemptsToLock();
        if (user == null) {
            if (isSessionLocked(model))
                return "login";
            session().setAttribute("lastInvalidAttempt", LocalDateTime.now());
            int invalidAttempts = session().getAttribute("sessionInvalidAttempts") == null ? 0 : (int) session().getAttribute("sessionInvalidAttempts");
            invalidAttempts++;
            session().setAttribute("sessionInvalidAttempts", invalidAttempts);
            errorMessage = "Wrong username or password! Try again in " + getWaitDuration(invalidAttempts) + " seconds";
        } else if ((user.getInvalidLoginAttempts() <= attemptsToLock && attemptsToLock > 0) || user.getAttemptsToLock() == 0) {
            user.setInvalidLoginAttempts(user.getInvalidLoginAttempts() + 1);
            user.setLastInvalidLogin(LocalDateTime.now());
            user = userService.createOrUpdate(user);
            if (isAccountLocked(user, model))
                errorMessage = "Account locked, contact administrator!";
            else
                errorMessage = "Wrong username or password! Try again in " + getWaitDuration(user) + " seconds";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "login";
    }

    private boolean isAccountLocked(User user, Model model) {
        String errorMessage = "Account locked, contact administrator!";
        if (user.getInvalidLoginAttempts() < user.getAttemptsToLock() || user.getAttemptsToLock() == 0) {
            if (LocalDateTime.now().isAfter(user.getLastInvalidLogin().plusSeconds(getWaitTime(user))))
                return false;
            errorMessage = "Account locked. Try again in " + getWaitDuration(user) + " seconds";
        }
        model.addAttribute("errorMessage", errorMessage);
        return true;
    }

    private boolean isSessionLocked(Model model) {
        String errorMessage = "";
        int invalidAttempts = session().getAttribute("sessionInvalidAttempts") == null ? 0 : (int) session().getAttribute("sessionInvalidAttempts");
        LocalDateTime lastInvalidTry = (LocalDateTime) session().getAttribute("lastInvalidAttempt");
        if (lastInvalidTry == null)
            return false;
        if (invalidAttempts > 0) {
            if (LocalDateTime.now().isAfter(lastInvalidTry.plusSeconds(getWaitTime(invalidAttempts))))
                return false;
            errorMessage = "Account locked. Try again in " + getWaitDuration(invalidAttempts) + " seconds";
        }
        model.addAttribute("errorMessage", errorMessage);
        return true;
    }

    private long getWaitDuration(User user) {
        return Duration.between(LocalDateTime.now(), user.getLastInvalidLogin().plusSeconds(getWaitTime(user))).getSeconds();
    }

    private long getWaitDuration(int invalidAttempts) {
        return Duration.between(LocalDateTime.now(), ((LocalDateTime) session().getAttribute("lastInvalidAttempt")).plusSeconds(getWaitTime(invalidAttempts))).getSeconds();
    }

    private long getWaitTime(User user) {
        return user.getInvalidLoginAttempts() * 3;
    }

    private long getWaitTime(int invalidAttempts) {
        return invalidAttempts * 3;
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public String logOut() {
        HttpSession session = session();
        session.setAttribute("loggedUser", null);
        session.setAttribute("loggedIn", false);
        return "redirect:/login";
    }

}
