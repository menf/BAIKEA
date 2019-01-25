package com.bai.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "INVALID_LOGIN")
public class InvalidLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;
    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;
    @Column(name = "ATTEMPTS")
    private int attempts;
    @Column(name = "LAST_ATTEMPT")
    private LocalDateTime lastAttempt;
    @Column(name = "LOCK_ATTEMPTS")
    private int lockAttempt;

    public InvalidLogin() {
    }

    public InvalidLogin(String username) {
        this.username = username;
    }

    public InvalidLogin(int id, String username, int attempts, LocalDateTime lastAttempt) {
        this.id = id;
        this.username = username;
        this.attempts = attempts;
        this.lastAttempt = lastAttempt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public LocalDateTime getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(LocalDateTime lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public int getLockAttempt() {
        return lockAttempt;
    }

    public void setLockAttempt(int lockAttempt) {
        this.lockAttempt = lockAttempt;
    }
}
