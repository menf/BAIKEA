package com.bai.services;

import com.bai.models.User;
import com.bai.repositories.InvalidLoginRepository;
import com.bai.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvalidLoginRepository invalidLoginRepository;

    public User authenticate(String username, String password) {
        Optional<User> userResult = userRepository.findUserByName(username);
        if (!userResult.isPresent())
            return null;
        User user = userResult.get();
        if (password.equals(user.getPassword()))
            return user;
        return null;
    }

    public User findByName(String username) {
        Optional<User> userResult = userRepository.findUserByName(username);
        return userResult.isPresent() ? userResult.get() : null;
    }

    public User createOrUpdate(String username, String password) {
        User user = new User(username, password);
        return userRepository.save(user);
    }

    public User createOrUpdate(User user) {
        return userRepository.save(user);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public InvalidLoginRepository getInvalidLoginRepository() {
        return invalidLoginRepository;
    }

    public void setInvalidLoginRepository(InvalidLoginRepository invalidLoginRepository) {
        this.invalidLoginRepository = invalidLoginRepository;
    }
}
