package com.bai.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "PARTIAL_PASSWORD")
public class PartialPassword implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    @Column(name = "MASK", nullable = false)
    private String mask;
    @Column(name = "CURRENT")
    private boolean current;
    @Column(name = "LAST_USED")
    private LocalDateTime lastUsed;

    protected PartialPassword() {
    }

    public PartialPassword(User user, String mask) {
        this.user = user;
        this.mask = mask;
    }

    public PartialPassword(User user, String mask, LocalDateTime lastUsed) {
        this.user = user;
        this.mask = mask;
        this.lastUsed = lastUsed;
    }

    public PartialPassword(User user, String mask, boolean current) {
        this.user = user;
        this.mask = mask;
        this.current = current;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
}
