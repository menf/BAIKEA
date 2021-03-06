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

import javax.management.InvalidAttributeValueException;
import javax.naming.AuthenticationException;
import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bai.utils.SessionUtil.session;

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
        message.setModified(LocalDateTime.now());
        messageService.create(message);
        return "redirect:/messages";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
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
        List<AllowedMessages> allowedMessages = messageService.getAllowedMessagesRepository().findAllByMessageId(messageId);
        for (AllowedMessages allowedMessage : allowedMessages) {
            messageService.getAllowedMessagesRepository().delete(allowedMessage);
        }
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
        User loggedUser = (User) session().getAttribute("loggedUser");
        if (loggedUser == null)
            throw new NoPermissionException("Log in to edit messages");
        Optional<User> userResult = messageService.getUserRepository().findById(editMessageForm.getUserId());
        if (!userResult.isPresent())
            throw new NoPermissionException("User with id = " + editMessageForm.getUserId() + "does not exist.");
        Optional<Message> messageResult = messageService.getMessageRepository().findById(editMessageForm.getMessageId());
        if (!messageResult.isPresent())
            throw new InvalidAttributeValueException("Message with id = " + editMessageForm.getMessageId() + " does not exist.");
        Message message = messageResult.get();
        User user = userResult.get();
        List<Message> allowedMessages = messageService.findAllowedMessages(loggedUser.getId());
        if (!allowedMessages.contains(message) && message.getUser().getId() != loggedUser.getId())
            throw new NoPermissionException("No edit permissions!");
        if (message.getUser().getId() != loggedUser.getId() && editMessageForm.getAllowedUserId().length > 0) {
            throw new NoPermissionException("Only owner can add permissions");
        }
        if (editMessageForm.getAllowedUserId() != null) {
            for (int allowFor : editMessageForm.getAllowedUserId()) {
                Optional<User> addUserResult = messageService.getUserRepository().findById(allowFor);
                if (!addUserResult.isPresent())
                    throw new InvalidAttributeValueException("User with id = " + allowFor + " does not exist.");
                User addUser = addUserResult.get();
                AllowedMessages allowedMessagesToAdd;
                Optional<AllowedMessages> revokeMessage = messageService.getAllowedMessagesRepository().findByUserIdAndMessageId(allowFor, message.getId());
                if (revokeMessage.isPresent()) {
                    allowedMessagesToAdd = revokeMessage.get();
                    messageService.getAllowedMessagesRepository().delete(allowedMessagesToAdd);
                } else {
                    allowedMessagesToAdd = new AllowedMessages(addUser, message);
                    messageService.getAllowedMessagesRepository().save(allowedMessagesToAdd);
                }
            }
        }
        message.setText(editMessageForm.getMessageText());
        message.setModified(LocalDateTime.now());
        messageService.getMessageRepository().save(message);
        return "redirect:/messages";
    }


}
