package com.bai.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginForm {

    @NotNull
    @Size(min = 2, max = 80)
    private String username;

    @NotNull
    @Size(min = 8, max = 16)
    private String password;

    public LoginForm() {
    }

    public LoginForm(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
