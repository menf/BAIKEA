package com.bai.controllers;

import com.bai.models.*;
import com.bai.services.UserService;
import com.bai.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

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

    @RequestMapping(value = "/hash", method = RequestMethod.GET)
    public String hashLoginUserSelect(@ModelAttribute LoginHashForm loginHashForm, Model model) {
        HttpSession session = session();
        if (session.getAttribute("loggedUser") != null)
            return "redirect:/user";
        if (loginHashForm == null)
            loginHashForm = new LoginHashForm();
        Optional<User> userResult = userService.getUserRepository().findUserByName(loginHashForm.getUsername());
        if (!userResult.isPresent())
            loginHashForm.setMask(PasswordUtils.DEFAULT_MASK);
        else
            loginHashForm.setMask(userService.getUserCurrentMask(userResult.get()));
        model.addAttribute("loginHashForm", loginHashForm);
        return "loginHash";
    }

    @RequestMapping(value = "/hash/submit", method = RequestMethod.GET)
    @Transactional
    public String hashLoginSubmit(@ModelAttribute LoginHashForm loginHashForm, Model model) {
        HttpSession session = session();
        if (session.getAttribute("loggedUser") != null)
            return "redirect:/user";
        if (loginHashForm == null)
            loginHashForm = new LoginHashForm();
        Optional<User> userResult = userService.getUserRepository().findUserByName(loginHashForm.getUsername());
        if (!userResult.isPresent()) {
            loginHashForm.setMask(PasswordUtils.DEFAULT_MASK);
            handleInvalidLoginAttemptForNonExistentUser(new LoginForm(loginHashForm.getUsername()), model);
            model.addAttribute("loginHashForm", loginHashForm);
            return "loginHash";
        }
        loginHashForm.setMask(userService.getUserCurrentMask(userResult.get()));
        model.addAttribute("loginHashForm", loginHashForm);
        User user = userService.authenticateHashLogin(loginHashForm.getUsername(), loginHashForm.getPassword());
        if (user == null) {
            handleInvalidLoginAttemptForExistingUser(userResult.get(), model);
            return "loginHash";
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime unlockTime = user.getLastInvalidLogin().plusSeconds(user.getInvalidLoginAttempts() * 3);
        long timeToUnlock = Duration.between(now, unlockTime).getSeconds();
        if (now.isBefore(unlockTime)) {
            model.addAttribute("errorMessage", "Account locked. Try again in " + timeToUnlock + " seconds.");
            return "loginHash";
        }
        if (user.getInvalidLoginAttempts() >= user.getAttemptsToLock() && user.getAttemptsToLock() > 0) {
            model.addAttribute("errorMessage", "Account locked. Please contact administrator.");
            return "loginHash";
        }
        session.setAttribute("invalidAttempts", user.getInvalidLoginAttempts());
        session.setAttribute("lastLogin", user.getLastLogin());
        user.setInvalidLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        user = userService.updateUser(user);
        session.setAttribute("loggedUser", user);
        session.setAttribute("loggedIn", true);
        userService.updatePartialPasswordMask(user);
        return "redirect:/messages";
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String loginSubmit(@ModelAttribute LoginForm loginForm, Model model) {
        User user = userService.authenticate(loginForm.getUsername(), loginForm.getPassword());
        Optional<User> userExists = userService.getUserRepository().findUserByName(loginForm.getUsername());
        if (!userExists.isPresent()) {
            handleInvalidLoginAttemptForNonExistentUser(loginForm, model);
            return "login";
        }
        if (user == null) {
            handleInvalidLoginAttemptForExistingUser(userExists.get(), model);
            return "login";
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime unlockTime = user.getLastInvalidLogin().plusSeconds(user.getInvalidLoginAttempts() * 3);
        long timeToUnlock = Duration.between(now, unlockTime).getSeconds();
        if (now.isBefore(unlockTime)) {
            model.addAttribute("errorMessage", "Account locked. Try again in " + timeToUnlock + " seconds.");
            return "login";
        }
        if (user.getInvalidLoginAttempts() >= user.getAttemptsToLock() && user.getAttemptsToLock() > 0) {
            model.addAttribute("errorMessage", "Account locked. Please contact administrator.");
            return "login";
        }
        HttpSession session = session();
        session.setAttribute("invalidAttempts", user.getInvalidLoginAttempts());
        session.setAttribute("lastLogin", user.getLastLogin());
        user.setInvalidLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        user = userService.updateUser(user);
        session.setAttribute("loggedUser", user);
        session.setAttribute("loggedIn", true);
        return "redirect:/messages";
    }

    private void handleInvalidLoginAttemptForNonExistentUser(LoginForm loginForm, Model model) {
        Optional<InvalidLogin> invalidLoginResult = userService.getInvalidLoginRepository().findByUsername(loginForm.getUsername());
        InvalidLogin invalidLogin;
        if (!invalidLoginResult.isPresent()) {
            InvalidLogin newInvalidLogin = new InvalidLogin(loginForm.getUsername());
            int invalidAttempts = Math.abs(new Random().nextInt() % 5);
            newInvalidLogin.setLockAttempt(invalidAttempts);
            invalidLogin = logInvalidLoginAttempt(newInvalidLogin);
            long timeToUnlock = Duration.between(LocalDateTime.now(), invalidLogin.getLastAttempt().plusSeconds(invalidLogin.getAttempts() * 3)).getSeconds();
            model.addAttribute("errorMessage", "Invalid username or password. Try again in " + timeToUnlock + " seconds.");
            return;
        }
        invalidLogin = invalidLoginResult.get();
        long timeToUnlock = Duration.between(LocalDateTime.now(), invalidLogin.getLastAttempt().plusSeconds(invalidLogin.getAttempts() * 3)).getSeconds();
        if (timeToUnlock > 0) {
            model.addAttribute("errorMessage", "Account locked. Try again in " + timeToUnlock + " seconds.");
            return;
        }
        if (invalidLogin.getAttempts() >= invalidLogin.getLockAttempt() && invalidLogin.getLockAttempt() > 0) {
            model.addAttribute("errorMessage", "Account locked. Please contact administrator.");
            return;
        }
        invalidLogin = logInvalidLoginAttempt(invalidLogin);
        timeToUnlock = Duration.between(LocalDateTime.now(), invalidLogin.getLastAttempt().plusSeconds(invalidLogin.getAttempts() * 3)).getSeconds();
        model.addAttribute("errorMessage", "Invalid username or password. Try again in " + timeToUnlock + " seconds.");
    }

    private InvalidLogin logInvalidLoginAttempt(InvalidLogin invalidLogin) {
        invalidLogin.setLastAttempt(LocalDateTime.now());
        invalidLogin.setAttempts(invalidLogin.getAttempts() + 1);
        return userService.getInvalidLoginRepository().save(invalidLogin);
    }

    private void handleInvalidLoginAttemptForExistingUser(User user, Model model) {
        long timeToUnlock = Duration.between(LocalDateTime.now(), user.getLastInvalidLogin().plusSeconds(user.getInvalidLoginAttempts() * 3)).getSeconds();
        if (timeToUnlock > 0) {
            model.addAttribute("errorMessage", "Account locked. Try again in " + timeToUnlock + " seconds.");
            return;
        }
        if (user.getAttemptsToLock() > 0 && user.getInvalidLoginAttempts() >= user.getAttemptsToLock()) {
            model.addAttribute("errorMessage", "Account locked. Please contact administrator.");
            return;
        }
        user.setInvalidLoginAttempts(user.getInvalidLoginAttempts() + 1);
        user.setLastInvalidLogin(LocalDateTime.now());
        user = userService.getUserRepository().save(user);
        timeToUnlock = Duration.between(LocalDateTime.now(), user.getLastInvalidLogin().plusSeconds(user.getInvalidLoginAttempts() * 3)).getSeconds();
        model.addAttribute("errorMessage", "Invalid username or password. Try again in " + timeToUnlock + " seconds.");
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public String logOut() {
        HttpSession session = session();
        session.setAttribute("loggedUser", null);
        session.setAttribute("loggedIn", false);
        return "redirect:/login";
    }

}
