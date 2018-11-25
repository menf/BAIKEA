package com.bai.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ALLOWED_MESSAGES")
public class AllowedMessages implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private Message message;

    protected AllowedMessages() {
    }

    public AllowedMessages(User user, Message message) {
        this.user = user;
        this.message = message;
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", user, message);
    }
}
