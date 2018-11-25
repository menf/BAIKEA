package com.bai.services;

import com.bai.models.AllowedMessages;
import com.bai.models.Message;
import com.bai.models.User;
import com.bai.repositories.AllowedMessagesRepository;
import com.bai.repositories.MessageRepository;
import com.bai.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    AllowedMessagesRepository allowedMessagesRepository;
    @Autowired
    UserRepository userRepository;

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public List<Message> findAllowedMessages(int userId) {
        List<AllowedMessages> userAllowedMessages = allowedMessagesRepository.findByUserId(userId);
        List<Message> allowedMessages = new ArrayList<>();
        if (userAllowedMessages != null)
            allowedMessages.addAll(userAllowedMessages.stream().map(message -> message.getMessage()).collect(Collectors.toList()));
        return allowedMessages;
    }

    public Message create(Message message) {
        return messageRepository.save(message);
    }

    public Message update(Message message) {
        return messageRepository.save(message);
    }

    public void delete(int messageId) {
        messageRepository.deleteById(messageId);
    }

    public void revokeMessagePermissions(int userId, int messageId) {
        allowedMessagesRepository.deleteByUserIdAndMessageId(userId, messageId);
    }

    public void addMessagePermissions(int userId, int messageId) throws InvalidAttributeValueException, InstanceAlreadyExistsException {
        Optional<User> userResult = userRepository.findById(userId);
        if (!userResult.isPresent())
            throw new InvalidAttributeValueException("User with id = " + userId + " does not exist");
        Optional<Message> messageResult = messageRepository.findById(messageId);
        if (!messageResult.isPresent())
            throw new InvalidAttributeValueException("Message with id = " + messageId + " does not exist");
        Optional<AllowedMessages> allowedMessagesResult = allowedMessagesRepository.findByUserIdAndMessageId(userId, messageId);
        if (allowedMessagesResult.isPresent())
            throw new InstanceAlreadyExistsException("User with id = " + userId + " has access to message with id = " + messageId);
        AllowedMessages allowedMessages = new AllowedMessages(userResult.get(), messageResult.get());
        allowedMessagesRepository.save(allowedMessages);
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public AllowedMessagesRepository getAllowedMessagesRepository() {
        return allowedMessagesRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}

