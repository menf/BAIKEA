package com.bai.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;
    @Column(name = "SALT", nullable = false)
    private String salt;
    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;
    @Column(name = "LAST_INVALID_LOGIN")
    private LocalDateTime lastInvalidLogin;
    @Column(name = "INVALID_LOGIN_ATTEMPTS")
    private int invalidLoginAttempts;
    @Column(name = "ATTEMPTS_TO_LOCK")
    private int attemptsToLock;

    protected User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, LocalDateTime lastLogin) {
        this.name = name;
        this.password = password;
        this.lastLogin = lastLogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getLastInvalidLogin() {
        return lastInvalidLogin;
    }

    public void setLastInvalidLogin(LocalDateTime lastInvalidLogin) {
        this.lastInvalidLogin = lastInvalidLogin;
    }

    public int getInvalidLoginAttempts() {
        return invalidLoginAttempts;
    }

    public void setInvalidLoginAttempts(int invalidLoginAttempts) {
        this.invalidLoginAttempts = invalidLoginAttempts;
    }

    public int getAttemptsToLock() {
        return attemptsToLock;
    }

    public void setAttemptsToLock(int attemptsToLock) {
        this.attemptsToLock = attemptsToLock;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', password='%s', lastLogin='%s']", id, name, password, lastLogin);
    }
}
