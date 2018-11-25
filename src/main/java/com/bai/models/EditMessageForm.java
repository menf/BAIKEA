package com.bai.models;

import javax.validation.constraints.Size;

public class EditMessageForm {

    @Size(min = 1, max = 255)
    private String messageText;
    private int messageId;
    private int userId;
    private int[] allowedUserId;

    public EditMessageForm(int messageId, @Size(min = 1, max = 255) String messageText, int userId) {
        this.messageText = messageText;
        this.messageId = messageId;
        this.userId = userId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int[] getAllowedUserId() {
        return allowedUserId;
    }

    public void setAllowedUserId(int[] allowedUserId) {
        this.allowedUserId = allowedUserId;
    }
}
