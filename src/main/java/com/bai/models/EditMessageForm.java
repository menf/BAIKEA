package com.bai.models;

import javax.validation.constraints.Size;

public class EditMessageForm {

    @Size(min = 1, max = 255)
    private String messageText;
    private String[] allowedUserId;

    public EditMessageForm(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String[] getAllowedUserId() {
        return allowedUserId;
    }

    public void setAllowedUserId(String[] allowedUserId) {
        this.allowedUserId = allowedUserId;
    }
}
