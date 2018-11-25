package com.bai.controllers;

import com.bai.models.AllowedMessages;
import com.bai.models.EditMessageForm;
import com.bai.models.Message;
import com.bai.models.User;
import com.bai.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.management.InvalidAttributeValueException;
import javax.naming.AuthenticationException;
import javax.naming.NoPermissionException;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(method = RequestMethod.GET)
    public String getMessages(Model model) {
        User user = (User) session().getAttribute("loggedUser");
        model.addAttribute("messages", messageService.findAll());
        model.addAttribute("isAdmin", false);
        if (user != null) {
            model.addAttribute("loggedUserId", user.getId());
            model.addAttribute("allowedMessages", messageService.findAllowedMessages(user.getId()));
        } else {
            model.addAttribute("loggedUserId", -1);
            model.addAttribute("allowedMessages", new ArrayList<>());
        }
        return "messages";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addMessages(@RequestParam("messageText") String messageText, Model model) throws AuthenticationException {
        User user = (User) session().getAttribute("loggedUser");
        if (user == null)
            throw new AuthenticationException("Log in to add messages");
        Message message = new Message(user, messageText);
        messageService.create(message);
        return "redirect:/messages";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public String removeMessage(@RequestParam("messageId") int messageId, Model model) throws InvalidAttributeValueException, NoPermissionException {
        User user = (User) session().getAttribute("loggedUser");
        if (user == null)
            throw new NoPermissionException("Log in to remove messages");
        Optional<Message> messageResults = messageService.getMessageRepository().findById(messageId);
        if (!messageResults.isPresent())
            throw new InvalidAttributeValueException("Message does not exist");
        Message message = messageResults.get();
        if (message.getUser().getId() != user.getId())
            throw new NoPermissionException("User " + user.getName() + " is not message owner.");
        messageService.delete(messageId);
        return "redirect:/messages";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editMessage(@RequestParam("messageId") int messageId, Model model) throws InvalidAttributeValueException, NoPermissionException {
        User user = (User) session().getAttribute("loggedUser");
        if (user == null)
            throw new NoPermissionException("Log in to edit messages");
        Optional<Message> messageResult = messageService.getMessageRepository().findById(messageId);
        if (!messageResult.isPresent())
            throw new InvalidAttributeValueException("Message with id = " + messageId + " does not exist.");
        Message message = messageResult.get();
        List<Message> allowedMessages = messageService.findAllowedMessages(user.getId());
        if (!allowedMessages.contains(message) && message.getUser().getId() != user.getId())
            throw new NoPermissionException("No edit permissions!");
        model.addAttribute("editMessageForm", new EditMessageForm(message.getId(), message.getText(), message.getUser().getId()));
        List<User> users = messageService.getUserRepository().findAll();
        users.removeIf(u -> u.getId() == user.getId());
        model.addAttribute("users", users);
        if (message.getUser().getId() == user.getId())
            model.addAttribute("isMessageOwner", true);
        return "messagesEditor";
    }

    @RequestMapping(value = "/edit/submit", method = RequestMethod.GET)
    public String editMessage(@ModelAttribute EditMessageForm editMessageForm,
                              Model model) throws NoPermissionException, InvalidAttributeValueException {
        Optional<User> userResult = messageService.getUserRepository().findById(editMessageForm.getUserId());
        if (!userResult.isPresent())
            throw new NoPermissionException("Log in to edit messages");
        Optional<Message> messageResult = messageService.getMessageRepository().findById(editMessageForm.getMessageId());
        if (!messageResult.isPresent())
            throw new InvalidAttributeValueException("Message with id = " + editMessageForm.getMessageId() + " does not exist.");
        Message message = messageResult.get();
        User user = userResult.get();
        if (message.getUser().getId() != user.getId() && editMessageForm.getAllowedUserId().length > 0) {
            throw new NoPermissionException("Only owner can add permissions");
        }
        for (int allowFor : editMessageForm.getAllowedUserId()) {
            Optional<User> addUserResult = messageService.getUserRepository().findById(allowFor);
            if (!addUserResult.isPresent())
                throw new InvalidAttributeValueException("User with id = " + allowFor + " does not exist.");
            User addUser = addUserResult.get();
            AllowedMessages allowedMessages;
            Optional<AllowedMessages> revokeMessage = messageService.getAllowedMessagesRepository().findByUserIdAndMessageId(allowFor, message.getId());
            if (revokeMessage.isPresent()) {
                allowedMessages = revokeMessage.get();
                messageService.getAllowedMessagesRepository().delete(allowedMessages);
            } else {
                allowedMessages = new AllowedMessages(addUser, message);
                messageService.getAllowedMessagesRepository().save(allowedMessages);
            }
        }
        message.setText(editMessageForm.getMessageText());
        messageService.getMessageRepository().save(message);
        return "redirect:/messages";
    }

    public static HttpSession session() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true); // true == allow create
    }

}
