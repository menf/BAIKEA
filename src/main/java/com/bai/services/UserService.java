package com.bai.services;

import com.bai.models.PartialPassword;
import com.bai.models.User;
import com.bai.repositories.InvalidLoginRepository;
import com.bai.repositories.PartialPasswordRepository;
import com.bai.repositories.UserRepository;
import com.bai.utils.PasswordUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvalidLoginRepository invalidLoginRepository;
    @Autowired
    private PartialPasswordRepository partialPasswordRepository;

    public User authenticate(String username, String password) {
        Optional<User> userResult = userRepository.findUserByName(username);
        if (!userResult.isPresent())
            return null;
        User user = userResult.get();
        if (password.equals(user.getPassword()))
            return user;
        return null;
    }

    @Transactional
    public User authenticateHashLogin(String username, String[] password) {
        Optional<User> userResult = userRepository.findUserByName(username);
        if (!userResult.isPresent())
            return null;
        User user = userResult.get();
        PartialPassword partialPassword = partialPasswordRepository.findByUserIdAndCurrentTrue(user.getId()).get();
        String pass = Arrays.stream(password).map(c -> {
            if (!StringUtils.isEmpty(c))
                return "" + c.charAt(0);
            else
                return "";
        }).collect(Collectors.joining());
        String hashedFragment = DigestUtils.md5Hex(pass + user.getSalt()).toUpperCase();
        if (!hashedFragment.equals(partialPassword.getPassword()))
            return null;
        return user;
    }

    public void updatePartialPasswordMask(User user) {
        PartialPassword partialPassword = partialPasswordRepository.findByUserIdAndCurrentTrue(user.getId()).get();
        try (Stream<PartialPassword> passwordStream = partialPasswordRepository.findByUserIdAndCurrentFalseOrderByLastUsedAsc(user.getId())) {
            PartialPassword nextPartialPassword = passwordStream.findFirst().get();
            nextPartialPassword.setCurrent(true);
            partialPassword.setCurrent(false);
            partialPassword.setLastUsed(LocalDateTime.now());
            partialPasswordRepository.save(partialPassword);
            partialPasswordRepository.save(nextPartialPassword);
        }
    }

    private boolean passwordMatchMask(String decodedPassword, String[] password, String mask) {
        boolean[] boolMask = PasswordUtils.maskToBooleanArray(mask);
        for (int i = 0; i < boolMask.length; i++)
            if (boolMask[i] && !password[i].equals("" + decodedPassword.charAt(i)))
                return false;
        return true;
    }

    public User findByName(String username) {
        Optional<User> userResult = userRepository.findUserByName(username);
        return userResult.orElse(null);
    }

    public User createUser(String username, String password) {
        User user = new User(username, password);
        user.setLastInvalidLogin(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setSalt(PasswordUtils.generateSalt());
        user.setPasswordHash(PasswordUtils.hashPassword(user));
        createPartialPasswords(user);
        return userRepository.save(user);
    }

    public boolean[] getUserCurrentMask(User user) {
        Optional<PartialPassword> partialPasswordResult = partialPasswordRepository.findByUserIdAndCurrentTrue(user.getId());
        return partialPasswordResult.isPresent() ? PasswordUtils.maskToBooleanArray(partialPasswordResult.get().getMask()) : PasswordUtils.DEFAULT_MASK;
    }

    public User updateUserPassword(User user) {
        partialPasswordRepository.deletePartialPasswordByUserId(user.getId());
        user.setSalt(PasswordUtils.generateSalt());
        user.setPasswordHash(PasswordUtils.hashPassword(user));
        createPartialPasswords(user);
        return userRepository.save(user);
    }

    private void createPartialPasswords(User user) {
        List<String> masks = PasswordUtils.generateMasksForUser(user, 10);
        List<PartialPassword> partialPasswords = masks.stream()
                .map(mask -> new PartialPassword(user, mask, LocalDateTime.now(), DigestUtils.md5Hex(PasswordUtils.getPartialPassword(user.getPassword(), mask) + user.getSalt()).toUpperCase()))
                .collect(Collectors.toList());
        partialPasswords.get(0).setCurrent(true);
        partialPasswordRepository.saveAll(partialPasswords);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public InvalidLoginRepository getInvalidLoginRepository() {
        return invalidLoginRepository;
    }

    public PartialPasswordRepository getPartialPasswordRepository() {
        return partialPasswordRepository;
    }


}
