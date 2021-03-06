package com.bai.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserForm {
    @Size(min = 2, max = 80, message = "Długość loginu: [2...80]")
    private String username;

    @NotNull
    @Size(min = 1, max = 50)
    private String password;

    @NotNull
    @Size(min = 1, max = 50)
    private String passwordConfirm;

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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
