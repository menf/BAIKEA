package com.bai.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "MESSAGES")
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    @Column(name = "TEXT", nullable = false)
    private String text;
    @Column(name = "MODIFIED")
    private LocalDateTime modified;

    protected Message() {
    }

    public Message(User user, String text) {
        this.user = user;
        this.text = text;
    }

    public Message(User user, String text, LocalDateTime modified) {
        this.user = user;
        this.text = text;
        this.modified = modified;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return String.format("Message[id=%d, text='%s', modified='%s' user='%s']\n", id, text, modified, user);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message))
            return false;
        Message message = (Message) obj;
        return id == message.getId();
    }
}
